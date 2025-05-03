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
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import umc.cozymate.R
import umc.cozymate.databinding.FragmentMakingPublicRoomBinding
import umc.cozymate.ui.cozy_home.room.room_detail.OwnerRoomDetailInfoActivity
import umc.cozymate.ui.pop_up.ServerErrorPopUp
import umc.cozymate.ui.viewmodel.MakingRoomViewModel
import umc.cozymate.util.CharacterUtil

@AndroidEntryPoint
class MakingPublicRoomFragment : Fragment() {
    private val TAG = this.javaClass.simpleName
    private var _binding: FragmentMakingPublicRoomBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MakingRoomViewModel by viewModels()
    private var personaId: Int? = 0
    private var roomName: String = ""
    private var debounceJob: Job? = null
    private var mateNumOption: TextView? = null
    private var mateNum: Int? = 0
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
        viewModel.setPersona(personaId ?: 0)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRoomCreationResultObserver()
        setBackBtn()
        setRoomPersona()
        setRoomName()
        setMateNum()
        setHashtags()
        setNextBtn()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setRoomCreationResultObserver() {
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        viewModel.createPublicRoomResponse.observe(viewLifecycleOwner) { res ->
            if (res != null) {
                viewModel.saveRoomName(res.result.name)
                viewModel.saveRoomId(res.result.roomId)
                viewModel.saveRoomPersona(res.result.profileImage)
                viewModel.saveInviteCode(res.result.inviteCode)
                goToRoomDetail(res.result.roomId)
            }
        }
        viewModel.createPublicRoomError.observe(viewLifecycleOwner) { res ->
            Log.d(TAG, "방생성 실패: ${res}")
            if (res != null) {
                val popup = ServerErrorPopUp.newInstance(res.code, res.message)
                popup.show(childFragmentManager, "팝업")
            }
        }
    }

    private fun goToRoomDetail(roomId: Int) {
        val intent = Intent(requireContext(), OwnerRoomDetailInfoActivity::class.java)
        intent.putExtra(OwnerRoomDetailInfoActivity.ARG_ROOM_ID, roomId)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun setBackBtn() {
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun setRoomPersona() {
        binding.ivPersona.setOnClickListener {
            val intent = Intent(context, SelectingRoomPersonaActivity::class.java)
            personaResultLauncher.launch(intent)
        }
    }

    private val personaResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { res ->
            if (res.resultCode == Activity.RESULT_OK) {
                personaId = res.data?.getIntExtra("selectedCharacterId", 1) ?: 0
                CharacterUtil.setImg(personaId, binding.ivPersona)
                viewModel.setPersona(personaId!!)
                updateNextBtnState()
            }
        }

    private fun setRoomName() {
        setValidRoomNameObserver()
        setRoomNameTextWatcher()
    }

    private fun setValidRoomNameObserver() {
        viewModel.isRoomNameValid.observe(viewLifecycleOwner) { isValid ->
            with(binding) {
                if (!isValid) {
                    tvAlertName.visibility = View.VISIBLE
                    tvAlertName.text = "이미 사용중인 방이름이에요!"
                    tilRoomName.isErrorEnabled = true
                } else {
                    tvAlertName.visibility = View.GONE
                    tilRoomName.isErrorEnabled = false
                    roomName = etRoomName.text.toString()
                    viewModel.setNickname(roomName)
                    updateNextBtnState()
                }
            }
        }
    }

    private fun setRoomNameTextWatcher() {
        binding.etRoomName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val input = s.toString()
                val invalidLength = input.length > 12
                val pattern = "^(?=.*[가-힣a-zA-Z0-9])[가-힣a-zA-Z0-9 ]{1,12}(?<! )$".toRegex()
                val containsSeparatedHangul = input.any { it in 'ㄱ'..'ㅎ' || it in 'ㅏ'..'ㅣ' }
                when {
                    invalidLength -> {
                        binding.tvAlertName.visibility = View.VISIBLE
                        binding.tvAlertName.text = "방이름은 12글자를 넘을 수 없어요!"
                        binding.tilRoomName.isErrorEnabled = true
                    }

                    containsSeparatedHangul -> {
                        binding.tvAlertName.visibility = View.VISIBLE
                        binding.tvAlertName.text = "방이름은 분리된 한글(자음, 모음)이 포함되면 안 돼요!"
                        binding.tilRoomName.isErrorEnabled = true
                    }

                    !pattern.matches(input) -> {
                        binding.tvAlertName.visibility = View.VISIBLE
                        binding.tvAlertName.text = "방이름은 한글, 영어, 숫자 및 공백만 입력해주세요!\n" +
                                "단 공백은 처음이나 끝에 올 수 없습니다."
                        binding.tilRoomName.isErrorEnabled = true
                    }

                    else -> {
                        binding.tvAlertName.visibility = View.GONE
                        binding.tilRoomName.isErrorEnabled = false
                        debounceJob?.cancel()
                        debounceJob = viewModel.viewModelScope.launch {
                            delay(1000L)
                            roomName = binding.etRoomName.text.toString()
                            viewModel.roomNameCheck(roomName)
                        }
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setMateNum() {
        val numPeopleTextViews = listOf(
            binding.chip1 to 2,
            binding.chip2 to 3,
            binding.chip3 to 4,
            binding.chip4 to 5,
            binding.chip5 to 6,
        )
        for ((textView, value) in numPeopleTextViews) {
            textView.setOnClickListener { updateMateNum(it, value) }
        }
    }

    private fun updateMateNum(view: View, value: Int) {
        mateNumOption?.apply {
            setTextColor(resources.getColor(R.color.unuse_font, null))
            background =
                resources.getDrawable(R.drawable.custom_option_box_background_default, null)
        }
        mateNumOption = view as TextView
        mateNumOption?.apply {
            setTextColor(resources.getColor(R.color.main_blue, null))
            background = resources.getDrawable(R.drawable.custom_option_box_background_selected_6dp)
        }
        mateNum = value
        viewModel.setMaxMateNum(mateNum ?: 0)
        updateNextBtnState()
    }

    private fun setHashtags() {
        setupHashtag(binding.hashtag1)
        setupHashtag(binding.hashtag2)
        setupHashtag(binding.hashtag3)
        binding.etRoomHashtag.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val input = s.toString()
                val invalidLength = input.length > 5
                if (input.isNotEmpty() && !invalidLength) {
                    binding.tvAlertHashtag.visibility = View.GONE
                    binding.tilRoomHashtag.isErrorEnabled = false
                } else if (invalidLength) {
                    binding.tvAlertHashtag.visibility = View.VISIBLE
                    binding.tvAlertHashtag.text = "해시태그는 최대 5글자 입력 가능해요!"
                    binding.tilRoomHashtag.isErrorEnabled = true
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        binding.etRoomHashtag.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || (event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                val hashtagText = binding.etRoomHashtag.text.toString().trim()
                if (hashtagText.isNotEmpty() && hashtags.size < 3) {
                    hashtags.add(hashtagText)
                    addHashtag()
                    binding.etRoomHashtag.text?.clear()
                    binding.tvAlertHashtag.visibility = View.GONE
                    binding.tilRoomHashtag.isErrorEnabled = false
                } else if (hashtags.size >= 3) {
                    binding.tvAlertHashtag.visibility = View.VISIBLE
                    binding.tvAlertHashtag.text = "최대 3개의 해시태그만 추가할 수 있어요!"
                    binding.tilRoomHashtag.isErrorEnabled = true
                }
                true
            } else false
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupHashtag(tv: TextView) {
        tv.visibility = View.GONE
        tv.isEnabled = true
        tv.isClickable = true
        tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_close, 0) // 닫기 버튼 설정
        tv.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                removeHashtag(tv)
                val drawableEnd = tv.compoundDrawables[2]
                drawableEnd?.let {
                    val drawableWidth = it.bounds.width()
                    val touchableAreaStart = tv.width - tv.paddingEnd - drawableWidth
                    if (event.x >= touchableAreaStart) {
                        removeHashtag(tv)
                        true
                    } else false
                } ?: false
            } else false
        }
    }

    private fun removeHashtag(tv: TextView) {
        tv.visibility = View.GONE
        hashtags.remove(tv.text)
        viewModel.setHashtags(hashtags)
        updateNextBtnState()
    }

    private fun addHashtag() {
        val chipViews = listOf(binding.hashtag1, binding.hashtag2, binding.hashtag3)
        for (i in chipViews.indices) {
            if (i < hashtags.size) {
                chipViews[i].text = hashtags[i]
                chipViews[i].visibility = View.VISIBLE
            } else {
                chipViews[i].visibility = View.GONE
            }
        }
        viewModel.setHashtags(hashtags)
        updateNextBtnState()
    }

    private fun setNextBtn() {
        updateNextBtnState()
        binding.btnNext.setOnClickListener {
            if (hashtags.size == 0) {
                Toast.makeText(context, "방 해시태그를 한 개 이상 입력해주세요", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.setPersona(personaId ?: 0)
                viewModel.setNickname(roomName)
                viewModel.setMaxMateNum(mateNum ?: 0)
                viewModel.setHashtags(hashtags)
                viewModel.checkAndSubmitCreatePublicRoom()
            }
        }
    }

    // 페르소나/닉넴/인원수/해시태그 입력 필수
    private fun updateNextBtnState() {
        val isPersonaSelected = personaId != 0
        val isNicknameEntered = binding.etRoomName.text?.isNotEmpty() == true
        val isMateNumSelected = mateNum != 0
        val isHashtagEntered = hashtags.isNotEmpty()
        val isEnabled = isPersonaSelected && isNicknameEntered && isMateNumSelected && isHashtagEntered
        binding.btnNext.isEnabled = isEnabled
    }
}