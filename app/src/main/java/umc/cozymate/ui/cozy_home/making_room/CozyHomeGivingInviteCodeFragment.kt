package umc.cozymate.ui.cozy_home.making_room

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.databinding.FragmentCozyHomeGivingInviteCodeBinding

// 플로우2 : 방정보 입력창(1) > "초대코드 발급창(2)" > 룸메이트 대기창(3) > 코지홈 입장창(4) > 코지홈 활성화창
@AndroidEntryPoint
class CozyHomeGivingInviteCodeFragment : Fragment() {

    private var _binding: FragmentCozyHomeGivingInviteCodeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCozyHomeGivingInviteCodeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            btnNext.setOnClickListener {
                (activity as? CozyHomeGivingInviteCodeActivity)?.loadFragment3()
            }
        }
    }
}