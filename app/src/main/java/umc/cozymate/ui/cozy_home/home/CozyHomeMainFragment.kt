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
import dagger.hilt.android.AndroidEntryPoint
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

        viewModel.fetchRoomIdIfNeeded()
        initState()
        initView()
        initListener()
        openMessage()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel()
        univViewModel.isMailVerified()
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
            btnSchoolCertificate.setOnClickListener {
                startActivity(Intent(activity, UniversityCertificationActivity::class.java))
            }
            if (state == UserRoomState.NO_ROOM || state == UserRoomState.REQUEST_SENT) {
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
        univViewModel.isVerified.observe(viewLifecycleOwner) { isVerified ->
            if (isVerified == true && univViewModel.university.value == null) {
                univViewModel.fetchMyUniversity()
            }
        }
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
    }
}