package umc.cozymate.ui.cozy_home.waiting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import umc.cozymate.databinding.FragmentCozyHomeEnteringBinding
import umc.cozymate.ui.cozy_home.make_room.CozyHomeInviteRoommateActivity

// 플로우1 : 방정보 입력창(1) > 룸메이트 선택창(2) > 룸메이트 대기창(3) > "코지홈 입장창(4)" > 코지홈 활성화창
class CozyHomeEnteringFragment : Fragment() {

    private var _binding: FragmentCozyHomeEnteringBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCozyHomeEnteringBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnNext.setOnClickListener {
            (activity as? CozyHomeInviteRoommateActivity)?.loadFragment5()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
