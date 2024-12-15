package umc.cozymate.ui.cozy_home.making_room

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
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
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.R
import umc.cozymate.databinding.FragmentMakingPublicRoomBinding
import umc.cozymate.ui.viewmodel.MakingRoomViewModel
import umc.cozymate.util.setupTextInputWithMaxLength

@AndroidEntryPoint
class MakingPublicRoomFragment : Fragment() {

    private var _binding: FragmentMakingPublicRoomBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MakingRoomViewModel
    private var numPeopleOption: TextView? = null
    private var numPeople: String? = null
    private var charId: Int? = 1
    private val hashtags = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMakingPublicRoomBinding.inflate(inflater, container, false)

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
            setupHashtagInput()

            ivBack.setOnClickListener {
                requireActivity().onBackPressed()
            }
            btnNext.setOnClickListener {
                val roomName = etRoomName.text.toString()
                val maxNum = numPeople?.filter { it.isDigit() }?.toInt() ?: 6 // 인원 수 숫자만 추출

                if (roomName.isNotEmpty() && charId != null && maxNum > 0 && hashtags.size > 0) {
                    viewModel.setNickname(roomName)
                    viewModel.setMaxNum(maxNum)
                    viewModel.setImg(charId ?: 1)
                    viewModel.setHashtags(hashtags)

                    // 방 생성 요청
                    viewModel.createPublicRoom()
                } else {
                    if (hashtags.size == 0) "방 해시태그를 한 개 이상 입력해주세요"
                    else Toast.makeText(context, "방 이름과 인원 수를 확인해주세요.", Toast.LENGTH_SHORT).show()
                }
            }

            var isSelected = false
            ivCharacter.setOnClickListener {
                val intent = Intent(context, CozyHomeSelectingCharacterActivity::class.java)
                characterResultLauncher.launch(intent)
                isSelected = true
            }
            if (isSelected) ivCharacter.setImageResource(R.drawable.character_id_1)

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

    private fun setupHashtagInput() {
        with(binding) {
            setupHashtag(hashtag1)
            setupHashtag(hashtag2)
            setupHashtag(hashtag3)
            etRoomHashtag.setOnEditorActionListener { textView, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE || (event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                    val hashtagText = etRoomHashtag.text.toString().trim()

                    if (hashtagText.isNotEmpty() && hashtags.size < 3) {
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
        tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_close, 0) // 닫기 버튼 설정
        tv.visibility = View.GONE
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

