package umc.cozymate.ui.cozy_home.home

import android.content.Context
import android.content.Intent
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
import umc.cozymate.ui.message.MessageActivity
import umc.cozymate.ui.university_certification.UniversityCertificationActivity
import umc.cozymate.ui.university_certification.UniversityViewModel
import umc.cozymate.ui.viewmodel.CozyHomeViewModel

@AndroidEntryPoint
class CozyHomeMainFragment : Fragment() {
    private var _binding: FragmentCozyHomeMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CozyHomeViewModel by viewModels()
    private val univViewModel: UniversityViewModel by viewModels()
    private var roomId: Int = 0
    private var state: UserRoomState = UserRoomState.NO_ROOM
    private var isCertificated: Boolean = false

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
        initState()
        initView()
        initListener()
        openMessage()
        viewLifecycleOwner.lifecycleScope.launch {
            if (univViewModel.isVerified.value == false) {
                univViewModel.isMailVerified()
            }
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
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
                    roomRecommendContainer.visibility = View.VISIBLE
                    roommateRecommendContainer.visibility = View.VISIBLE
                }

                UserRoomState.CREATED_ROOM -> {
                    myRoomContainer.visibility = View.VISIBLE
                    roomRecommendContainer.visibility = View.VISIBLE
                    roommateRecommendContainer.visibility = View.VISIBLE
                    roommateRequestContainer.visibility = View.VISIBLE
                }

                UserRoomState.REQUEST_SENT -> {
                    roomRecommendContainer.visibility = View.VISIBLE
                    roommateRecommendContainer.visibility = View.VISIBLE
                }

                UserRoomState.HAS_ROOM -> {
                    myRoomContainer.visibility = View.VISIBLE
                    roomRecommendContainer.visibility = View.VISIBLE
                    roommateRecommendContainer.visibility = View.VISIBLE
                }
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
            // 학교 버튼
            btnSchoolCertificate.setOnClickListener {
                startActivity(Intent(activity, UniversityCertificationActivity::class.java))
            }
            // 방장/방참여 사용자는 버튼 비활성화
            if (state == UserRoomState.HAS_ROOM|| state == UserRoomState.CREATED_ROOM) {
                btnMakeRoom.isEnabled = false
                btnEnterRoom.isEnabled = false
                btnMakeRoom.setTextColor(ContextCompat.getColor(requireContext(), R.color.unuse_font))
                btnEnterRoom.setTextColor(ContextCompat.getColor(requireContext(), R.color.unuse_font))
            }
        }
    }

    private fun openMessage() {
        binding.btnMessage.setOnClickListener {
            startActivity(Intent(activity, MessageActivity::class.java))
        }

    }

    private fun observeViewModel() {
        univViewModel.university.observe(viewLifecycleOwner) { univ ->
            with(binding) {
                tvSchoolName.text = univ
                if (univ == "학교 인증을 해주세요") {
                    ivSchoolWhite.visibility = View.VISIBLE
                    ivSchoolBlue.visibility = View.GONE
                    ivNext.visibility = View.VISIBLE
                } else {
                    ivSchoolWhite.visibility = View.GONE
                    ivSchoolBlue.visibility = View.VISIBLE
                    ivNext.visibility = View.GONE
                    tvSchoolName.setTextColor(ContextCompat.getColor(requireContext(), R.color.main_blue))
                    btnSchoolCertificate.isEnabled = false
                // btnSchoolCertificate.setOnClickListener(null)
                }
            }
        }
        univViewModel.isVerified.observe(viewLifecycleOwner) { isVerified ->
            if (isVerified == true && univViewModel.university.value == null) {
                viewLifecycleOwner.lifecycleScope.launch {
                    univViewModel.fetchMyUniversityIfNeeded()
                }
            }
        }
    }
}