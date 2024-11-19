package umc.cozymate.ui.cozy_home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import umc.cozymate.databinding.FragmentCozyHomeMainBinding
import umc.cozymate.ui.message.MessageActivity
import umc.cozymate.ui.university_certification.UniversityCertificationActivity

class CozyHomeMainFragment : Fragment() {
    private var _binding: FragmentCozyHomeMainBinding? = null
    private val binding get() = _binding!!

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

    private fun openMessage(){
        binding.btnMessage.setOnClickListener {
            startActivity(Intent(activity, MessageActivity::class.java))
        }

    }
}