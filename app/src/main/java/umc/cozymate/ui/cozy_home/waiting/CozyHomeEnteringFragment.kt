package umc.cozymate.ui.cozy_home.waiting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import umc.cozymate.databinding.FragmentCozyHomeEnteringBinding
import umc.cozymate.ui.cozy_home.entering_room.CozyHomeEnteringInviteCodeActivity
import umc.cozymate.ui.cozy_home.making_room.CozyHomeGivingInviteCodeActivity
import umc.cozymate.ui.cozy_home.making_room.CozyHomeInvitingRoommateActivity

// 플로우1 : 방정보 입력창(1) > 룸메이트 선택창(2) > 룸메이트 대기창(3) > "코지홈 입장창(4)" > 코지홈 활성화창
// 플로우2 : 방정보 입력창(1) > 초대코드 발급창(2) > 룸메이트 대기창(3) > "코지홈 입장창(4)" > 코지홈 활성화창
// 플로우3 : "초대코드 입력창(1)" > "룸메이트 대기창(2)" > 코지홈 입장창(3) > 코지홈 활성화창

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
            (activity as? CozyHomeInvitingRoommateActivity)?.loadFragment5()
            (activity as? CozyHomeGivingInviteCodeActivity)?.loadFragment5()
            (activity as? CozyHomeEnteringInviteCodeActivity)?.loadFragment4()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
