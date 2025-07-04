package umc.cozymate.ui.my_page.lifestyle

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import umc.cozymate.R
import umc.cozymate.data.model.request.UserInfoRequest
import umc.cozymate.databinding.ActivityFetchLifestyleBinding
import umc.cozymate.ui.viewmodel.RoommateViewModel
import umc.cozymate.ui.viewmodel.UniversityViewModel
import umc.cozymate.util.StatusBarUtil
import umc.cozymate.util.navigationHeight
import umc.cozymate.util.setStatusBarTransparent

@AndroidEntryPoint
class FetchLifestyleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFetchLifestyleBinding
    private lateinit var spf: SharedPreferences

    private val universityViewModel: UniversityViewModel by viewModels()

    private val maxLength = 200

    private val viewModel: RoommateViewModel by viewModels()

    // 임시 데이터 저장 변수
    private var selectedAdmissionYear: Int = -1
    private var selectedDormitoryName: String? = null
    private var selectedNumOfRoommate: String? = null
    private var selectedAcceptance: String? = null
    private var selectedWakeUpMeridian: String? = null
    private var selectedWakeUpTime: Int = -1
    private var selectedSleepingMeridian: String? = null
    private var selectedSleepingTime: Int = 0
    private var selectedTurnOffMeridian: String? = null
    private var selectedTurnOffTime: Int = 2
    private var selectedSmoking: String? = "비흡연자"
    private var selectedSleepingHabits: MutableList<String> = mutableListOf()
    private var selectedAc: String = ""
    private var selectedHeater: String = ""
    private var selectedLifePattern: String? = null
    private var selectedIntimacy: String? = null
    private var selectedCanShare: String? = null
    private var selectedIsPlayGame: String? = null
    private var selectedIsPhoneCall: String? = null
    private var selectedStudying: String? = null
    private var selectedIntake: String? = null
    private var selectedCleanSensitivity: String = ""
    private var selectedNoiseSensitivity: String = ""
    private var selectedCleaningFrequency: String? = null
    private var selectedDrinkingFrequency: String? = null
    private var selectedPersonality: MutableList<String> = mutableListOf()
    private var selectedMbti: String? = null
    private var selectedETCInfo: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFetchLifestyleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.setStatusBarTransparent()
        window.navigationBarColor = Color.WHITE
        StatusBarUtil.updateStatusBarColor(this@FetchLifestyleActivity, Color.WHITE)
        binding.main.setPadding(0, 0, 0, this.navigationHeight())

        spf = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        initData()
        observeDormitoryNames()
        // 뒤로가기 버튼
        binding.ivBack.setOnClickListener { finish() }

        // 저장 버튼
        binding.btnSave.setOnClickListener {
            sendFetchUserDataToViewModel()
        }

        // 플로팅 버튼
        setFloatingButton()
    }

    private fun initData() {
        initAdmissionYear(spf.getInt("user_admissionYear", -1))
        initDormitoryName(spf.getString("user_dormName", ""))
        initNumOfRoommate(spf.getString("user_numOfRoommate", ""))
        initAcceptance(spf.getString("user_dormJoiningStatus", ""))
        initWakeUpTime(spf.getInt("user_wakeUpTime", 0))
        initSleepingTime(spf.getInt("user_sleepingTime", 0))
        initTurnOffTime(spf.getInt("user_turnOffTime", 1))
        initSmoking(spf.getString("user_smokingStatus", ""))
        initSleepingHabits(getStringListFromPreferences("user_sleepingHabits"))
        initAc(spf.getString("user_coolingIntensity", ""))
        initHeater(spf.getString("user_heatingIntensity", ""))
        initLifePattern(spf.getString("user_lifePattern", ""))
        initIntimacy(spf.getString("user_intimacy", ""))
        initCanShare(spf.getString("user_sharingStatus", ""))
        initIsPlayGame(spf.getString("user_gamingStatus", ""))
        initIsPhoneCall(spf.getString("user_callingStatus", ""))
        initIsStudying(spf.getString("user_studyingStatus", ""))
        initIntake(spf.getString("user_eatingStatus", ""))
        initCleanSensitivity(spf.getString("user_cleannessSensitivity", ""))
        initNoiseSensitivity(spf.getString("user_noiseSensitivity", ""))
        initCleaningFrequency(spf.getString("user_cleaningFrequency", ""))
        initDrinkingFrequency(spf.getString("user_drinkingFrequency", ""))
        initPersonality(getStringListFromPreferences("user_personalities"))
        initMbti(spf.getString("user_mbti", ""))
        initETCInfo(spf.getString("user_selfIntroduction", ""))
        fetchUniversityData()
    }

    private fun initAdmissionYear(savedValue: Int) {
        if (savedValue != -1) {
            binding.etNumber.setText(savedValue.toString())
            selectedAdmissionYear = savedValue
        }

        val inputFilter = InputFilter.LengthFilter(2)
        binding.etNumber.filters = arrayOf(inputFilter)

        binding.etNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val inputText = s.toString()
                selectedAdmissionYear = if (inputText.isNotEmpty()) inputText.toInt() else -1
            }
        })
    }


    private fun initDormitoryName(savedValue: String?) {
        lifecycleScope.launch {
            val universityId = spf.getInt("user_university_id", 1)
            try {
                universityViewModel.getDormitory(universityId)
                universityViewModel.dormitoryNames.observe(this@FetchLifestyleActivity) { dormitoryNames ->
                    setupDormitoryOptions(dormitoryNames, savedValue)
                }
            } catch (e: Exception) {
                Log.e("FetchLifestyleActivity", "Failed to fetch dormitory names: $e")
            }
        }
    }

    private fun fetchUniversityData() {
        val universityId = spf.getInt("user_university_id", 1)
        lifecycleScope.launch {
            try {
                universityViewModel.getDormitory(universityId)
            } catch (e: Exception) {
                Log.e("FetchLifestyleActivity", "Error fetching dormitory data: $e")
            }
        }
    }

    private fun observeDormitoryNames() {
        universityViewModel.dormitoryNames.observe(this) { dormitoryNames ->
            if (!dormitoryNames.isNullOrEmpty()) {
                setupDormitoryOptions(dormitoryNames, spf.getString("user_dormName", ""))
            } else {
                Log.e("FetchLifestyleActivity", "Dormitory names list is empty or null")
            }
        }
    }

    private fun setupDormitoryOptions(dormitoryNames: List<String>, savedValue: String? = null) {
        // lyDormitoryName의 기존 뷰 제거
        binding.lyDormitoryName.removeAllViews()

        dormitoryNames.forEach { dormitoryName ->
            val textView = createDormitoryOption(dormitoryName)
            // lyDormitoryName에 버튼 추가
            binding.lyDormitoryName.addView(textView)

            // 초기 선택된 기숙사 설정
            if (dormitoryName == savedValue) {
                updateSelectedDormitoryOption(textView)
            }
        }
    }

    private fun updateSelectedDormitoryOption(selectedView: TextView) {
        // 모든 기숙사 옵션 초기화 (안전한 타입 확인)
        for (i in 0 until binding.lyDormitoryName.childCount) {
            val child = binding.lyDormitoryName.getChildAt(i)
            if (child is TextView) { // TextView인 경우만 초기화
                child.setTextColor(ContextCompat.getColor(this, R.color.unuse_font))
                child.background =
                    ContextCompat.getDrawable(this, R.drawable.custom_option_box_background_default)
            }
        }

        // 선택된 옵션 강조
        selectedView.setTextColor(ContextCompat.getColor(this, R.color.main_blue))
        selectedView.background =
            ContextCompat.getDrawable(this, R.drawable.custom_option_box_background_selected_6dp)
    }

    private fun createDormitoryOption(dormitoryName: String): TextView {
        return TextView(this).apply {
            text = dormitoryName
            textSize = 14f
            setPadding(48, 32, 48, 32)
            setTextColor(ContextCompat.getColor(this@FetchLifestyleActivity, R.color.unuse_font))
            background = ContextCompat.getDrawable(
                this@FetchLifestyleActivity,
                R.drawable.custom_option_box_background_default
            )

            // 간격 추가
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(16, 8, 16, 8) // 좌, 상, 우, 하 간격 설정
            }
            layoutParams = params

            setOnClickListener {
                updateSelectedDormitoryOption(this)
                selectedDormitoryName = dormitoryName
                saveAllSelections() // 변경 즉시 저장
            }
        }
    }

    private fun initNumOfRoommate(selectedValue: String?) {
        val views = listOf(
            binding.num0, binding.num2, binding.num3,
            binding.num4, binding.num5, binding.num6
        )

        // 초기화
        views.forEach { view ->
            if (view.text.toString() == selectedValue) {
                view.setBackgroundResource(R.drawable.custom_option_box_background_selected_6dp)
                view.setTextColor(getColor(R.color.main_blue))
                selectedNumOfRoommate = selectedValue ?: ""
            } else {
                view.setBackgroundResource(R.drawable.custom_option_box_background_default)
                view.setTextColor(getColor(R.color.unuse_font))
            }
        }

        // 클릭 이벤트
        views.forEach { view ->
            view.setOnClickListener {
                updateNumOfRoommate(view, views)
            }
        }
    }

    private fun updateNumOfRoommate(view: TextView, views: List<TextView>) {
        views.forEach {
            it.setBackgroundResource(R.drawable.custom_option_box_background_default)
            it.setTextColor(getColor(R.color.unuse_font))
        }

        view.setBackgroundResource(R.drawable.custom_option_box_background_selected_6dp)
        view.setTextColor(getColor(R.color.main_blue))

        selectedNumOfRoommate = view.text.toString()
    }

    private fun getNumFromTextView(view: TextView): Int {
        return when (view.id) {
            binding.num0.id -> 0
            binding.num2.id -> 2
            binding.num3.id -> 3
            binding.num4.id -> 4
            binding.num5.id -> 5
            binding.num6.id -> 6
            else -> -1
        }
    }

    private fun initAcceptance(selectedValue: String?) {
        val views = listOf(
            binding.dormitoryPass,
            binding.dormitoryWaiting,
            binding.dormitoryNumber
        )

        views.forEach { view ->
            if (view.text.toString() == selectedValue) {
                view.setBackgroundResource(R.drawable.custom_option_box_background_selected_6dp)
                view.setTextColor(getColor(R.color.main_blue))
                selectedAcceptance = selectedValue
            } else {
                view.setBackgroundResource(R.drawable.custom_option_box_background_default)
                view.setTextColor(getColor(R.color.unuse_font))
            }
        }

        views.forEach { view ->
            view.setOnClickListener {
                updateAcceptance(view, views)
            }
        }
    }

    private fun updateAcceptance(view: TextView, views: List<TextView>) {
        views.forEach {
            it.setBackgroundResource(R.drawable.custom_option_box_background_default)
            it.setTextColor(getColor(R.color.unuse_font))
        }

        view.setBackgroundResource(R.drawable.custom_option_box_background_selected_6dp)
        view.setTextColor(getColor(R.color.main_blue))

        selectedAcceptance = view.text.toString()
    }

    private fun initWakeUpTime(savedValue: Int) {
        val views = listOf(
            binding.tvWakeup1, binding.tvWakeup2, binding.tvWakeup3, binding.tvWakeup4,
            binding.tvWakeup5, binding.tvWakeup6, binding.tvWakeup7, binding.tvWakeup8,
            binding.tvWakeup9, binding.tvWakeup10, binding.tvWakeup11, binding.tvWakeup12
        )

        val (meridian, hour) = if (savedValue in 0..23) {
            if (savedValue == 0) "오전" to 12
            else if (savedValue in 1..11) "오전" to savedValue
            else if (savedValue == 12) "오후" to 12
            else "오후" to (savedValue - 12)
        } else {
            null to -1
        }

        // AM/PM 초기화
        initWakeUpMeridian(meridian)

        // 시간 선택 초기화
        views.forEach { view ->
            if (getWakeUpTimeFromTextView(view) == hour) {
                view.setBackgroundResource(R.drawable.custom_option_box_background_selected_6dp)
                view.setTextColor(getColor(R.color.main_blue))
                selectedWakeUpTime = savedValue
            } else {
                view.setBackgroundResource(R.drawable.custom_option_box_background_default)
                view.setTextColor(getColor(R.color.unuse_font))
            }
        }

        views.forEach { view ->
            view.setOnClickListener {
                updateWakeUpTime(view, views)
            }
        }
    }

    private fun updateWakeUpTime(view: TextView, views: List<TextView>) {
        views.forEach {
            it.setBackgroundResource(R.drawable.custom_option_box_background_default)
            it.setTextColor(getColor(R.color.unuse_font))
        }

        view.setBackgroundResource(R.drawable.custom_option_box_background_selected_6dp)
        view.setTextColor(getColor(R.color.main_blue))

        val hour = getWakeUpTimeFromTextView(view)

        selectedWakeUpTime = when (selectedWakeUpMeridian) {
            "오전" -> if (hour == 12) 0 else hour
            "오후" -> if (hour == 12) 12 else hour + 12
            else -> -1
        }
    }

    private fun initWakeUpMeridian(selectedValue: String?) {
        val views = listOf(binding.tvWakeAm, binding.tvWakePm)

        val mappedValue = when (selectedValue) {
            "오전" -> "AM"
            "오후" -> "PM"
            else -> null
        }

        views.forEach { view ->
            if (view.text.toString() == mappedValue) {
                view.setTextColor(getColor(R.color.main_blue))
                selectedWakeUpMeridian = selectedValue
            } else {
                view.setTextColor(getColor(R.color.unuse_font))
            }
        }

        views.forEach { view ->
            view.setOnClickListener {
                updateWakeUpMeridian(view, views)
            }
        }
    }

    private fun updateWakeUpMeridian(view: TextView, views: List<TextView>) {
        views.forEach { it.setTextColor(getColor(R.color.unuse_font)) }
        view.setTextColor(getColor(R.color.main_blue))

        selectedWakeUpMeridian = when (view.text.toString()) {
            "AM" -> "오전"
            "PM" -> "오후"
            else -> null
        }
    }

    private fun getWakeUpTimeFromTextView(view: TextView): Int {
        return when (view.id) {
            binding.tvWakeup1.id -> 1
            binding.tvWakeup2.id -> 2
            binding.tvWakeup3.id -> 3
            binding.tvWakeup4.id -> 4
            binding.tvWakeup5.id -> 5
            binding.tvWakeup6.id -> 6
            binding.tvWakeup7.id -> 7
            binding.tvWakeup8.id -> 8
            binding.tvWakeup9.id -> 9
            binding.tvWakeup10.id -> 10
            binding.tvWakeup11.id -> 11
            binding.tvWakeup12.id -> 12
            else -> -1
        }
    }

    private fun initSleepingMeridian(selectedValue: String?) {
        val views = listOf(binding.tvSleepAm, binding.tvSleepPm)

        val mappedValue = when (selectedValue) {
            "오전" -> "AM"
            "오후" -> "PM"
            else -> null
        }

        // 초기화: 저장된 값과 일치하는 TextView의 상태를 설정
        views.forEach { view ->
            if (view.text.toString() == mappedValue) {
                view.setTextColor(getColor(R.color.main_blue)) // 선택된 텍스트는 파란색
                selectedSleepingMeridian = selectedValue
            } else {
                view.setTextColor(getColor(R.color.unuse_font)) // 기본 텍스트 색상
            }
        }

        // 클릭 이벤트 처리
        views.forEach { view ->
            view.setOnClickListener {
                updateSleepingMeridian(view, views)
            }
        }
    }

    private fun updateSleepingMeridian(view: TextView, views: List<TextView>) {
        // 모든 텍스트 색상 초기화
        views.forEach { it.setTextColor(getColor(R.color.unuse_font)) }

        // 선택된 텍스트 색상 변경
        view.setTextColor(getColor(R.color.main_blue))

        selectedSleepingMeridian = when (view.text.toString()) {
            "AM" -> "오전"
            "PM" -> "오후"
            else -> null
        }
    }

    private fun initSleepingTime(savedValue: Int) {
        val views = listOf(
            binding.tvSleep1, binding.tvSleep2, binding.tvSleep3, binding.tvSleep4,
            binding.tvSleep5, binding.tvSleep6, binding.tvSleep7, binding.tvSleep8,
            binding.tvSleep9, binding.tvSleep10, binding.tvSleep11, binding.tvSleep12
        )

        // 초기화
        views.forEach { view ->
            if (getSleepTimeFromTextView(view) == savedValue) {
                view.setBackgroundResource(R.drawable.custom_option_box_background_selected_6dp)
                view.setTextColor(getColor(R.color.main_blue))
                selectedSleepingTime = savedValue
            } else {
                view.setBackgroundResource(R.drawable.custom_option_box_background_default)
                view.setTextColor(getColor(R.color.unuse_font))
            }
        }

        // 클릭 이벤트
        views.forEach { view ->
            view.setOnClickListener {
                updateSleepingTime(view, views)
            }
        }
    }

    private fun updateSleepingTime(view: TextView, views: List<TextView>) {
        views.forEach {
            it.setBackgroundResource(R.drawable.custom_option_box_background_default)
            it.setTextColor(getColor(R.color.unuse_font))
        }

        view.setBackgroundResource(R.drawable.custom_option_box_background_selected_6dp)
        view.setTextColor(getColor(R.color.main_blue))

        val hour = getSleepTimeFromTextView(view)

        selectedSleepingTime = when (selectedSleepingMeridian) {
            "오전" -> if (hour == 12) 0 else hour
            "오후" -> if (hour == 12) 12 else hour + 12
            else -> -1
        }
    }

    private fun getSleepTimeFromTextView(view: TextView): Int {
        return when (view.id) {
            binding.tvSleep1.id -> 1
            binding.tvSleep2.id -> 2
            binding.tvSleep3.id -> 3
            binding.tvSleep4.id -> 4
            binding.tvSleep5.id -> 5
            binding.tvSleep6.id -> 6
            binding.tvSleep7.id -> 7
            binding.tvSleep8.id -> 8
            binding.tvSleep9.id -> 9
            binding.tvSleep10.id -> 10
            binding.tvSleep11.id -> 11
            binding.tvSleep12.id -> 12
            else -> -1
        }
    }

    private fun initTurnOffMeridian(selectedValue: String?) {
        val views = listOf(binding.tvLightOffAm, binding.tvLightOffPm)

        val mappedValue = when (selectedValue) {
            "오전" -> "AM"
            "오후" -> "PM"
            else -> null
        }

        // 초기화: 저장된 값과 일치하는 TextView의 상태를 설정
        views.forEach { view ->
            if (view.text.toString() == mappedValue) {
                view.setTextColor(getColor(R.color.main_blue)) // 선택된 텍스트는 파란색
                selectedTurnOffMeridian = selectedValue
            } else {
                view.setTextColor(getColor(R.color.unuse_font)) // 기본 텍스트 색상
            }
        }

        // 클릭 이벤트 처리
        views.forEach { view ->
            view.setOnClickListener {
                updateTurnOffMeridian(view, views)
            }
        }
    }

    private fun updateTurnOffMeridian(view: TextView, views: List<TextView>) {
        // 모든 텍스트 색상 초기화
        views.forEach { it.setTextColor(getColor(R.color.unuse_font)) }

        // 선택된 텍스트 색상 변경
        view.setTextColor(getColor(R.color.main_blue))

        // 선택된 값 저장
//        selectedWakeUpMeridian = view.text.toString()
        selectedTurnOffMeridian = when (view.text.toString()) {
            "AM" -> "오전"
            "PM" -> "오후"
            else -> null
        }
    }

    private fun initTurnOffTime(savedValue: Int) {
        val views = listOf(
            binding.tvLightOff1, binding.tvLightOff2, binding.tvLightOff3, binding.tvLightOff4,
            binding.tvLightOff5, binding.tvLightOff6, binding.tvLightOff7, binding.tvLightOff8,
            binding.tvLightOff9, binding.tvLightOff10, binding.tvLightOff11, binding.tvLightOff12
        )

        // 초기화
        views.forEach { view ->
            if (getTurnOffTimeFromTextView(view) == savedValue) {
                view.setBackgroundResource(R.drawable.custom_option_box_background_selected_6dp)
                view.setTextColor(getColor(R.color.main_blue))
                selectedTurnOffTime = savedValue
            } else {
                view.setBackgroundResource(R.drawable.custom_option_box_background_default)
                view.setTextColor(getColor(R.color.unuse_font))
            }
        }

        // 클릭 이벤트
        views.forEach { view ->
            view.setOnClickListener {
                updateTurnOffTime(view, views)
            }
        }
    }

    private fun updateTurnOffTime(view: TextView, views: List<TextView>) {
        views.forEach {
            it.setBackgroundResource(R.drawable.custom_option_box_background_default)
            it.setTextColor(getColor(R.color.unuse_font))
        }

        view.setBackgroundResource(R.drawable.custom_option_box_background_selected_6dp)
        view.setTextColor(getColor(R.color.main_blue))

        val hour = getTurnOffTimeFromTextView(view)

        selectedTurnOffTime = when (selectedTurnOffMeridian) {
            "오전" -> if (hour == 12) 0 else hour
            "오후" -> if (hour == 12) 12 else hour + 12
            else -> -1
        }
    }

    private fun getTurnOffTimeFromTextView(view: TextView): Int {
        return when (view.id) {
            binding.tvLightOff1.id -> 1
            binding.tvLightOff2.id -> 2
            binding.tvLightOff3.id -> 3
            binding.tvLightOff4.id -> 4
            binding.tvLightOff5.id -> 5
            binding.tvLightOff6.id -> 6
            binding.tvLightOff7.id -> 7
            binding.tvLightOff8.id -> 8
            binding.tvLightOff9.id -> 9
            binding.tvLightOff10.id -> 10
            binding.tvLightOff11.id -> 11
            binding.tvLightOff12.id -> 12
            else -> -1
        }
    }

    private fun initSmoking(selectedValue: String?) {
        val views =
            listOf(binding.smokeNo, binding.smokePaper, binding.smokeEletronic, binding.smokeWater)

        views.forEach { view ->
            if (view.text.toString() == selectedValue) {
                view.setBackgroundResource(R.drawable.custom_option_box_background_selected_6dp)
                view.setTextColor(getColor(R.color.main_blue))
                selectedSmoking = selectedValue
            } else {
                view.setBackgroundResource(R.drawable.custom_option_box_background_default)
                view.setTextColor(getColor(R.color.unuse_font))
            }
        }
        views.forEach { view ->
            view.setOnClickListener {
                updateSmoking(view, views)
            }
        }
    }

    private fun updateSmoking(view: TextView, views: List<TextView>) {
        views.forEach {
            it.setBackgroundResource(R.drawable.custom_option_box_background_default)
            it.setTextColor(getColor(R.color.unuse_font))
        }
        view.setBackgroundResource(R.drawable.custom_option_box_background_selected_6dp)
        view.setTextColor(getColor(R.color.main_blue))
        selectedSmoking = view.text.toString()
    }

    private fun initSleepingHabits(selectedValues: List<String>) {
        val views = listOf(
            binding.sleepHabitNo,
            binding.sleepHabitNoise,
            binding.sleepHabitTeeth,
            binding.sleepHabitMoveSick,
            binding.sleepHabitSpeak,
            binding.sleepHabitMove
        )

        views.forEach { view ->
            if (selectedValues.contains(view.text.toString())) {
                view.setBackgroundResource(R.drawable.custom_option_box_background_selected_6dp)
                view.setTextColor(getColor(R.color.main_blue))
                selectedSleepingHabits.add(view.text.toString())
            } else {
                view.setBackgroundResource(R.drawable.custom_option_box_background_default)
                view.setTextColor(getColor(R.color.unuse_font))
            }
        }

        views.forEach { view ->
            view.setOnClickListener {
                updateSleepingHabits(view, views)
            }
        }
    }

    private fun updateSleepingHabits(view: TextView, views: List<TextView>) {
        if (selectedSleepingHabits.contains(view.text.toString())) {
            selectedSleepingHabits.remove(view.text.toString())
            view.setBackgroundResource(R.drawable.custom_option_box_background_default)
            view.setTextColor(getColor(R.color.unuse_font))
        } else {
            selectedSleepingHabits.add(view.text.toString())
            view.setBackgroundResource(R.drawable.custom_option_box_background_selected_6dp)
            view.setTextColor(getColor(R.color.main_blue))
        }
    }

    private fun initAc(selectedValue: String?) {
        val views = listOf(
            binding.acNo, binding.acWeak, binding.acEnough, binding.acStrong
        )

        // 초기화
        views.forEach { view ->
            if (view.text.toString() == selectedValue) {
                view.setBackgroundResource(R.drawable.custom_option_box_background_selected_6dp)
                view.setTextColor(getColor(R.color.main_blue))
                selectedAc = selectedValue ?: ""
            } else {
                view.setBackgroundResource(R.drawable.custom_option_box_background_default)
                view.setTextColor(getColor(R.color.unuse_font))
            }
        }

        // 클릭 이벤트
        views.forEach { view ->
            view.setOnClickListener {
                updateAc(view, views)
            }
        }
    }

    private fun updateAc(view: TextView, views: List<TextView>) {
        views.forEach {
            it.setBackgroundResource(R.drawable.custom_option_box_background_default)
            it.setTextColor(getColor(R.color.unuse_font))
        }

        view.setBackgroundResource(R.drawable.custom_option_box_background_selected_6dp)
        view.setTextColor(getColor(R.color.main_blue))

        selectedAc = view.text.toString()
    }

    private fun initHeater(selectedValue: String?) {
        val views = listOf(
            binding.heaterNo, binding.heaterWeak, binding.heaterEnough,
            binding.heaterStrong
        )

        // 초기화
        views.forEach { view ->
            if (view.text.toString() == selectedValue) {
                view.setBackgroundResource(R.drawable.custom_option_box_background_selected_6dp)
                view.setTextColor(getColor(R.color.main_blue))
                selectedHeater = selectedValue ?: ""
            } else {
                view.setBackgroundResource(R.drawable.custom_option_box_background_default)
                view.setTextColor(getColor(R.color.unuse_font))
            }
        }

        // 클릭 이벤트
        views.forEach { view ->
            view.setOnClickListener {
                updateHeater(view, views)
            }
        }
    }

    private fun updateHeater(view: TextView, views: List<TextView>) {
        views.forEach {
            it.setBackgroundResource(R.drawable.custom_option_box_background_default)
            it.setTextColor(getColor(R.color.unuse_font))
        }

        view.setBackgroundResource(R.drawable.custom_option_box_background_selected_6dp)
        view.setTextColor(getColor(R.color.main_blue))

        selectedHeater = view.text.toString()
    }

    private fun initLifePattern(selectedValue: String?) {
        val views = listOf(binding.livingMorning, binding.livingDawn)

        views.forEach { view ->
            if (view.text.toString() == selectedValue) {
                view.setBackgroundResource(R.drawable.custom_option_box_background_selected_6dp)
                view.setTextColor(getColor(R.color.main_blue))
                selectedLifePattern = selectedValue
            } else {
                view.setBackgroundResource(R.drawable.custom_option_box_background_default)
                view.setTextColor(getColor(R.color.unuse_font))
            }
        }
        views.forEach { view ->
            view.setOnClickListener {
                updateLifePattern(view, views)
            }
        }
    }

    private fun updateLifePattern(view: TextView, views: List<TextView>) {
        views.forEach {
            it.setBackgroundResource(R.drawable.custom_option_box_background_default)
            it.setTextColor(getColor(R.color.unuse_font))
        }
        view.setBackgroundResource(R.drawable.custom_option_box_background_selected_6dp)
        view.setTextColor(getColor(R.color.main_blue))
        selectedLifePattern = view.text.toString()
    }

    private fun initIntimacy(selectedValue: String?) {
        val views = listOf(binding.friendlyNo, binding.friendlyEnough, binding.friendlyYes)

        views.forEach { view ->
            if (view.text.toString() == selectedValue) {
                view.setBackgroundResource(R.drawable.custom_option_box_background_selected_6dp)
                view.setTextColor(getColor(R.color.main_blue))
                selectedIntimacy = selectedValue
            } else {
                view.setBackgroundResource(R.drawable.custom_option_box_background_default)
                view.setTextColor(getColor(R.color.unuse_font))
            }
        }
        views.forEach { view ->
            view.setOnClickListener {
                updateIntimacy(view, views)
            }
        }
    }

    private fun updateIntimacy(view: TextView, views: List<TextView>) {
        views.forEach {
            it.setBackgroundResource(R.drawable.custom_option_box_background_default)
            it.setTextColor(getColor(R.color.unuse_font))
        }
        view.setBackgroundResource(R.drawable.custom_option_box_background_selected_6dp)
        view.setTextColor(getColor(R.color.main_blue))
        selectedIntimacy = view.text.toString()
    }

    private fun initCanShare(selectedValue: String?) {
        val views = listOf(
            binding.shareNothing,
            binding.shareTissue,
            binding.shareCloths,
            binding.shareEverything
        )

        views.forEach { view ->
            if (view.text.toString() == selectedValue) {
                view.setBackgroundResource(R.drawable.custom_option_box_background_selected_6dp)
                view.setTextColor(getColor(R.color.main_blue))
                selectedCanShare = selectedValue
            } else {
                view.setBackgroundResource(R.drawable.custom_option_box_background_default)
                view.setTextColor(getColor(R.color.unuse_font))
            }
        }
        views.forEach { view ->
            view.setOnClickListener {
                updateCanShare(view, views)
            }
        }
    }

    private fun updateCanShare(view: TextView, views: List<TextView>) {
        views.forEach {
            it.setBackgroundResource(R.drawable.custom_option_box_background_default)
            it.setTextColor(getColor(R.color.unuse_font))
        }
        view.setBackgroundResource(R.drawable.custom_option_box_background_selected_6dp)
        view.setTextColor(getColor(R.color.main_blue))
        selectedCanShare = view.text.toString()
    }

    private fun initIsPlayGame(selectedValue: String?) {
        val views = listOf(binding.gameNo, binding.gameKeyboard, binding.gameVoice)

        views.forEach { view ->
            if (view.text.toString() == selectedValue) {
                view.setBackgroundResource(R.drawable.custom_option_box_background_selected_6dp)
                view.setTextColor(getColor(R.color.main_blue))
                selectedIsPlayGame = selectedValue
            } else {
                view.setBackgroundResource(R.drawable.custom_option_box_background_default)
                view.setTextColor(getColor(R.color.unuse_font))
            }
        }
        views.forEach { view ->
            view.setOnClickListener {
                updateIsPlayGame(view, views)
            }
        }
    }

    private fun updateIsPlayGame(view: TextView, views: List<TextView>) {
        views.forEach {
            it.setBackgroundResource(R.drawable.custom_option_box_background_default)
            it.setTextColor(getColor(R.color.unuse_font))
        }
        view.setBackgroundResource(R.drawable.custom_option_box_background_selected_6dp)
        view.setTextColor(getColor(R.color.main_blue))
        selectedIsPlayGame = view.text.toString()
    }

    private fun initIsPhoneCall(selectedValue: String?) {
        val views = listOf(binding.callNo, binding.callHurry, binding.callFrequently)

        views.forEach { view ->
            if (view.text.toString() == selectedValue) {
                view.setBackgroundResource(R.drawable.custom_option_box_background_selected_6dp)
                view.setTextColor(getColor(R.color.main_blue))
                selectedIsPhoneCall = selectedValue
            } else {
                view.setBackgroundResource(R.drawable.custom_option_box_background_default)
                view.setTextColor(getColor(R.color.unuse_font))
            }
        }
        views.forEach { view ->
            view.setOnClickListener {
                updateIsPhoneCall(view, views)
            }
        }
    }

    private fun updateIsPhoneCall(view: TextView, views: List<TextView>) {
        views.forEach {
            it.setBackgroundResource(R.drawable.custom_option_box_background_default)
            it.setTextColor(getColor(R.color.unuse_font))
        }
        view.setBackgroundResource(R.drawable.custom_option_box_background_selected_6dp)
        view.setTextColor(getColor(R.color.main_blue))
        selectedIsPhoneCall = view.text.toString()
    }

    private fun initIsStudying(selectedValue: String?) {
        val views = listOf(binding.studyNo, binding.studyExam, binding.studyEveryday)

        views.forEach { view ->
            if (view.text.toString() == selectedValue) {
                view.setBackgroundResource(R.drawable.custom_option_box_background_selected_6dp)
                view.setTextColor(getColor(R.color.main_blue))
                selectedStudying = selectedValue
            } else {
                view.setBackgroundResource(R.drawable.custom_option_box_background_default)
                view.setTextColor(getColor(R.color.unuse_font))
            }
        }
        views.forEach { view ->
            view.setOnClickListener {
                updateIsStudying(view, views)
            }
        }
    }

    private fun updateIsStudying(view: TextView, views: List<TextView>) {
        views.forEach {
            it.setBackgroundResource(R.drawable.custom_option_box_background_default)
            it.setTextColor(getColor(R.color.unuse_font))
        }
        view.setBackgroundResource(R.drawable.custom_option_box_background_selected_6dp)
        view.setTextColor(getColor(R.color.main_blue))
        selectedStudying = view.text.toString()
    }

    private fun initIntake(selectedValue: String?) {
        val views = listOf(
            binding.eatingNo,
            binding.eatingDrink,
            binding.eatingSnack,
            binding.eatingDelivery
        )

        views.forEach { view ->
            if (view.text.toString() == selectedValue) {
                view.setBackgroundResource(R.drawable.custom_option_box_background_selected_6dp)
                view.setTextColor(getColor(R.color.main_blue))
                selectedIntake = selectedValue
            } else {
                view.setBackgroundResource(R.drawable.custom_option_box_background_default)
                view.setTextColor(getColor(R.color.unuse_font))
            }
        }
        views.forEach { view ->
            view.setOnClickListener {
                updateIntake(view, views)
            }
        }
    }

    private fun updateIntake(view: TextView, views: List<TextView>) {
        views.forEach {
            it.setBackgroundResource(R.drawable.custom_option_box_background_default)
            it.setTextColor(getColor(R.color.unuse_font))
        }
        view.setBackgroundResource(R.drawable.custom_option_box_background_selected_6dp)
        view.setTextColor(getColor(R.color.main_blue))
        selectedIntake = view.text.toString()
    }

    private fun initCleanSensitivity(selectedValue: String?) {
        val views = listOf(
            binding.clean1, binding.clean2, binding.clean3,
            binding.clean4, binding.clean5
        )

        // 초기화
        views.forEach { view ->
            if (view.text.toString() == selectedValue) {
                view.setBackgroundResource(R.drawable.custom_option_box_background_selected_6dp)
                view.setTextColor(getColor(R.color.main_blue))
                selectedCleanSensitivity = selectedValue ?: ""
            } else {
                view.setBackgroundResource(R.drawable.custom_option_box_background_default)
                view.setTextColor(getColor(R.color.unuse_font))
            }
        }

        // 클릭 이벤트
        views.forEach { view ->
            view.setOnClickListener {
                updateCleanSen(view, views)
            }
        }
    }

    private fun updateCleanSen(view: TextView, views: List<TextView>) {
        views.forEach {
            it.setBackgroundResource(R.drawable.custom_option_box_background_default)
            it.setTextColor(getColor(R.color.unuse_font))
        }

        view.setBackgroundResource(R.drawable.custom_option_box_background_selected_6dp)
        view.setTextColor(getColor(R.color.main_blue))

        selectedCleanSensitivity = view.text.toString()
    }

    private fun initNoiseSensitivity(selectedValue: String?) {
        val views = listOf(
            binding.noise1, binding.noise2, binding.noise3,
            binding.noise4, binding.noise5
        )

        // 초기화
        views.forEach { view ->
            if (view.text.toString() == selectedValue) {
                view.setBackgroundResource(R.drawable.custom_option_box_background_selected_6dp)
                view.setTextColor(getColor(R.color.main_blue))
                selectedNoiseSensitivity = selectedValue ?: ""
            } else {
                view.setBackgroundResource(R.drawable.custom_option_box_background_default)
                view.setTextColor(getColor(R.color.unuse_font))
            }
        }

        // 클릭 이벤트
        views.forEach { view ->
            view.setOnClickListener {
                updateNoise(view, views)
            }
        }
    }

    private fun updateNoise(view: TextView, views: List<TextView>) {
        views.forEach {
            it.setBackgroundResource(R.drawable.custom_option_box_background_default)
            it.setTextColor(getColor(R.color.unuse_font))
        }

        view.setBackgroundResource(R.drawable.custom_option_box_background_selected_6dp)
        view.setTextColor(getColor(R.color.main_blue))

        selectedNoiseSensitivity = view.text.toString()
    }

    private fun initCleaningFrequency(selectedValue: String?) {
        val views = listOf(
            binding.cleanFrequencyMonth,
            binding.cleanFrequencyEvery2weeks,
            binding.cleanFrequencyEveryWeek,
            binding.cleanFrequency2days,
            binding.cleanFrequencyEveryday
        )

        views.forEach { view ->
            if (view.text.toString() == selectedValue) {
                view.setBackgroundResource(R.drawable.custom_option_box_background_selected_6dp)
                view.setTextColor(getColor(R.color.main_blue))
                selectedCleaningFrequency = selectedValue
            } else {
                view.setBackgroundResource(R.drawable.custom_option_box_background_default)
                view.setTextColor(getColor(R.color.unuse_font))
            }
        }
        views.forEach { view ->
            view.setOnClickListener {
                updateCleanFrequency(view, views)
            }
        }
    }

    private fun updateCleanFrequency(view: TextView, views: List<TextView>) {
        views.forEach {
            it.setBackgroundResource(R.drawable.custom_option_box_background_default)
            it.setTextColor(getColor(R.color.unuse_font))
        }
        view.setBackgroundResource(R.drawable.custom_option_box_background_selected_6dp)
        view.setTextColor(getColor(R.color.main_blue))
        selectedCleaningFrequency = view.text.toString()
    }

    private fun initDrinkingFrequency(selectedValue: String?) {
        val views = listOf(
            binding.drinkNo,
            binding.drinkMonth,
            binding.drinkWeek,
            binding.drink4Weeks,
            binding.drinkEveryday
        )

        views.forEach { view ->
            if (view.text.toString() == selectedValue) {
                view.setBackgroundResource(R.drawable.custom_option_box_background_selected_6dp)
                view.setTextColor(getColor(R.color.main_blue))
                selectedDrinkingFrequency = selectedValue
            } else {
                view.setBackgroundResource(R.drawable.custom_option_box_background_default)
                view.setTextColor(getColor(R.color.unuse_font))
            }
        }
        views.forEach { view ->
            view.setOnClickListener {
                updateDrink(view, views)
            }
        }
    }

    private fun updateDrink(view: TextView, views: List<TextView>) {
        views.forEach {
            it.setBackgroundResource(R.drawable.custom_option_box_background_default)
            it.setTextColor(getColor(R.color.unuse_font))
        }
        view.setBackgroundResource(R.drawable.custom_option_box_background_selected_6dp)
        view.setTextColor(getColor(R.color.main_blue))
        selectedDrinkingFrequency = view.text.toString()
    }

    private fun initPersonality(selectedValues: List<String>) {
        val views = listOf(
            binding.personalityQuite,
            binding.personalityActivity,
            binding.personalityTmt,
            binding.personalityClean,
            binding.personalityShame,
            binding.personalityHome,
            binding.personalityGoOut,
            binding.personalityHurry,
            binding.personalityRelaxed,
            binding.personalityShy,
            binding.personalityLazy,
            binding.personalityDiligent
        )

        views.forEach { view ->
            if (selectedValues.contains(view.text.toString())) {
                view.setBackgroundResource(R.drawable.custom_option_box_background_selected_6dp)
                view.setTextColor(getColor(R.color.main_blue))
                selectedPersonality.add(view.text.toString())
            } else {
                view.setBackgroundResource(R.drawable.custom_option_box_background_default)
                view.setTextColor(getColor(R.color.unuse_font))
            }
        }

        views.forEach { view ->
            view.setOnClickListener {
                updatePersonality(view, views)
            }
        }
    }

    private fun updatePersonality(view: TextView, views: List<TextView>) {
        if (selectedPersonality.contains(view.text.toString())) {
            selectedPersonality.remove(view.text.toString())
            view.setBackgroundResource(R.drawable.custom_option_box_background_default)
            view.setTextColor(getColor(R.color.unuse_font))
        } else {
            selectedPersonality.add(view.text.toString())
            view.setBackgroundResource(R.drawable.custom_option_box_background_selected_6dp)
            view.setTextColor(getColor(R.color.main_blue))
        }
    }

    private fun initMbti(selectedValue: String?) {
        val views = listOf(
            binding.mbtiIstj,
            binding.mbtiIsfj,
            binding.mbtiInfj,
            binding.mbtiIntj,
            binding.mbtiIstp,
            binding.mbtiISFP,
            binding.mbtiInfp,
            binding.mbtiINTP,
            binding.mbtiEstp,
            binding.mbtiEsfp,
            binding.mbtiEnfp,
            binding.mbtiEntp,
            binding.mbtiEstj,
            binding.mbtiEsfj,
            binding.mbtiEnfj,
            binding.mbtiEntj
        )

        views.forEach { view ->
            if (view.text.toString() == selectedValue) {
                view.setBackgroundResource(R.drawable.custom_option_box_background_selected_6dp)
                view.setTextColor(getColor(R.color.main_blue))
                selectedMbti = selectedValue
            } else {
                view.setBackgroundResource(R.drawable.custom_option_box_background_default)
                view.setTextColor(getColor(R.color.unuse_font))
            }
        }
        views.forEach { view ->
            view.setOnClickListener {
                updateMbti(view, views)
            }
        }
    }

    private fun updateMbti(view: TextView, views: List<TextView>) {
        views.forEach {
            it.setBackgroundResource(R.drawable.custom_option_box_background_default)
            it.setTextColor(getColor(R.color.unuse_font))
        }
        view.setBackgroundResource(R.drawable.custom_option_box_background_selected_6dp)
        view.setTextColor(getColor(R.color.main_blue))
        selectedMbti = view.text.toString()
    }

    private fun initETCInfo(savedValue: String?) {
        savedValue?.let {
            binding.etSelectionInfo.setText(it)
            selectedETCInfo = it
        }

        val inputFilter = InputFilter.LengthFilter(maxLength)
        binding.etSelectionInfo.filters = arrayOf(inputFilter)

        binding.etSelectionInfo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val inputText = s?.toString() ?: ""

                binding.tvCurrentEt.text = "${inputText.length} / $maxLength"

                if (inputText.length > maxLength) {
                    showToast("최대 200자까지만 작성할 수 있습니다.")
                    binding.etSelectionInfo.setText(inputText.substring(0, maxLength))
                    binding.etSelectionInfo.setSelection(maxLength)
                }
                selectedETCInfo = inputText
            }
        })
    }

    private fun saveAllSelections() {
        val editor = spf.edit()
        with(editor) {
            putInt("user_admissionYear", selectedAdmissionYear)
            putString("user_dormName", selectedDormitoryName)
            putString("user_numOfRoommate", selectedNumOfRoommate)
            putString("user_dormJoiningStatus", selectedAcceptance)
            putInt("user_wakeUpTime", selectedWakeUpTime)
            putInt("user_sleepingTime", selectedSleepingTime)
            putInt("user_turnOffTime", selectedTurnOffTime)
            putString("user_smokingStatus", selectedSmoking)
            Log.d("FetchLifestyleActivity", "$selectedSmoking")
            putStringSet("user_sleepingHabits", selectedSleepingHabits.toSet())
            putString("user_coolingIntensity", selectedAc)
            putString("user_heatingIntensity", selectedHeater)
            putString("user_lifePattern", selectedLifePattern)
            putString("user_intimacy", selectedIntimacy)
            putString("user_sharingStatus", selectedCanShare)
            putString("user_gamingStatus", selectedIsPlayGame)
            putString("user_callingStatus", selectedIsPhoneCall)
            putString("user_studyingStatus", selectedStudying)
            putString("user_eatingStatus", selectedIntake)
            putString("user_cleannessSensitivity", selectedCleanSensitivity)
            putString("user_noiseSensitivity", selectedNoiseSensitivity)
            putString("user_cleaningFrequency", selectedCleaningFrequency)
            putString("user_drinkingFrequency", selectedDrinkingFrequency)
            putStringSet("user_personalities", selectedPersonality.toSet())
            putString("user_mbti", selectedMbti)
            putString("user_selfIntroduction", selectedETCInfo)
            apply()
        }
    }

    private fun getStringListFromPreferences(key: String): List<String> {
        val set = spf.getStringSet(key, emptySet())
        return set?.toList() ?: emptyList()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun setFloatingButton() {
        binding.fabGoTop.setOnClickListener {
            binding.nestedScrollView.smoothScrollTo(0, 0)
        }
        val fab = binding.fabGoTop
        val drawable = fab.drawable.mutate() // 현재 아이콘 수정 가능하도록 mutate()
        drawable.setTint(ContextCompat.getColor(this, R.color.main_blue)) // 색상 변경
        fab.setImageDrawable(drawable) // 변경된 아이콘 설정
    }

    private fun sendFetchUserDataToViewModel() {
        val userInfo = UserInfoRequest(
            admissionYear = selectedAdmissionYear.toString(),
            numOfRoommate = selectedNumOfRoommate ?: "",
            dormName = spf.getString("user_dormName", "") ?: "", // 기존 값 유지
            dormJoiningStatus = selectedAcceptance ?: "",
            wakeUpTime = selectedWakeUpTime,
            sleepingTime = selectedSleepingTime,
            turnOffTime = selectedTurnOffTime,
            smokingStatus = selectedSmoking ?: "",
            sleepingHabits = selectedSleepingHabits.toList(),
            coolingIntensity = selectedAc,
            heatingIntensity = selectedHeater,
            lifePattern = selectedLifePattern ?: "",
            intimacy = selectedIntimacy ?: "",
            sharingStatus = selectedCanShare ?: "",
            gamingStatus = selectedIsPlayGame ?: "",
            callingStatus = selectedIsPhoneCall ?: "",
            studyingStatus = selectedStudying ?: "",
            eatingStatus = selectedIntake ?: "",
            cleannessSensitivity = selectedCleanSensitivity,
            noiseSensitivity = selectedNoiseSensitivity,
            cleaningFrequency = selectedCleaningFrequency ?: "",
            drinkingFrequency = selectedDrinkingFrequency ?: "",
            personalities = selectedPersonality.toList(),
            mbti = selectedMbti ?: "",
            selfIntroduction = selectedETCInfo ?: ""
        )

        viewModel.fetchUserInfo(
            request = userInfo,
            onSuccess = {
                Log.d("Activity", "User info fetched successfully.")
                saveAllSelections()
            },
            onFailure = { errorMessage ->
                Log.d("Activity", "Failed to fetch user info: $errorMessage")
            })

        Handler(Looper.getMainLooper()).postDelayed({
            finish()  // 현재 액티비티 종료
        }, 400)
    }
}