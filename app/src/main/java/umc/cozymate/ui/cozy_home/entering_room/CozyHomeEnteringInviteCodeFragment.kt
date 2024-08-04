package umc.cozymate.ui.cozy_home.entering_room

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import umc.cozymate.databinding.FragmentCozyHomeEnteringInviteCodeBinding

// 플로우3 : "초대코드 입력창(1)" > 룸메이트 대기창(2) > 코지홈 입장창(3) > 코지홈 활성화창
class CozyHomeEnteringInviteCodeFragment : Fragment() {

    private var _binding: FragmentCozyHomeEnteringInviteCodeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCozyHomeEnteringInviteCodeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            btnNext.setOnClickListener {
                (activity as? CozyHomeEnteringInviteCodeActivity)?.loadFragment2()
            }
        }
    }
}