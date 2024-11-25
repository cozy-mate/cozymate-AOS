package umc.cozymate.ui.cozy_home.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.data.domain.UserRoomState
import umc.cozymate.databinding.FragmentCozyHomeMainBinding
import umc.cozymate.ui.message.MessageActivity
import umc.cozymate.ui.university_certification.UniversityCertificationActivity
import umc.cozymate.ui.viewmodel.CozyHomeViewModel

@AndroidEntryPoint
class CozyHomeMainFragment : Fragment() {
    private var _binding: FragmentCozyHomeMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CozyHomeViewModel by activityViewModels()
    private var roomId: Int = 0
    private var state: UserRoomState = UserRoomState.NO_ROOM

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
        // 방 생성 버튼 > 팝업
        binding.btnMakeRoom.setOnClickListener {
            val popup: DialogFragment = MakingRoomDialogFragment()
            popup.show(childFragmentManager, "팝업")
        }
        // 학교 인증
        binding.btnSchoolCertificate.setOnClickListener {
            startActivity(Intent(activity, UniversityCertificationActivity::class.java))
        }
    }

    private fun openMessage() {
        binding.btnMessage.setOnClickListener {
            startActivity(Intent(activity, MessageActivity::class.java))
        }

    }
}