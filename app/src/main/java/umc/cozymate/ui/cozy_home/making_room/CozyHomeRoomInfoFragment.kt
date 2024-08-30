package umc.cozymate.ui.cozy_home.making_room

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.R
import umc.cozymate.databinding.FragmentCozyHomeRoomInfoBinding
import umc.cozymate.ui.cozy_home.making_room.view_model.MakingRoomViewModel
import umc.cozymate.util.setupTextInputWithMaxLength

// 플로우1 : "방정보 입력창(1)" > 룸메이트 선택창(2) > 룸메이트 대기창(3) > 코지홈 입장창(4) > 코지홈 활성화창
@AndroidEntryPoint
class CozyHomeRoomInfoFragment : Fragment() {

    private var _binding: FragmentCozyHomeRoomInfoBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MakingRoomViewModel

    private var numPeopleOption: TextView? = null
    private var numPeople: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCozyHomeRoomInfoBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(requireActivity())[MakingRoomViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            btnNext.setOnClickListener {
                // 방 이름과 최대 인원 수를 ViewModel에 설정
                val roomName = etRoomName.text.toString()
                val maxNum = numPeople?.filter { it.isDigit() }?.toInt() ?: 6 // 인원 수 숫자만 추출
                val characterImage = 1 // 기본 캐릭터 이미지 (선택된 값이 없으므로 0으로 설정)

                if (roomName.isNotEmpty() && maxNum > 0) {
                    viewModel.setNickname(roomName)
                    viewModel.setMaxNum(maxNum)
                    viewModel.setImg(characterImage)

                    // 방 생성 요청
                    viewModel.createRoom()
                } else {
                    Toast.makeText(context, "방 이름과 인원 수를 확인해주세요.", Toast.LENGTH_SHORT).show()
                }

                // (activity as? CozyHomeInvitingRoommateActivity)?.loadFragment2()
                (activity as? CozyHomeGivingInviteCodeActivity)?.loadFragment2()
            }

            var isSelected = false
            ivCharacter.setOnClickListener {
                val intent = Intent(context, CozyHomeSelectingCharacterActivity::class.java)
                startActivity(intent)
                isSelected = true
            }
            if (isSelected) ivCharacter.setImageResource(R.drawable.character_1)

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
            background =
                resources.getDrawable(R.drawable.custom_option_box_background_default, null)
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

