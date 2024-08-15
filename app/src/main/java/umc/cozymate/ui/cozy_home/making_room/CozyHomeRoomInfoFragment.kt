package umc.cozymate.ui.cozy_home.making_room

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import umc.cozymate.R
import umc.cozymate.databinding.FragmentCozyHomeRoomInfoBinding
import umc.cozymate.util.setupTextInputWithMaxLength

// 플로우1 : "방정보 입력창(1)" > 룸메이트 선택창(2) > 룸메이트 대기창(3) > 코지홈 입장창(4) > 코지홈 활성화창
class CozyHomeRoomInfoFragment : Fragment() {

    private var _binding: FragmentCozyHomeRoomInfoBinding? = null
    private val binding get() = _binding!!

    private var numPeopleOption: TextView? = null
    private var numPeople: String? = null

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
                (activity as? CozyHomeGivingInviteCodeActivity)?.loadFragment2()
            }

            var isSelected = false
            ivCharacter.setOnClickListener {
                val intent = Intent(context, CozyHomeSelectingCharacterActivity::class.java)
                startActivity(intent)
                isSelected = true
            }
            if (isSelected) ivCharacter.setImageResource(R.drawable.character_0)

            val numPeopleTexts = listOf(
                binding.chip1 to "2명",
                binding.chip2 to "3명",
                binding.chip3 to "4명",
                binding.chip4 to "5명",
                binding.chip5 to "6명",
            )
            for ((textView, value) in numPeopleTexts) {
                textView.setOnClickListener { numPeopleSelected(it, value) }
            }
        }

        checkValidInfo()
    }

    // 인원수 옵션 클릭
    private fun numPeopleSelected(view: View, value: String) {
        numPeopleOption?.apply {
            setTextColor(resources.getColor(R.color.unuse_font, null))
            background = resources.getDrawable(R.drawable.custom_option_box_background_default, null)
        }
        numPeopleOption = view as TextView
        numPeopleOption?.apply {
            setTextColor(resources.getColor(R.color.main_blue, null))
            background = resources.getDrawable(R.drawable.custom_option_box_background_selected_6dp)
        }
        numPeople = value
        saveNumPeople(value)
        // updateNextButtonState()
    }

    private fun saveNumPeople(value: String) {
        /*with(spf.edit()) {
            putString("num_people", value)
            apply()
        }*/
        Log.d("Basic Info", "Num People: $value")
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

