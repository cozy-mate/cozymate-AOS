package umc.cozymate.ui.cozy_home.room.making_room

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.R
import umc.cozymate.databinding.FragmentMakingPublicRoomBinding
import umc.cozymate.ui.viewmodel.MakingRoomViewModel

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
            checkValidInfo()

            // 인원수 체크
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

        // 방 생성 옵저빙
        setupObservers()
    }

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
                viewModel.setPersona(charId!!)
                viewModel.setNickname(roomName)
                viewModel.setMaxNum(numPeople!!)
                viewModel.setHashtags(hashtags)
                Log.d(TAG, "잘 되니? : $charId $roomName $numPeople $hashtags")
                viewModel.createPublicRoom() // 방 정보 POST

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
        updateNextButtonState()
    }

    // 방 이름 유효한지 체크
    private fun checkValidInfo() {
        with(binding) {
            // 방 글자수
            etRoomName.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val input = s.toString()
                    if (input.length > 12) {
                        tvAlertName.visibility = View.VISIBLE
                        tvAlertName.text = "방이름은 최대 12글자만 가능해요!"
                    } else {
                        tvAlertName.visibility = View.GONE
                        viewModel.setNickname(input)
                        roomName = etRoomName.text.toString()
                        updateNextButtonState()
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            // 해시태그 중복
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
        viewModel.roomCreationResult.observe(viewLifecycleOwner) { result ->
            (activity as? MakingPublicRoomActivity)?.loadMyRoomDetailActivity(0)
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
            setCharacterImage(selectedCharacterId)
            viewModel.setPersona(selectedCharacterId)
            updateNextButtonState()
        }
    }

    private fun setCharacterImage(persona: Int) {
        when (persona) {
            1 -> binding.ivCharacter.setImageResource(R.drawable.character_id_1)
            2 -> binding.ivCharacter.setImageResource(R.drawable.character_id_2)
            3 -> binding.ivCharacter.setImageResource(R.drawable.character_id_3)
            4 -> binding.ivCharacter.setImageResource(R.drawable.character_id_4)
            5 -> binding.ivCharacter.setImageResource(R.drawable.character_id_5)
            6 -> binding.ivCharacter.setImageResource(R.drawable.character_id_6)
            7 -> binding.ivCharacter.setImageResource(R.drawable.character_id_7)
            8 -> binding.ivCharacter.setImageResource(R.drawable.character_id_8)
            9 -> binding.ivCharacter.setImageResource(R.drawable.character_id_9)
            10 -> binding.ivCharacter.setImageResource(R.drawable.character_id_10)
            11 -> binding.ivCharacter.setImageResource(R.drawable.character_id_11)
            12 -> binding.ivCharacter.setImageResource(R.drawable.character_id_12)
            13 -> binding.ivCharacter.setImageResource(R.drawable.character_id_13)
            14 -> binding.ivCharacter.setImageResource(R.drawable.character_id_14)
            15 -> binding.ivCharacter.setImageResource(R.drawable.character_id_15)
            16 -> binding.ivCharacter.setImageResource(R.drawable.character_id_16)
            else -> binding.ivCharacter.setImageResource(R.drawable.character_id_1) // 기본 이미지 설정
        }
    }
}

