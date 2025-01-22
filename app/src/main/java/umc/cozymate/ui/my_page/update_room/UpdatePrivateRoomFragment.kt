package umc.cozymate.ui.my_page.update_room

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import umc.cozymate.R
import umc.cozymate.databinding.FragmentMakingPrivateRoomBinding
import umc.cozymate.ui.MainActivity
import umc.cozymate.ui.cozy_home.room.making_room.SelectingRoomCharacterActivity
import umc.cozymate.ui.viewmodel.MakingRoomViewModel
import umc.cozymate.util.CharacterUtil

@AndroidEntryPoint
class UpdatePrivateRoomFragment : Fragment() {
    private val TAG = this.javaClass.simpleName
    private var _binding: FragmentMakingPrivateRoomBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MakingRoomViewModel by viewModels()
    private var numPeopleOption: TextView? = null
    private var numPeople: Int = 0
    private var charId: Int? = 1
    private var roomName: String = ""
    private var debounceJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMakingPrivateRoomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.setPersona(charId ?: 1)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            // 뒤로가기
            ivBack.setOnClickListener {
                val intent = Intent(context, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                requireActivity().finish()
            }
            // 캐릭터 선택
            ivCharacter.setOnClickListener {
                val intent = Intent(context, SelectingRoomCharacterActivity::class.java)
                characterResultLauncher.launch(intent)
            }
            // 방 이름 유효성 체크
            checkValidRoomName()
            // 최대인원수 체크
            val numPeopleTexts = listOf(
                binding.chip1 to 2,
                binding.chip2 to 3,
                binding.chip3 to 4,
                binding.chip4 to 5,
                binding.chip5 to 6,
            )
            for ((textView, value) in numPeopleTexts) {
                textView.setOnClickListener { numPeopleSelected(it, value) }
            }
            // 캐릭터, 방이름, 최대인원 선택되어야 [수정] 버튼 활성화
            updateNextButtonState()
        }
        setupObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // 다음 버튼 비황성/활성
    fun updateNextButtonState() {
        with(binding) {
            val isCharacterSelected = charId != 0
            val isRoomNameEntered = etRoomName.text?.isNotEmpty() == true
            val isPeopleNumSelected = numPeople != 0
            val isEnabled = isCharacterSelected && isRoomNameEntered && isPeopleNumSelected
            btnNext.isEnabled = isEnabled
            btnNext.setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.checkAndSubmitUpdateRoom() // 방 정보 PATCH
                    Log.d(TAG, "초대코드방 clicklistener 활성화 : $charId $roomName $numPeople")
                }
            }
        }
    }

    // 방 이름 유효한지 체크
    private fun checkValidRoomName() {
        with(binding) {
            // 방 글자수
            etRoomName.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val input = s.toString()
                    val pattern = "^(?=.*[가-힣a-zA-Z0-9])[가-힣a-zA-Z0-9 ]{1,12}(?<! )$".toRegex()
                    val containsSeparatedHangul = input.any { it in 'ㄱ'..'ㅎ' || it in 'ㅏ'..'ㅣ' }
                    when {
                        containsSeparatedHangul -> {
                            tvAlertName.visibility = View.VISIBLE
                            tvAlertName.text = "방이름은 분리된 한글(자음, 모음)이 포함되면 안됩니다!"
                            tilRoomName.isErrorEnabled = true
                        }

                        !pattern.matches(input) -> {
                            tvAlertName.visibility = View.VISIBLE
                            tvAlertName.text = "방이름은 최대 12글자로 한글, 영어, 숫자 및 공백만 입력해주세요!\n" +
                                    "단 공백은 처음이나 끝에 올 수 없습니다."
                            tilRoomName.isErrorEnabled = true
                        }

                        else -> {
                            tvAlertName.visibility = View.GONE
                            roomName = etRoomName.text.toString()
                            tilRoomName.isErrorEnabled = false
                            viewModel.setNickname(roomName)
                            // Debounce 작업: 사용자가 입력을 멈춘 후 일정 시간 후에 중복 체크 API 호출
                            debounceJob?.cancel()
                            debounceJob = viewModel.viewModelScope.launch {
                                delay(500L) // 500ms 대기
                                viewModel.setNickname(input)
                                viewModel.roomNameCheck() // API 호출
                                observeRoomNameValid()
                            }
                            // 다음 버튼 상태 확인
                            updateNextButtonState()
                        }
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }
    }

    // 방이름 중복체크 옵저빙
    fun observeRoomNameValid() {
        viewModel.isNameValid.observe(viewLifecycleOwner) { isValid ->
            with(binding) {
                if (!isValid) {
                    tvAlertName.visibility = View.VISIBLE
                    tvAlertName.text = "이미 사용중인 방이름이에요!"
                } else {
                    tvAlertName.visibility = View.GONE
                    roomName = etRoomName.text.toString()
                    viewModel.setNickname(roomName)
                }
            }
        }
    }

    // 인원수 옵션 클릭
    private fun numPeopleSelected(view: View, value: Int) {
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
        viewModel.setMaxNum(numPeople)
        updateNextButtonState()
    }

    private fun setupObservers() {
        // 방 생성 결과를 관찰하여 성공 시 다음 화면으로 전환
        viewModel.updateRoomInfoResponse.observe(viewLifecycleOwner) { result ->
            requireActivity().finish()
        }
        // 에러 응답도 추가로 처리할 수 있음 >> TODO : 팝업 띄우기
        viewModel.updateRoomInfoError.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    // 캐릭터 아이디 받아오기
    private val characterResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // 결과가 정상적으로 반환되었는지 확인
            val selectedCharacterId = result.data?.getIntExtra("selectedCharacterId", 0) ?: 0
            // 선택된 캐릭터 아이디 반영
            charId = selectedCharacterId
            CharacterUtil.setImg(charId, binding.ivCharacter)
            viewModel.setPersona(selectedCharacterId)
            updateNextButtonState()
        }
    }
}

