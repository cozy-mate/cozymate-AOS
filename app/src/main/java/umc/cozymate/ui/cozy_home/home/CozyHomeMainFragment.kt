package umc.cozymate.ui.cozy_home.home

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import umc.cozymate.R
import umc.cozymate.data.domain.UserRoomState
import umc.cozymate.databinding.FragmentCozyHomeMainBinding
import umc.cozymate.ui.cozy_home.room.join_room.JoinRoomActivity
import umc.cozymate.ui.cozy_home.room.making_room.MakingRoomDialogFragment
import umc.cozymate.ui.cozy_home.room.my_room.MyRoomComponent
import umc.cozymate.ui.cozy_home.room.received_request.MyReceivedRequestComponent
import umc.cozymate.ui.cozy_home.room.room_recommend.RoomRecommendComponent
import umc.cozymate.ui.cozy_home.room.sent_request.MySentRequestComponent
import umc.cozymate.ui.cozy_home.roommate.roommate_recommend.RoommateRecommendComponent
import umc.cozymate.ui.message.MessageMemberActivity
import umc.cozymate.ui.notification.NotificationActivity
import umc.cozymate.ui.university_certification.UniversityCertificationActivity
import umc.cozymate.ui.viewmodel.CozyHomeViewModel
import umc.cozymate.ui.viewmodel.SplashViewModel
import umc.cozymate.ui.viewmodel.UniversityViewModel
import umc.cozymate.util.StatusBarUtil

@AndroidEntryPoint
class CozyHomeMainFragment : Fragment() {
    private var _binding: FragmentCozyHomeMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CozyHomeViewModel by viewModels()
    private val univViewModel: UniversityViewModel by viewModels()
    private val splashViewmodel: SplashViewModel by viewModels()
    private var roomId: Int = 0
    private var state: UserRoomState = UserRoomState.NO_ROOM
    private var universityFlag: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        Main: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCozyHomeMainBinding.inflate(inflater, Main, false)
        StatusBarUtil.updateStatusBarColor(requireActivity(), Color.WHITE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initState()
        initView()
        initListener()
        onRefresh()
        openMessage()
        openNotification()
        splashViewmodel.memberCheck() // 멤버 정보 저장(닉네임 안 불러와지는 문제 해결을 위해 시도)
        viewLifecycleOwner.lifecycleScope.launch {
            univViewModel.isMailVerified()
        }
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        activity?.intent?.let { intent ->
            val isRoomExist = intent.getBooleanExtra("isRoomExist", true)
            val isRoomManager = intent.getBooleanExtra("isRoomManager", true)

            // 전달된 값을 기반으로 UI 업데이트
            if (!isRoomExist) {
                initState()
            } else if (isRoomExist == true) {
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.getRoomId()
                    initState()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onRefresh() {
        // SwipeRefreshLayout OnRefreshListener를 등록합니다
        binding.refreshLayout.setOnRefreshListener {
            initState()
            initView()
            splashViewmodel.memberCheck() // 멤버 정보 저장(닉네임 안 불러와지는 문제 해결을 위해 시도)
            viewLifecycleOwner.lifecycleScope.launch {
                univViewModel.isMailVerified()
            }
            // 각 컴포넌트 새로고침
            val myRoomComponent = childFragmentManager.findFragmentById(R.id.my_room_container) as? MyRoomComponent
            val requestedRoommateComponent = childFragmentManager.findFragmentById(R.id.requested_roommate_container) as? MyReceivedRequestComponent
            val requestedRoomComponent = childFragmentManager.findFragmentById(R.id.requested_room_container) as? MySentRequestComponent
            val roommateRecommendComponent = childFragmentManager.findFragmentById(R.id.roommate_recommend_container) as? RoommateRecommendComponent
            val roomRecommendComponent = childFragmentManager.findFragmentById(R.id.room_recommend_container) as? RoomRecommendComponent
            myRoomComponent?.refreshData()
            requestedRoommateComponent?.refreshData()
            requestedRoomComponent?.refreshData()
            roommateRecommendComponent?.refreshData()
            roomRecommendComponent?.refreshData()
            // isRefreshing = false 인 경우 새로고침 완료시 새로고침 아이콘이 사라집니다
            binding.refreshLayout.isRefreshing = false
        }
    }

    private fun initState() {
        val spf = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        roomId = spf.getInt("room_id", 0)

        if (roomId == 0) {
            state = UserRoomState.NO_ROOM
        } else {
            state = UserRoomState.HAS_ROOM
        }
    }

    // 사용자의 방없음/방장/초대요청보냄/방참여 상태에 따른 컴포넌트 띄우기
    private fun initView() {
        with(binding) {
            when (state) {
                UserRoomState.NO_ROOM -> {
                    myRoomContainer.visibility = View.GONE
                    //requestedRoommateContainer.visibility = View.GONE <- 요청 있을 때만
                    requestedRoomContainer.visibility = View.VISIBLE
                    roomRecommendContainer.visibility = View.VISIBLE
                    roommateRecommendContainer.visibility = View.VISIBLE
                }

                UserRoomState.HAS_ROOM -> {
                    myRoomContainer.visibility = View.VISIBLE
                    requestedRoommateContainer.visibility = View.VISIBLE
                    requestedRoomContainer.visibility = View.GONE
                    roomRecommendContainer.visibility = View.VISIBLE
                    roommateRecommendContainer.visibility = View.VISIBLE
                }

                UserRoomState.REQUEST_SENT -> {}
                UserRoomState.CREATED_ROOM -> {}
            }
        }

    }

    private fun initListener() {
        with(binding) {
            // 방 생성 버튼 > 팝업
            btnMakeRoom.setOnClickListener {
                val popup: DialogFragment = MakingRoomDialogFragment()
                popup.show(childFragmentManager, "팝업")
            }
            // 방 참여 버튼
            btnEnterRoom.setOnClickListener {
                startActivity(Intent(activity, JoinRoomActivity::class.java))
            }
            // 학교 버튼
            btnSchoolCertificate.setOnClickListener {
                val intent = Intent(activity, UniversityCertificationActivity::class.java)
                intent.putExtra(UniversityCertificationActivity.UNIVERSITY_FLAG, universityFlag)
                startActivity(intent)
            }
            // 방장/방참여 사용자는 버튼 비활성화
            if (state == UserRoomState.HAS_ROOM || state == UserRoomState.CREATED_ROOM) {
                btnMakeRoom.isEnabled = false
                btnEnterRoom.isEnabled = false
                btnMakeRoom.setTextColor(ContextCompat.getColor(requireContext(), R.color.unuse_font))
                btnEnterRoom.setTextColor(ContextCompat.getColor(requireContext(), R.color.unuse_font))
            }
        }
    }

    private fun openMessage() {
        binding.btnMessage.setOnClickListener {
            startActivity(Intent(activity, MessageMemberActivity::class.java))
        }
    }

    private fun openNotification() {
        binding.btnBell.setOnClickListener {
            startActivity(Intent(activity, NotificationActivity::class.java))
        }
    }

    private fun observeViewModel() {
        univViewModel.university.observe(viewLifecycleOwner) { univ ->
            with(binding) {
                tvSchoolName.text = univ
                if (univ == "학교 인증을 해주세요") {
                    universityFlag = false
                    ivSchoolWhite.visibility = View.VISIBLE
                    ivSchoolBlue.visibility = View.GONE
                    ivNext.visibility = View.VISIBLE
                } else {
                    universityFlag = true
                    ivSchoolWhite.visibility = View.GONE
                    ivSchoolBlue.visibility = View.VISIBLE
                    ivNext.visibility = View.GONE
                    tvSchoolName.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.main_blue
                        )
                    )
                }
            }
        }
        // 메일인증 여부가 확인되면, 사용자 대학교를 조회한다.
        univViewModel.isVerified.observe(viewLifecycleOwner) { isVerified ->
            if (isVerified == true && univViewModel.university.value == null) {
                viewLifecycleOwner.lifecycleScope.launch {
                    univViewModel.fetchMyUniversityIfNeeded()
                }
            }
        }
    }
}