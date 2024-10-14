package umc.cozymate.ui.roommate.lifestyle_info

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationSet
import android.view.animation.TranslateAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import umc.cozymate.R
import umc.cozymate.databinding.FragmentBasicInfoBinding
import umc.cozymate.ui.roommate.RoommateInputInfoActivity
import umc.cozymate.ui.roommate.UserInfoSPFHelper
import umc.cozymate.ui.roommate.data_class.UserInfo

class BasicInfoFragment : Fragment() {

    private lateinit var binding: FragmentBasicInfoBinding
    private lateinit var spfHelper: UserInfoSPFHelper

    private var userInfo = UserInfo()
    private var onLivingOption: TextView? = null
    private var numPeopleOption: TextView? = null
    private var numPeople: Int? = 2

    // 타이머를 위한 Handler와 Runnable 선언
    private val handler = Handler(Looper.getMainLooper())
    private var runnable: Runnable? = null
    private val delayInMillis: Long = 500  // 1초 (1000ms)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBasicInfoBinding.inflate(inflater, container, false)

        spfHelper = (activity as RoommateInputInfoActivity).getUserInfoSPFHelper()
        userInfo = spfHelper.loadUserInfo()

        binding.etNumber.filters = arrayOf(InputFilter.LengthFilter(2))  // 최대 2자리 입력
        binding.etNumber.inputType = InputType.TYPE_CLASS_NUMBER // 숫자만 입력 가능하게 설정

        binding.etBirth.filters = arrayOf(InputFilter.LengthFilter(4))  // 최대 2자리 입력
        binding.etBirth.inputType = InputType.TYPE_CLASS_NUMBER // 숫자만 입력 가능하게 설정

        initTextChangeListener()
        initLivingSelector()
        initNumPeoPleSelector()

        binding.nestedScrollView.setOnTouchListener { _, _ ->
            removeEditTextFocus()
            false
        }

        updateNextButtonState()

        return binding.root
    }

    private fun initTextChangeListener() {
        binding.etNumber.addTextChangedListener(createTextWatcher())
        binding.etBirth.addTextChangedListener(createBirthTextWatcher())
    }

    // 텍스트 변경 시 딜레이 후 레이아웃 표시를 위한 타이머 재설정 함수
    private fun resetDebounceTimer(action: () -> Unit) {
        runnable?.let { handler.removeCallbacks(it) }  // 기존 타이머 취소
        runnable = Runnable { action() }  // 새로운 작업 설정
        handler.postDelayed(runnable!!, delayInMillis)  // 1초 후 실행
    }

    private fun initLivingSelector() {
        val onLivingTexts = listOf(
            binding.dormitoryPass to "합격",
            binding.dormitoryWaiting to "대기중",
            binding.dormitoryNumber to "예비번호를 받았어요!"
        )
        for ((textView, value) in onLivingTexts) {
            textView.setOnClickListener { onLivingOptionSelected(it, value) }
        }
    }

    private fun initNumPeoPleSelector() {
        val numPeopleTexts = listOf(
            binding.num2 to 2,
            binding.num3 to 3,
            binding.num4 to 4,
            binding.num5 to 5,
            binding.num6 to 6
        )
        for ((textView, value) in numPeopleTexts) {
            textView.setOnClickListener {
                numPeopleSelected(it, value)
            }
        }
    }

    private fun onLivingOptionSelected(view: View, value: String) {
        onLivingOption?.apply {
            setTextColor(resources.getColor(R.color.unuse_font, null))
            background = resources.getDrawable(R.drawable.custom_option_box_background_default, null)
        }
        onLivingOption = view as TextView
        onLivingOption?.apply {
            setTextColor(resources.getColor(R.color.main_blue, null))
            background = resources.getDrawable(R.drawable.custom_option_box_background_selected_6dp, null)
        }
        userInfo = userInfo.copy(acceptance = value)
        spfHelper.saveUserInfo(userInfo)
        updateNextButtonState()
    }

    private fun numPeopleSelected(view: View, value: Int) {
        numPeopleOption?.apply {
            setTextColor(resources.getColor(R.color.unuse_font, null))
            background = resources.getDrawable(R.drawable.custom_option_box_background_default, null)
        }
        numPeopleOption = view as TextView
        numPeopleOption?.apply {
            setTextColor(resources.getColor(R.color.main_blue, null))
            background = resources.getDrawable(R.drawable.custom_option_box_background_selected_6dp, null)
        }
        numPeople = value
        userInfo = userInfo.copy(numOfRoommate = value)
        spfHelper.saveUserInfo(userInfo)

        // numPeople 선택 후 dormitory 레이아웃 표시
        showDormitoryLayout()

        updateNextButtonState()
    }

    fun updateNextButtonState() {
        val isDormitorySelected = onLivingOption != null
        val isRoommateNumSelected = numPeopleOption != null
        val isBirthFilled = binding.etBirth.text?.isNotEmpty() == true
        val isNumberFilled = binding.etNumber.text?.isNotEmpty() == true

        val filledCount = listOf(isDormitorySelected, isRoommateNumSelected, isBirthFilled, isNumberFilled).count { it }
        val completionRate = filledCount / 4f

        (activity as? RoommateInputInfoActivity)?.updateProgressBar(completionRate)

        // 다음 버튼 활성화 여부
        if (isDormitorySelected && isRoommateNumSelected && isBirthFilled && isNumberFilled) {
            (activity as? RoommateInputInfoActivity)?.showNextButton()
        }
    }

    private fun createTextWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                userInfo = userInfo.copy(admissionYear = binding.etNumber.text.toString())
                spfHelper.saveUserInfo(userInfo)
                // 타이머를 재설정하여 입력이 일정 시간 없으면 다음 레이아웃 표시
                resetDebounceTimer { showBirthLayout() }
                updateNextButtonState()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
    }

    private fun createBirthTextWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                userInfo = userInfo.copy(birth = binding.etBirth.text.toString())
                spfHelper.saveUserInfo(userInfo)
                // 타이머를 재설정하여 입력이 일정 시간 없으면 다음 레이아웃 표시
                resetDebounceTimer { showPeopleNumberLayout() }
                updateNextButtonState()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
    }

    private fun showBirthLayout() {
        if (binding.etNumber.text?.isNotEmpty() == true) {
            binding.clBirth.showWithSlideDownAnimation()
            scrollToTop()
        }
    }

    private fun showPeopleNumberLayout() {
        if (binding.etBirth.text?.isNotEmpty() == true) {
            binding.clPeopleNumber.showWithSlideDownAnimation()
            scrollToTop()
        }
    }

    private fun showDormitoryLayout() {
        binding.clDormitory.showWithSlideDownAnimation()
        scrollToTop()
    }

    private fun scrollToTop() {
        binding.nestedScrollView.scrollTo(0, 0)
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    private fun removeEditTextFocus() {
        binding.etNumber.clearFocus()
        binding.etBirth.clearFocus()
        hideKeyboard()
    }

    fun View.showWithSlideDownAnimation(duration: Long = 900) {
        this.apply {
            val slideDown = TranslateAnimation(0f, 0f, -this.height.toFloat(), 0f).apply {
                this.duration = duration
            }
            val fadeIn = AlphaAnimation(0f, 1f).apply {
                this.duration = duration
            }
            val animationSet = AnimationSet(true).apply {
                addAnimation(slideDown)
                addAnimation(fadeIn)
            }
            visibility = View.VISIBLE
            startAnimation(animationSet)
        }
    }
}
