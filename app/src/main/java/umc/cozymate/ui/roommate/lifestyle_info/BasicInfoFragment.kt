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
import umc.cozymate.util.AnalyticsConstants
import umc.cozymate.util.AnalyticsEventLogger

class BasicInfoFragment : Fragment() {

    private lateinit var binding: FragmentBasicInfoBinding
    private val universityViewModel: UniversityViewModel by activityViewModels()
    private var onLivingOption: TextView? = null
    private var numPeopleOption: TextView? = null
    private var numPeople: String? = "2"
    private var dormitoryNameOption: TextView? = null
    private var screenEnterTime: Long = 0

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

        binding.etAdmissionYear.filters = arrayOf(InputFilter.LengthFilter(2))  // 최대 2자리 입력
        binding.etAdmissionYear.inputType = InputType.TYPE_CLASS_NUMBER // 숫자만 입력 가능하게 설정

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

    override fun onResume() {
        super.onResume()
        screenEnterTime = System.currentTimeMillis()
    }

    override fun onPause() {
        super.onPause()
        val screenLeaveTime = System.currentTimeMillis()
        val sessionDuration = screenLeaveTime - screenEnterTime // 밀리초 단위

        // GA 이벤트 로그 추가
        AnalyticsEventLogger.logEvent(
            eventName = AnalyticsConstants.Event.SESSION_TIME_GENERAL,
            category = AnalyticsConstants.Category.LIFE_STYLE,
            action = AnalyticsConstants.Action.SESSION_TIME,
            label = AnalyticsConstants.Label.GENERAL,
            duration = sessionDuration
        )
    }

    private fun fetchUniversityData() {
        val sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val universityId = 1 // sharedPreferences.getInt("university_id", -1)

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
        universityViewModel.dormitoryNames.observe(viewLifecycleOwner) { dormName ->
            if (!dormName.isNullOrEmpty()) {
                setupDormitoryOptions(dormName)
            } else {
                Log.e("BasicInfoFragment", "Dormitory names list is empty or null")
            }
        }
    }

    private fun setupDormitoryOptions(dormName: List<String>) {
        binding.lyDormName.removeAllViews() // 기존 선택지 제거

        dormName.forEach { dormName ->
            val textView = TextView(requireContext()).apply {
                text = dormName
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
                    updateDormitorySelection(this, dormName)
                }
            }
            binding.lyDormName.addView(textView)
        }

        // 확인 로그 추가
        Log.d("setupDormitoryOptions", "Added dormitory options: $dormName")
    }
    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    private fun updateDormitorySelection(selectedView: TextView, dormName: String) {
        dormitoryNameOption?.apply {
            setTextColor(resources.getColor(R.color.unuse_font, null))
            background = resources.getDrawable(R.drawable.custom_option_box_background_default, null)
        }

        dormitoryNameOption = selectedView
        dormitoryNameOption?.apply {
            setTextColor(resources.getColor(R.color.main_blue, null))
            background = resources.getDrawable(R.drawable.custom_option_box_background_selected_6dp, null)
        }

        saveToSharedPreferences("user_dormName", dormName)
        resetDebounceTimer { showPeopleNumberLayout() }
    }

    private fun initTextChangeListener() {
        binding.etAdmissionYear.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val inputText = s.toString()
                if (inputText.length == 2) {
                    saveToSharedPreferences("user_admissionYear", s.toString())
                    showDormitoryNameLayout()
                    updateNextButtonState()
                } else {
                    binding.etAdmissionYear.error = "2자리 숫자를 입력해주세요."
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
            binding.dormJoiningStatusPass to "합격",
            binding.dormJoiningStatusWaiting to "대기중",
            binding.dormJoiningStatusNumber to "예비번호를 받았어요!"
        )
        for ((textView, value) in onLivingTexts) {
            textView.setOnClickListener {
//                onLivingOptionSelected(it, value)
                updateSelectedOption(it, "user_dormJoiningStatus", value)
            }
        }
    }

    private fun initNumPeoPleSelector() {
        val numPeopleTexts = listOf(
            binding.num0 to "0",
            binding.num2 to "2",
            binding.num3 to "3",
            binding.num4 to "4",
            binding.num5 to "5",
            binding.num6 to "6"
        )
        for ((textView, value) in numPeopleTexts) {
            textView.setOnClickListener {
                updateSelectedOption(it, "user_numOfRoommate", value)
            }
        }
    }

    private fun updateSelectedOption(view: View, key: String, value: Any) {
        val selectedTextView = view as TextView

        when (key) {
            "user_dormJoiningStatus" -> {
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
                saveToSharedPreferences(key, value as String)
                resetDebounceTimer { showDormJoiningStatusLayout() }
                updateNextButtonState()
            }

            "user_dormName" -> {
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

    fun updateNextButtonState() {
        val isDormitorySelected = onLivingOption != null
        val isRoommateNumSelected = numPeopleOption != null
        val isDormitoryNameSelected = dormitoryNameOption != null
        val isNumberFilled = binding.etAdmissionYear.text?.isNotEmpty() == true

        val filledCount = listOf(
            isDormitorySelected,
            isRoommateNumSelected,
            isDormitoryNameSelected,
            isNumberFilled
        ).count { it }
        val completionRate = filledCount / 4f

        (activity as? RoommateInputInfoActivity)?.updateProgressBar(completionRate)

        // 다음 버튼 활성화 여부
        if (isDormitorySelected && isRoommateNumSelected && isNumberFilled) {
            (activity as? RoommateInputInfoActivity)?.showNextButton()
        }
    }

    private fun showDormitoryNameLayout() {
        if (binding.etAdmissionYear.text?.isNotEmpty() == true) {
            Log.d("BasicInfoFragment", "showDormitoryNameLayout called, dormitory names: ${universityViewModel.dormitoryNames.value}")
            binding.clDormName.showWithSlideDownAnimation()
            scrollToTop()
        }
    }

    private fun showPeopleNumberLayout() {
        binding.clPeopleNumber.showWithSlideDownAnimation()
        scrollToTop()

    }

    private fun showDormitoryLayout() {
        binding.clDormName.showWithSlideDownAnimation()
        scrollToTop()
    }

    private fun showDormJoiningStatusLayout() {
        binding.clDormJoiningStatus.showWithSlideDownAnimation()
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
        binding.etAdmissionYear.clearFocus()
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