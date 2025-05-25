package umc.cozymate.ui.cozy_home.home

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.google.firebase.analytics.logEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import umc.cozymate.R
import umc.cozymate.databinding.FragmentCozyHomeMainBinding
import umc.cozymate.ui.cozy_home.room.join_room.JoinRoomActivity
import umc.cozymate.ui.cozy_home.room.making_room.MakingPublicRoomActivity
import umc.cozymate.ui.message.MessageMemberActivity
import umc.cozymate.ui.notification.NotificationActivity
import umc.cozymate.ui.roommate.RoommateOnboardingActivity
import umc.cozymate.ui.viewmodel.CozyHomeViewModel
import umc.cozymate.ui.viewmodel.SplashViewModel
import umc.cozymate.util.AnalyticsConstants
import umc.cozymate.util.AnalyticsConstants.*
import umc.cozymate.util.AnalyticsEventLogger
import umc.cozymate.util.PreferencesUtil.KEY_IS_LIFESTYLE_EXIST
import umc.cozymate.util.PreferencesUtil.PREFS_NAME
import umc.cozymate.util.StatusBarUtil

@AndroidEntryPoint
class CozyHomeFragment : Fragment() {
    private val TAG = this.javaClass.simpleName
    private val UPDATE_REQUEST_CODE = 1001
    private val updateManager by lazy { AppUpdateManagerFactory.create(requireActivity()) }
    private var _binding: FragmentCozyHomeMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CozyHomeViewModel by viewModels()
    private val splashViewmodel: SplashViewModel by viewModels()
    private var roomId: Int = 0
    private var isLifestyleExist: Boolean = false
    private var isRoomExist = false
    private var isRoomManager = false
    private var screenEnterTime: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        Main: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCozyHomeMainBinding.inflate(inflater, Main, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        StatusBarUtil.updateStatusBarColor(requireActivity(), Color.WHITE)
        binding.refreshLayout.isRefreshing = true
        setName()
        setMessageBtn()
        setNotificationBtn()
        initUserState()
        setOnRefreshListener()
        // 업데이트 체크
        checkForUpdate()
        binding.refreshLayout.isRefreshing = false

        val prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val allEntries = prefs.all
        for ((key, value) in allEntries) {
            Log.d("SharedPreferences", "Key: $key, Value: $value")
        }
    }

    override fun onResume() {
        super.onResume()
        screenEnterTime = System.currentTimeMillis()
        checkForUpdate()
        binding.refreshLayout.isRefreshing = true
        setName()
        setMessageBtn()
        setNotificationBtn()
        initUserState()
        binding.refreshLayout.isRefreshing = false
    }

    override fun onPause() {
        super.onPause()
        val screenLeaveTime = System.currentTimeMillis()
        val sessionDuration = screenLeaveTime - screenEnterTime // 밀리초 단위

        // GA 이벤트 로그 추가
        AnalyticsEventLogger.logEvent(
            eventName = AnalyticsConstants.Event.HOME_SESSION_TIME,
            category = AnalyticsConstants.Category.HOME_CONTENT,
            action = AnalyticsConstants.Action.SESSION_TIME,
            label = null,
            duration = sessionDuration
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setName() {
        splashViewmodel.memberCheck()
        val nickname = viewModel.getNickname().toString()
        val univName = viewModel.getUnivName().toString()
        binding.btnLifestyle.text = "${nickname}님, 라이프스타일을 입력하고\n나와 꼭 맞는 룸메이트를 찾아볼까요?"
        binding.tvUnivName.text = univName
        binding.tvUnivName.setTextColor(ContextCompat.getColor(requireContext(), R.color.main_blue))
    }

    private fun setMessageBtn() {
        binding.btnMessage.setOnClickListener {
            // GA 이벤트 로그 추가
            AnalyticsEventLogger.logEvent(
                eventName = Event.BUTTON_CLICK_CHAT,
                category = Category.HOME_HEADER,
                action = Action.BUTTON_CLICK,
                label = Label.CHAT,
            )

            startActivity(Intent(activity, MessageMemberActivity::class.java))
        }
    }

    private fun setNotificationBtn() {
        binding.btnNotification.setOnClickListener {
            // GA 이벤트 로그 추가
            AnalyticsEventLogger.logEvent(
                eventName = Event.BUTTON_CLICK_NOTICE,
                category = Category.HOME_HEADER,
                action = Action.BUTTON_CLICK,
                label = Label.NOTICE,
            )

            startActivity(Intent(activity, NotificationActivity::class.java))
        }
    }

    private fun initUserState() {
        observeUserState()
        setContents()
    }

    private fun observeUserState() {
        val spf = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        isLifestyleExist = spf.getBoolean(KEY_IS_LIFESTYLE_EXIST, false)
        if (!isLifestyleExist) {
            binding.btnLifestyle.visibility = View.VISIBLE
            binding.btnLifestyle.isEnabled = true
            binding.btnMakeRoom.isEnabled = false
            binding.btnEnterRoom.isEnabled = false
            binding.btnMakeRoom.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.unuse_font
                )
            )
            binding.btnEnterRoom.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.unuse_font
                )
            )
            binding.btnLifestyle.setOnClickListener() {
                // GA 이벤트 로그 추가
                AnalyticsEventLogger.logEvent(
                    eventName = Event.BUTTON_CLICK_LIFESTYLE,
                    category = Category.HOME_HEADER,
                    action = Action.BUTTON_CLICK,
                    label = Label.LIFE_STYLE,
                )

                val intent = Intent(requireContext(), RoommateOnboardingActivity::class.java)
                startActivity(intent)
            }
        } else {
            binding.btnLifestyle.visibility = View.GONE
        }
        viewModel.roomInfoResponse.observe(viewLifecycleOwner) { roomInfo ->
            if (roomInfo != null) {
                roomId = roomInfo.result.roomId
                isRoomExist = true
                isRoomManager = roomInfo.result.isRoomManager
                Log.d(TAG, "is room manager: ${isRoomManager}")
                if (isRoomManager) {
                    // 방장 뷰
                    parentFragmentManager.beginTransaction().apply {
                        replace(
                            R.id.container_cozyhome_content,
                            CozyHomeContentRoomManagerFragment()
                        )
                        commit()
                    }
                } else {
                    // 방 매칭 후 뷰
                    parentFragmentManager.beginTransaction().apply {
                        replace(
                            R.id.container_cozyhome_content,
                            CozyHomeContentAfterMatchingFragment()
                        )
                        commit()
                    }
                }
                binding.btnMakeRoom.isEnabled = false
                binding.btnEnterRoom.isEnabled = false
                binding.btnMakeRoom.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.unuse_font
                    )
                )
                binding.btnEnterRoom.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.unuse_font
                    )
                )
            }
        }
        roomId = spf.getInt("room_id", 0)
        if ((roomId == 0 || roomId == -1) && isLifestyleExist) {
            isRoomExist = false
            binding.btnMakeRoom.isEnabled = true
            binding.btnEnterRoom.isEnabled = true
            setRoomBtns()
            binding.btnMakeRoom.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.main_blue
                )
            )
            binding.btnEnterRoom.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.main_blue
                )
            )
        } else {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.fetchRoomInfo() // rooms/{roomId}
            }
        }
    }

    private fun setRoomBtns() {
        binding.btnMakeRoom.setOnClickListener {
            // GA 이벤트 로그 추가
            AnalyticsEventLogger.logEvent(
                eventName = Event.BUTTON_CLICK_MAKE_ROOM,
                category = Category.HOME_HEADER,
                action = Action.BUTTON_CLICK,
                label = Label.MAKE_ROOM,
            )

            startActivity(Intent(requireContext(), MakingPublicRoomActivity::class.java))
        }
        binding.btnEnterRoom.setOnClickListener {
            // GA 이벤트 로그 추가
            AnalyticsEventLogger.logEvent(
                eventName = Event.BUTTON_CLICK_JOIN_ROOM,
                category = Category.HOME_HEADER,
                action = Action.BUTTON_CLICK,
                label = Label.JOIN_ROOM,
            )

            startActivity(Intent(activity, JoinRoomActivity::class.java))
        }
    }

    private fun setContents() {
        if (!isLifestyleExist) {
            // 기본 뷰
            parentFragmentManager.beginTransaction().apply {
                replace(R.id.container_cozyhome_content, CozyHomeContentDefaultFragment())
                commit()
            }
        } else {
            if (!isRoomExist) {
                // 방 매칭 전 뷰
                parentFragmentManager.beginTransaction().apply {
                    replace(
                        R.id.container_cozyhome_content,
                        CozyHomeContentBeforeMatchingFragment()
                    )
                    commit()
                }
            }
        }
    }


    private fun setOnRefreshListener() {
        binding.refreshLayout.setOnRefreshListener {
            initUserState()
            val defaultFragment =
                childFragmentManager.findFragmentById(R.id.container_cozyhome_content) as? CozyHomeContentDefaultFragment
            val roomManagerFragment =
                childFragmentManager.findFragmentById(R.id.container_cozyhome_content) as? CozyHomeContentRoomManagerFragment
            val beforeMatchingFragment =
                childFragmentManager.findFragmentById(R.id.container_cozyhome_content) as? CozyHomeContentBeforeMatchingFragment
            val afterMatchingFragment =
                childFragmentManager.findFragmentById(R.id.container_cozyhome_content) as? CozyHomeContentAfterMatchingFragment
            if (!isLifestyleExist) {
                defaultFragment?.refreshData()
            } else {
                if (!isRoomExist) {
                    parentFragmentManager.beginTransaction().apply {
                        beforeMatchingFragment?.refreshData()
                    }
                } else {
                    if (isRoomManager) {
                        roomManagerFragment?.refreshData()
                    } else {
                        afterMatchingFragment?.refreshData()
                    }
                }
            }
            binding.refreshLayout.isRefreshing = false
        }
    }

    // 새로운 앱 버전이 있는지 확인합니다.
    private fun checkForUpdate() {
        val appUpdateInfoTask = updateManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                AlertDialog.Builder(requireContext())
                    .setTitle("")
                    .setMessage("새로운 버전이 출시되었습니다. 업데이트하시겠습니까?")
                    .setPositiveButton("확인") { _, _ ->
                        try {
                            // 강제 업데이트 요청
                            //updateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE, requireActivity(), UPDATE_REQUEST_CODE )
                            // 구글 플레이스토어로 리다이렉트
                            startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("market://details?id=umc.cozymate")
                                )
                            )
                        } catch (e: ActivityNotFoundException) {
                            startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://play.google.com/apps/test/umc.cozymate")
                                )
                            )
                        }
                    }
                    .setNegativeButton("나중에") { dialog, _ -> dialog.dismiss() }
                    .show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UPDATE_REQUEST_CODE && resultCode != Activity.RESULT_OK) {
            // 업데이트 실패 처리
            Toast.makeText(requireActivity(), "업데이트 실패", Toast.LENGTH_SHORT).show()
        }
    }
}