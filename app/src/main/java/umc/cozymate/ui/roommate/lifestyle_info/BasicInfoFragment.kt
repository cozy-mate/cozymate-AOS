package umc.cozymate.ui.roommate.lifestyle_info

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationSet
import android.view.animation.TranslateAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import umc.cozymate.R
import umc.cozymate.databinding.FragmentBasicInfoBinding
import umc.cozymate.ui.roommate.RoommateInputInfoActivity
import umc.cozymate.ui.viewmodel.UniversityViewModel

class BasicInfoFragment : Fragment() {

    private lateinit var binding: FragmentBasicInfoBinding
    private val universityViewModel: UniversityViewModel by activityViewModels()
    private var onLivingOption: TextView? = null
    private var numPeopleOption: TextView? = null
    private var numPeople: Int? = 2
    private var dormitoryNameOption: TextView? = null

    // 타이머를 위한 Handler와 Runnable 선언
    private val handler = Handler(Looper.getMainLooper())
    private var runnable: Runnable? = null
    private val delayInMillis: Long = 500  // 1초 (1000ms)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBasicInfoBinding.inflate(inflater, container, false)

        observeDormitoryNames()
        fetchUniversityData()

        binding.etNumber.filters = arrayOf(InputFilter.LengthFilter(2))  // 최대 2자리 입력
        binding.etNumber.inputType = InputType.TYPE_CLASS_NUMBER // 숫자만 입력 가능하게 설정

        binding.etBirth.filters = arrayOf(InputFilter.LengthFilter(4))  // 최대 4자리 입력
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


    private fun fetchUniversityData() {
        val sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val universityId = sharedPreferences.getInt("university_id", -1)

        if (universityId != -1) {
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    Log.d("BasicInfoFragment", "Fetching university info for ID: $universityId")
                    universityViewModel.getDormitory(universityId)
                } catch (e: Exception) {
                    Log.e("BasicInfoFragment", "Error fetching university info: $e")
                }
            }
        } else {
            Log.e("BasicInfoFragment", "University ID not found in SharedPreferences")
        }
    }

    // Observe the dormitory names from the ViewModel
    private fun observeDormitoryNames() {
        universityViewModel.dormitoryNames.observe(viewLifecycleOwner) { dormitoryNames ->
            if (!dormitoryNames.isNullOrEmpty()) {
                setupDormitoryOptions(dormitoryNames)
            } else {
                Log.e("BasicInfoFragment", "Dormitory names list is empty or null")
            }
        }
    }

    private fun setupDormitoryOptions(dormitoryNames: List<String>) {
        binding.lyDormitoryName.removeAllViews() // 기존 선택지 제거

        dormitoryNames.forEach { dormitoryName ->
            val textView = TextView(requireContext()).apply {
                text = dormitoryName
                textSize = 12f
                setPadding(48, 32, 48, 32)
                height = dpToPx(40)
                setTextColor(resources.getColor(R.color.unuse_font, null))
                background = resources.getDrawable(R.drawable.custom_option_box_background_default, null)
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 10, 16, 0)
                }
                setOnClickListener {
                    updateDormitorySelection(this, dormitoryName)
                }
            }
            binding.lyDormitoryName.addView(textView)
        }

        // 확인 로그 추가
        Log.d("setupDormitoryOptions", "Added dormitory options: $dormitoryNames")
    }
    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    private fun updateDormitorySelection(selectedView: TextView, dormitoryName: String) {
        dormitoryNameOption?.apply {
            setTextColor(resources.getColor(R.color.unuse_font, null))
            background = resources.getDrawable(R.drawable.custom_option_box_background_default, null)
        }

        dormitoryNameOption = selectedView
        dormitoryNameOption?.apply {
            setTextColor(resources.getColor(R.color.main_blue, null))
            background = resources.getDrawable(R.drawable.custom_option_box_background_selected_6dp, null)
        }

        saveToSharedPreferences("user_dormitoryName", dormitoryName)
        resetDebounceTimer { showPeopleNumberLayout() }
    }

    private fun initTextChangeListener() {
        binding.etNumber.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val inputText = s.toString()
                if (inputText.length == 2) {
                    saveToSharedPreferences("user_admissionYear", s.toString())
                    showBirthLayout()
                } else {
                    binding.etNumber.error = "2자리 숫자를 입력해주세요."
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        binding.etBirth.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val inputText = s.toString()
                if (inputText.length == 4) {
                    saveToSharedPreferences("user_birthday", s.toString())
                    updateNextButtonState()
                    showDormitoryNameLayout()
                } else {
                    binding.etBirth.error = "4자리 숫자를 입력해주세요."
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
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
            textView.setOnClickListener {
//                onLivingOptionSelected(it, value)
                updateSelectedOption(it, "user_acceptance", value)
            }
        }
    }

    private fun initNumPeoPleSelector() {
        val numPeopleTexts = listOf(
            binding.num0 to 0,
            binding.num2 to 2,
            binding.num3 to 3,
            binding.num4 to 4,
            binding.num5 to 5,
            binding.num6 to 6
        )
        for ((textView, value) in numPeopleTexts) {
            textView.setOnClickListener {
//                numPeopleSelected(it, value)
                updateSelectedOption(it, "user_numOfRoommate", value)
            }
        }
    }

    private fun updateSelectedOption(view: View, key: String, value: Any) {
        val selectedTextView = view as TextView

        when (key) {
            "user_acceptance" -> {
                onLivingOption?.apply {
                    setTextColor(resources.getColor(R.color.unuse_font, null))
                    background =
                        resources.getDrawable(R.drawable.custom_option_box_background_default, null)
                }
                onLivingOption = selectedTextView
                saveToSharedPreferences(key, value as String)
                updateNextButtonState()
            }

            "user_numOfRoommate" -> {
                numPeopleOption?.apply {
                    setTextColor(resources.getColor(R.color.unuse_font, null))
                    background =
                        resources.getDrawable(R.drawable.custom_option_box_background_default, null)
                }
                numPeopleOption = selectedTextView
                saveToSPFInt(key, value as Int)
                resetDebounceTimer { showDormitoryLayout() }
                updateNextButtonState()
            }

            "user_dormitoryName" -> {
                dormitoryNameOption?.apply {
                    setTextColor(resources.getColor(R.color.unuse_font, null))
                    background =
                        resources.getDrawable(R.drawable.custom_option_box_background_default, null)
                }
                dormitoryNameOption = selectedTextView
                saveToSharedPreferences(key, value as String)
                resetDebounceTimer { showPeopleNumberLayout() }
                updateNextButtonState()
            }
        }

        selectedTextView.apply {
            setTextColor(resources.getColor(R.color.main_blue, null))
            background =
                resources.getDrawable(R.drawable.custom_option_box_background_selected_6dp, null)
        }
    }

    private fun saveToSharedPreferences(key: String, value: String) {
        val spf = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        spf.edit().putString(key, value).apply()
    }

    private fun saveToSPFInt(key: String, value: Int) {
        val spf = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        spf.edit().putInt(key, value).apply()
    }

    fun updateNextButtonState() {
        val isDormitorySelected = onLivingOption != null
        val isRoommateNumSelected = numPeopleOption != null
        val isDormitoryNameSelected = dormitoryNameOption != null
        val isBirthFilled = binding.etBirth.text?.isNotEmpty() == true
        val isNumberFilled = binding.etNumber.text?.isNotEmpty() == true

        val filledCount = listOf(
            isDormitorySelected,
            isRoommateNumSelected,
            isDormitoryNameSelected,
            isBirthFilled,
            isNumberFilled
        ).count { it }
        val completionRate = filledCount / 5f

        (activity as? RoommateInputInfoActivity)?.updateProgressBar(completionRate)

        // 다음 버튼 활성화 여부
        if (isDormitorySelected && isRoommateNumSelected && isBirthFilled && isNumberFilled) {
            (activity as? RoommateInputInfoActivity)?.showNextButton()
        }
    }

    private fun showBirthLayout() {
        if (binding.etNumber.text?.isNotEmpty() == true) {
            binding.clBirth.showWithSlideDownAnimation()
            scrollToTop()
        }
    }

    private fun showDormitoryNameLayout() {
        if (binding.etBirth.text?.isNotEmpty() == true) {
            Log.d("BasicInfoFragment", "showDormitoryNameLayout called, dormitory names: ${universityViewModel.dormitoryNames.value}")
            binding.clDormitoryName.showWithSlideDownAnimation()
            scrollToTop()
        }
    }

    private fun showPeopleNumberLayout() {
        binding.clPeopleNumber.showWithSlideDownAnimation()
        scrollToTop()

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

    private fun View.showWithSlideDownAnimation(duration: Long = 1300) {
        if (this.visibility == View.VISIBLE) return

        // 애니메이션 설정
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
        this.startAnimation(animationSet)
        this.visibility = View.VISIBLE
    }
}