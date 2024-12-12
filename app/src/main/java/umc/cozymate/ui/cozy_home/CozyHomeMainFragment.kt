package umc.cozymate.ui.cozy_home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import umc.cozymate.databinding.FragmentCozyHomeMainBinding
import umc.cozymate.ui.cozy_home.roommate_detail.CozyHomeRoommateDetailActivity
import umc.cozymate.ui.message.MessageActivity
import umc.cozymate.ui.university_certification.UniversityCertificationActivity


class CozyHomeMainFragment : Fragment() {
    private var _binding: FragmentCozyHomeMainBinding? = null
    private val binding get() = _binding!!
    //private val viewModel: CozyHomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        Main: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCozyHomeMainBinding.inflate(inflater, Main, false)

        initView()
        initListener()
        openMessage()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initView() {
        /*viewModel.fetchRoomIdIfNeeded()
        val savedRoomId = viewModel.getSavedRoomId()
        if (savedRoomId == 0) {
            // SharedPreferences에 방 ID가 저장되어 있지 않다면 getRoomId 호출
            viewModel.getRoomId()
        } else {
            // 방 ID가 이미 저장되어 있다면 roomId에 값을 설정
            viewModel.setRoomId(savedRoomId)
        }

        viewModel.roomId.observe(viewLifecycleOwner) { id ->
            if (id != null && id != 0) {
                // 방 ID가 null이 아니면 방 정보를 가져옴
                // observeViewModel()
            }
        }*/
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

        binding.btnTestRoommate.setOnClickListener {
            startActivity(Intent(activity, CozyHomeRoommateDetailActivity::class.java))
        }
    }

    private fun openMessage(){
        binding.btnMessage.setOnClickListener {
            startActivity(Intent(activity, MessageActivity::class.java))
        }

    }
}