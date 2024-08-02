package umc.cozymate.ui.cozy_home.common

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import umc.cozymate.databinding.FragmentCozyHomeRoomInfoBinding
import umc.cozymate.ui.cozy_home.making_room.CozyHomeInvitingRoommateActivity
import umc.cozymate.util.setupTextInputWithMaxLength

// 플로우1 : "방정보 입력창(1)" > 룸메이트 선택창(2) > 룸메이트 대기창(3) > 코지홈 입장창(4) > 코지홈 활성화창
class CozyHomeRoomInfoFragment : Fragment() {

    private var _binding: FragmentCozyHomeRoomInfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCozyHomeRoomInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            btnNext.setOnClickListener {
                (activity as? CozyHomeInvitingRoommateActivity)?.loadFragment2()
                // (activity as? CozyHomeGivingInviteCodeActivity)?.loadFragment2()
            }

            ivCharacter.setOnClickListener {
                val intent = Intent(context, CozyHomeCharacterSelectionActivity::class.java)
                startActivity(intent)
            }
        }

        checkValidInfo()
    }

    // 방 이름 유효한지 체크
    private fun checkValidInfo() {
        with(binding) {
            setupTextInputWithMaxLength(
                textInputLayout = tilRoomName,
                textInputEditText = etRoomName,
                maxLength = 12,
                errorMessage = "방이름은 최대 12글자만 가능해요!"
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

