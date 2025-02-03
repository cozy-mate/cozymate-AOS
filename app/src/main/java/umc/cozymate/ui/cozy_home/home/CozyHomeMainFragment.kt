package umc.cozymate.ui.cozy_home.home

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
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
import umc.cozymate.ui.cozy_home.room.received_invitation.ReceivedInvitationComponent
import umc.cozymate.ui.cozy_home.room.received_join_request.ReceivedJoinRequestComponent
import umc.cozymate.ui.cozy_home.room.recommended_room.RecommendedRoomComponent
import umc.cozymate.ui.cozy_home.room.sent_join_request.SentJoinRequestComponent
import umc.cozymate.ui.cozy_home.roommate.recommended_roommate.RecommendedRoommateComponent
import umc.cozymate.ui.message.MessageMemberActivity
import umc.cozymate.ui.notification.NotificationActivity
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
        with(binding) {
            binding.refreshLayout.isRefreshing = true
            // 학교명
            // 학교인증 지금은 안 하도록 수정했습니다
            ivSchoolWhite.visibility = View.GONE
            ivSchoolBlue.visibility = View.VISIBLE
            ivNext.visibility = View.GONE
            tvSchoolName.text = "인하대학교"
            tvSchoolName.setTextColor(ContextCompat.getColor(requireContext(), R.color.main_blue))
            /*viewLifecycleOwner.lifecycleScope.launch {
                univViewModel.isMailVerified()
            }*/
            // 쪽지
            openMessage()
            // 알림
            openNotification()
            // 사용자 상태 (방/방장/
            initState()
            // 컴포넌트 초기화
            initView()
            // 새로고침 설정
            onRefresh()
            binding.refreshLayout.isRefreshing = false
        }
    }

    override fun onResume() {
        super.onResume()
        binding.refreshLayout.isRefreshing = true
        initState()
        initView()
        binding.refreshLayout.isRefreshing = false
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
            /*viewLifecycleOwner.lifecycleScope.launch {
                univViewModel.isMailVerified()
            }*/
            // 각 컴포넌트 새로고침
            val myRoom =
                childFragmentManager.findFragmentById(R.id.my_room_container) as? MyRoomComponent
            val receivedJoinRequest =
                childFragmentManager.findFragmentById(R.id.received_join_request_container) as? ReceivedJoinRequestComponent
            val receivedInvitation =
                childFragmentManager.findFragmentById(R.id.received_invitation_container) as? ReceivedInvitationComponent
            val sentJoinRequest =
                childFragmentManager.findFragmentById(R.id.sent_join_container) as? SentJoinRequestComponent
            val roommateRecommend =
                childFragmentManager.findFragmentById(R.id.recommended_roommate_container) as? RecommendedRoommateComponent
            val roomRecommend =
                childFragmentManager.findFragmentById(R.id.recommended_room_container) as? RecommendedRoomComponent
            myRoom?.refreshData()
            receivedJoinRequest?.refreshData()
            receivedInvitation?.refreshData()
            sentJoinRequest?.refreshData()
            roommateRecommend?.refreshData()
            roomRecommend?.refreshData()
            // isRefreshing = false 인 경우 새로고침 완료시 새로고침 아이콘이 사라집니다
            binding.refreshLayout.isRefreshing = false
        }
    }

    private fun initState() {
        val spf = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        roomId = spf.getInt("room_id", 0)
        splashViewmodel.memberCheck() // 멤버 정보 저장(닉네임 안 불러와지는 문제 해결을 위해 시도)
        // 방 정보 옵저빙
        // 방장인지 여부를 확인합니다.
        viewModel.roomInfoResponse.observe(viewLifecycleOwner) { roomInfo ->
            state = if (roomInfo != null) {
                if (roomInfo.result.isRoomManager) {
                    Log.d("tag", "$roomInfo")
                    UserRoomState.CREATED_ROOM
                } else {
                    UserRoomState.HAS_ROOM
                }
            } else {
                UserRoomState.NO_ROOM
            }
        }
        // 방 존재 여부를 spf로 조회한 방 아이디로 확인합니다.
        if (roomId == 0 || roomId == -1) {
            state = UserRoomState.NO_ROOM
        } else {
            state = UserRoomState.HAS_ROOM
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.fetchRoomInfo()
            }
        }
    }

    // 사용자의 방없음/방장/방참여 상태에 따른 컴포넌트 띄우기
    private fun initView() {
        with(binding) {
            initRoomButtonListener()
            when (state) {
                UserRoomState.NO_ROOM -> {
                    // 보이는 컴포넌트
                    receivedInvitationContainer.visibility = View.VISIBLE
                    recommendedRoomContainer.visibility = View.VISIBLE
                    recommendedRoommateContainer.visibility = View.VISIBLE
                    sentJoinContainer.visibility = View.VISIBLE
                    parentFragmentManager.beginTransaction().apply {
                        replace(R.id.received_invitation_container, ReceivedInvitationComponent())
                        replace(R.id.recommended_room_container, RecommendedRoomComponent())
                        replace(R.id.recommended_roommate_container, RecommendedRoommateComponent())
                        replace(R.id.sent_join_container, SentJoinRequestComponent())
                        commit()
                    }
                    // 안 보이는 컴포넌트
                    myRoomContainer.visibility = View.GONE
                    receivedJoinRequestContainer.visibility = View.GONE
                }

                UserRoomState.HAS_ROOM -> {
                    // 보이는 컴포넌트
                    myRoomContainer.visibility = View.VISIBLE
                    receivedInvitationContainer.visibility = View.VISIBLE
                    recommendedRoomContainer.visibility = View.VISIBLE
                    recommendedRoommateContainer.visibility = View.VISIBLE
                    receivedJoinRequestContainer.visibility = View.VISIBLE
                    parentFragmentManager.beginTransaction().apply {
                        replace(R.id.my_room_container, MyRoomComponent())
                        replace(R.id.received_invitation_container, ReceivedInvitationComponent())
                        replace(R.id.recommended_room_container, RecommendedRoomComponent())
                        replace(R.id.recommended_roommate_container, RecommendedRoommateComponent())
                        replace(R.id.received_join_request_container, ReceivedJoinRequestComponent())
                        commit()
                    }
                    // 안 보이는 컴포넌트
                    sentJoinContainer.visibility = View.GONE
                }

                UserRoomState.CREATED_ROOM -> {
                    // 보이는 컴포넌트
                    myRoomContainer.visibility = View.VISIBLE
                    receivedInvitationContainer.visibility = View.VISIBLE
                    recommendedRoomContainer.visibility = View.VISIBLE
                    recommendedRoommateContainer.visibility = View.VISIBLE
                    receivedJoinRequestContainer.visibility = View.VISIBLE
                    parentFragmentManager.beginTransaction().apply {
                        replace(R.id.my_room_container, MyRoomComponent())
                        replace(R.id.received_invitation_container, ReceivedInvitationComponent())
                        replace(R.id.recommended_room_container, RecommendedRoomComponent())
                        replace(R.id.recommended_roommate_container, RecommendedRoommateComponent())
                        replace(R.id.received_join_request_container, ReceivedJoinRequestComponent())
                        commit()
                    }
                    // 안 보이는 컴포넌트
                    sentJoinContainer.visibility = View.GONE
                }
            }
        }
    }

    private fun initRoomButtonListener() {
        with(binding) {
            // 방 생성 버튼 > 팝업
            btnMakeRoom.setOnClickListener {
                startActivity(Intent(requireContext(), MakingRoomDialogFragment::class.java))
            }
            // 방 참여 버튼
            btnEnterRoom.setOnClickListener {
                startActivity(Intent(activity, JoinRoomActivity::class.java))
            }
            // 학교 버튼
            /*btnSchoolCertificate.setOnClickListener {
                val intent = Intent(activity, UniversityCertificationActivity::class.java)
                intent.putExtra(UniversityCertificationActivity.UNIVERSITY_FLAG, universityFlag)
                startActivity(intent)
            }*/
            // 방장/방참여 사용자는 버튼 비활성화
            if (state == UserRoomState.HAS_ROOM || state == UserRoomState.CREATED_ROOM) {
                btnMakeRoom.isEnabled = false
                btnEnterRoom.isEnabled = false
                btnMakeRoom.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.unuse_font
                    )
                )
                btnEnterRoom.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.unuse_font
                    )
                )
            } else {
                btnMakeRoom.isEnabled = true
                btnEnterRoom.isEnabled = true
                btnMakeRoom.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.main_blue
                    )
                )
                btnEnterRoom.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.main_blue
                    )
                )
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

    // 학교 인증 / 학교명 옵저빙
    private fun observeUnivViewModel() {
        univViewModel.university.observe(viewLifecycleOwner) { univ ->
            with(binding) {
                tvSchoolName.text = univ
                // 학교인증 x
                if (univ == "학교 인증을 해주세요") {
                    universityFlag = false
                    ivSchoolWhite.visibility = View.VISIBLE
                    ivSchoolBlue.visibility = View.GONE
                    ivNext.visibility = View.VISIBLE
                }
                universityFlag = true
                ivSchoolWhite.visibility = View.GONE
                ivSchoolBlue.visibility = View.VISIBLE
                ivNext.visibility = View.GONE
                tvSchoolName.text = "인하대학교"
                tvSchoolName.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.main_blue
                    )
                )
            }
        }
        // 메일인증 여부가 확인되면, 사용자 대학교를 조회한다.
        univViewModel.isVerified.observe(viewLifecycleOwner) { isVerified ->
            if (isVerified == true && univViewModel.university.value == null) {
                viewLifecycleOwner.lifecycleScope.launch {
                    // univViewModel.fetchMyUniversityIfNeeded()
                }
            }
        }
    }
}