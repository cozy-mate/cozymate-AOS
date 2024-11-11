package umc.cozymate.ui.cozy_home.making_room

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.R
import umc.cozymate.databinding.FragmentMakingPrivateRoomBinding
import umc.cozymate.ui.viewmodel.MakingRoomViewModel
import umc.cozymate.util.setupTextInputWithMaxLength

@AndroidEntryPoint
class MakingPublicRoomFragment : Fragment() {

    private var _binding: FragmentMakingPrivateRoomBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MakingRoomViewModel

    private var numPeopleOption: TextView? = null
    private var numPeople: String? = null

    private var charId: Int? = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMakingPrivateRoomBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(requireActivity())[MakingRoomViewModel::class.java]
        setupObservers()

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        viewModel.setImg(charId ?: 1)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            ivBack.setOnClickListener {
                requireActivity().onBackPressed()
            }
            btnNext.setOnClickListener {
                // 방 이름과 최대 인원 수를 ViewModel에 설정
                val roomName = etRoomName.text.toString()
                val maxNum = numPeople?.filter { it.isDigit() }?.toInt() ?: 6 // 인원 수 숫자만 추출

                if (roomName.isNotEmpty() && maxNum > 0) {
                    viewModel.setNickname(roomName)
                    viewModel.setMaxNum(maxNum)
                    viewModel.setImg(charId ?: 1)

                    // 방 생성 요청
                    viewModel.createRoom()
                } else {
                    Toast.makeText(context, "방 이름과 인원 수를 확인해주세요.", Toast.LENGTH_SHORT).show()
                }

                // (activity as? CozyHomeInvitingRoommateActivity)?.loadFragment2()
                //(activity as? CozyHomeGivingInviteCodeActivity)?.loadFragment2()
            }

            var isSelected = false
            ivCharacter.setOnClickListener {
                val intent = Intent(context, CozyHomeSelectingCharacterActivity::class.java)
                characterResultLauncher.launch(intent)
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

    private fun setupObservers() {
        // 로딩 상태를 관찰하여 ProgressBar를 제어
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                // 로딩 중일 때 ProgressBar를 표시
                (activity as? MakingPrivateRoomActivity)?.showProgressBar(true)
            } else {
                // 로딩이 끝났을 때 ProgressBar를 숨김
                (activity as? MakingPrivateRoomActivity)?.showProgressBar(false)
            }
        }

        // 방 생성 결과를 관찰하여 성공 시 다음 화면으로 전환
        viewModel.roomCreationResult.observe(viewLifecycleOwner) { result ->
            // 방 생성 성공 시 다음 화면으로 이동
            (activity as? MakingPrivateRoomActivity)?.loadFragment2()
        }

        // 에러 응답도 추가로 처리할 수 있음
        viewModel.errorResponse.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                // 에러 처리 (예: 토스트 메시지로 에러 표시)
                Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // 캐릭터 아이디 받아오기
    private val characterResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // 결과가 정상적으로 반환되었는지 확인
            val selectedCharacterId = result.data?.getIntExtra("selectedCharacterId", 1) ?: 1
            // 이미지 리소스를 반영
            charId = selectedCharacterId
            setCharacterImage(selectedCharacterId)
            // ViewModel에 선택된 이미지 ID 저장
            viewModel.setImg(selectedCharacterId)
        }
    }

    private fun setCharacterImage(persona: Int) {
        when (persona) {
            1 -> binding.ivCharacter.setImageResource(R.drawable.character_1)
            2 -> binding.ivCharacter.setImageResource(R.drawable.character_2)
            3 -> binding.ivCharacter.setImageResource(R.drawable.character_3)
            4 -> binding.ivCharacter.setImageResource(R.drawable.character_4)
            5 -> binding.ivCharacter.setImageResource(R.drawable.character_5)
            6 -> binding.ivCharacter.setImageResource(R.drawable.character_6)
            7 -> binding.ivCharacter.setImageResource(R.drawable.character_7)
            8 -> binding.ivCharacter.setImageResource(R.drawable.character_8)
            9 -> binding.ivCharacter.setImageResource(R.drawable.character_9)
            10 -> binding.ivCharacter.setImageResource(R.drawable.character_10)
            11 -> binding.ivCharacter.setImageResource(R.drawable.character_11)
            12 -> binding.ivCharacter.setImageResource(R.drawable.character_12)
            13 -> binding.ivCharacter.setImageResource(R.drawable.character_13)
            14 -> binding.ivCharacter.setImageResource(R.drawable.character_14)
            15 -> binding.ivCharacter.setImageResource(R.drawable.character_15)
            16 -> binding.ivCharacter.setImageResource(R.drawable.character_16)
            else -> binding.ivCharacter.setImageResource(R.drawable.character_1) // 기본 이미지 설정
        }
    }
}

