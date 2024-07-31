package umc.cozymate.ui.cozy_home.make_room

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import umc.cozymate.databinding.FragmentCozyHomeSelectRoommateBinding

// 플로우1 : 방정보 입력창(1) > "룸메이트 선택창(2)" > 룸메이트 대기창(3) > 코지홈 입장창(4) > 코지홈 활성화창
class CozyHomeSelectRoommateFragment : Fragment() {

    private var _binding: FragmentCozyHomeSelectRoommateBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCozyHomeSelectRoommateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            btnNext.setOnClickListener {
                (activity as? CozyHomeInviteRoommateActivity)?.loadFragment3()
            }
        }
    }
}