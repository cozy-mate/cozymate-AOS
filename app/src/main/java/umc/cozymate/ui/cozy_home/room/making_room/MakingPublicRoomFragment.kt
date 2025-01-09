package umc.cozymate.ui.cozy_home.room.making_room

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import umc.cozymate.R
import umc.cozymate.databinding.FragmentMakingPublicRoomBinding
import umc.cozymate.ui.viewmodel.MakingRoomViewModel
import umc.cozymate.util.CharacterUtil

@AndroidEntryPoint
class MakingPublicRoomFragment : Fragment() {
    private val TAG = this.javaClass.simpleName
    private var _binding: FragmentMakingPublicRoomBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MakingRoomViewModel by viewModels()
    private var roomName: String = ""
    private var numPeopleOption: TextView? = null
    private var numPeople: Int? = 0
    private var charId: Int? = 0 // 0은 선택 안 되었다는 의미
    private val hashtags = mutableListOf<String>()
    private var debounceJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMakingPublicRoomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.setPersona(charId ?: 0)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            // 뒤로가기
            ivBack.setOnClickListener {
                requireActivity().onBackPressed()
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
            // 해시태그 설정
            setupHashtagInput()

            // 캐릭터, 이름, 최대인원수, 해시태그 선택되어 있어야 다음 버튼 활성화
            updateNextButtonState()
        }

        addProgerssBar()

        // 방 생성 옵저빙
        setupObservers()
    }
    private fun addProgerssBar(){
        viewModel.loading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }
    }

    // 다음 버튼 비활성/활성
    fun updateNextButtonState() {
        with(binding) {
            val isCharacterSelected = charId != 0
            val isRoomNameEntered = etRoomName.text?.isNotEmpty() == true
            val isPeopleNumSelected = numPeople != 0
            val isHashtagEntered = hashtags.isNotEmpty()
            val isEnabled =
                isCharacterSelected && isRoomNameEntered && isPeopleNumSelected && isHashtagEntered
            btnNext.isEnabled = isEnabled
            btnNext.setOnClickListener {
                viewModel.checkAndSubmitCreatePublicRoom() // 방 정보 POST
                if (hashtags.size == 0) {
                    Toast.makeText(context, "방 해시태그를 한 개 이상 입력해주세요", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // 해시태그 설정
    private fun setupHashtagInput() {
        with(binding) {
            hashtag1.visibility = View.GONE
            hashtag2.visibility = View.GONE
            hashtag3.visibility = View.GONE
            // 삭제 버튼 설정
            setupHashtag(hashtag1)
            setupHashtag(hashtag2)
            setupHashtag(hashtag3)
            // 해시태그 입력값을 반영하기
            etRoomHashtag.setOnEditorActionListener { textView, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE || (event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                    val hashtagText = etRoomHashtag.text.toString().trim()
                    if (hashtagText.isNotEmpty() && hashtags.size < 3) {
                        // 해시태그 추가 및 edittext 텍스트 삭제
                        hashtags.add(hashtagText)
                        updateHashtagChips()
                        etRoomHashtag.text?.clear()
                        viewModel.setHashtags(hashtags)
                    } else if (hashtags.size >= 3) {
                        Toast.makeText(context, "최대 3개의 해시태그만 추가할 수 있습니다.", Toast.LENGTH_SHORT)
                            .show()
                    }
                    true
                } else {
                    false
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setupHashtag(tv: TextView) {
        tv.isEnabled = true
        tv.isClickable = true
        tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_close, 0) // 닫기 버튼 설정
        tv.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                removeHashtag(tv) ///
                val drawableEnd = tv.compoundDrawables[2]
                drawableEnd?.let {
                    val drawableWidth = it.bounds.width()
                    val touchableAreaStart = tv.width - tv.paddingEnd - drawableWidth
                    if (event.x >= touchableAreaStart) {
                        removeHashtag(tv) /// 왜 안 됨?
                        true
                    } else {
                        false
                    }
                } ?: false
            } else {
                false
            }
        }
    }

    fun removeHashtag(tv: TextView) {
        tv.visibility = View.GONE
        hashtags.remove(tv.text)
        updateNextButtonState()
        viewModel.setHashtags(hashtags)
    }

    private fun updateHashtagChips() {
        val chipViews = listOf(binding.hashtag1, binding.hashtag2, binding.hashtag3)
        for (i in chipViews.indices) {
            if (i < hashtags.size) {
                chipViews[i].text = hashtags[i]
                chipViews[i].visibility = View.VISIBLE
            } else {
                chipViews[i].visibility = View.GONE
            }
        }
        updateNextButtonState()
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
        viewModel.setMaxNum(numPeople!!)
        updateNextButtonState()
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
                            viewModel.setNickname(roomName)
                            // Debounce 작업: 사용자가 입력을 멈춘 후 일정 시간 후에 중복 체크 API 호출
                            debounceJob?.cancel()
                            debounceJob = viewModel.viewModelScope.launch {
                                delay(1000L) // 1000ms 대기
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

            // 해시태그 중복
        }
    }

    // 방이름 중복체크 옵저빙
    fun observeRoomNameValid() {
        viewModel.isNameValid.observe(viewLifecycleOwner) { isValid ->
            with(binding) {
                if (!isValid) {
                    tvAlertName.visibility = View.VISIBLE
                    tvAlertName.text = "이미 사용중인 방이름이에요!"
                    tilRoomName.isErrorEnabled = true
                } else {
                    tvAlertName.visibility = View.GONE
                    roomName = etRoomName.text.toString()
                    tilRoomName.isErrorEnabled = false
                    viewModel.setNickname(roomName)
                }
            }
        }
    }

    private fun setupObservers() {
        // 로딩 상태를 관찰하여 ProgressBar를 표시 / 숨기기
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                (activity as? MakingPublicRoomActivity)?.showProgressBar(true)
            } else {
                (activity as? MakingPublicRoomActivity)?.showProgressBar(false)
            }
        }

        // 방 생성 결과를 관찰하여 성공 시 다음 화면으로 전환
        viewModel.publicRoomCreationResult.observe(viewLifecycleOwner) { result ->
            //(activity as? MakingPublicRoomActivity)?.loadMyRoomDetailActivity(0)
            (activity as? MakingPublicRoomActivity)?.loadMainActivity()
        }

        // 팝업을 띄워서 에러 응답 처리
        viewModel.errorResponse.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // SelectingRoomCharacterActivity에서 캐릭터 아이디 받아오기
    private val characterResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // 결과가 정상적으로 반환되었는지 확인
            val selectedCharacterId = result.data?.getIntExtra("selectedCharacterId", 1) ?: 0
            // 선택된 캐릭터 아이디 반영
            charId = selectedCharacterId
            CharacterUtil.setImg(selectedCharacterId, binding.ivCharacter)
            viewModel.setPersona(selectedCharacterId)
            updateNextButtonState()
        }
    }
}

