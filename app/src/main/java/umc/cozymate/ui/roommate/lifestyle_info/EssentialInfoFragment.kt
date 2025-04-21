package umc.cozymate.ui.roommate.lifestyle_info

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationSet
import android.view.animation.TranslateAnimation
import android.widget.TextView
import androidx.fragment.app.Fragment
import umc.cozymate.R
import umc.cozymate.databinding.FragmentEssentialInfoBinding
import umc.cozymate.ui.roommate.RoommateInputInfoActivity

class EssentialInfoFragment : Fragment() {

    private lateinit var binding: FragmentEssentialInfoBinding

    private lateinit var spf: SharedPreferences

    private var wakeAmpmOption: TextView? = null
    private var wakeTimeOption: TextView? = null
    private var sleepAmpmOption: TextView? = null
    private var sleepTimeOption: TextView? = null
    private var lightOffAmpmOption: TextView? = null
    private var lightOffTimeOption: TextView? = null

    private var smokeOption: TextView? = null
    private var selectedSleepHabits: MutableList<String> = mutableListOf()
    private var acOption: TextView? = null
    private var heaterOption: TextView? = null
    private var livingPatternOption: TextView? = null
    private var friendlyOption: TextView? = null
    private var shareOption: TextView? = null
    private var gameOption: TextView? = null
    private var callOption: TextView? = null
    private var studyOption: TextView? = null
    private var eatingOption: TextView? = null
    private var cleanOption: TextView? = null
    private var noiseOption: TextView? = null
    private var cleanFrequencyOption: TextView? = null
    private var drinkingFrequencyOption: TextView? = null
    private var selectedPersonalitys: MutableList<String> = mutableListOf()
    private var mbtiOption: TextView? = null

    private val handler = Handler(Looper.getMainLooper())
    private var runnable: Runnable? = null
    private val delayInMillis: Long = 500

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEssentialInfoBinding.inflate(layoutInflater, container, false)

        spf = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        initPage()

        updateNextButtonState()

        return binding.root
    }

    private fun saveToSPF(key: String, value: String) {
        spf.edit().putString(key, value).apply()
    }

    private fun saveToSPFList(key: String, value: List<String>) {
        spf.edit().putStringSet(key, value.toSet()).apply()
    }

    private fun saveToSPFInt(key: String, value: Int) {
        spf.edit().putInt(key, value).apply()
    }

    private fun updateSelectedStringOption(
        view: View,
        key: String,
        value: String,
        currentOption: TextView?
    ) {
        currentOption?.apply {
            setTextColor(resources.getColor(R.color.unuse_font, null))
            background =
                resources.getDrawable(R.drawable.custom_option_box_background_default, null)
        }

        val selectedTextView = view as TextView
        selectedTextView.apply {
            setTextColor(resources.getColor(R.color.main_blue, null))
            background =
                resources.getDrawable(R.drawable.custom_option_box_background_selected_6dp, null)
        }

        saveToSPF(key, value)
        updateNextButtonState()
    }

    private fun toggleMultiSelection(
        view: View,
        key: String,
        value: String,
        selectedList: MutableList<String>
    ) {
        val textView = view as TextView
        if (selectedList.contains(value)) {
            selectedList.remove(value)
            textView.setTextColor(resources.getColor(R.color.unuse_font, null))
            textView.background =
                resources.getDrawable(R.drawable.custom_option_box_background_default, null)
        } else {
            selectedList.add(value)
            textView.setTextColor(resources.getColor(R.color.main_blue, null))
            textView.background =
                resources.getDrawable(R.drawable.custom_option_box_background_selected_6dp, null)
        }

        saveToSPFList(key, selectedList)
        updateNextButtonState()
    }

    private fun updateSelectedIntOption(
        view: View,
        key: String,
        value: Int,
        currentOption: TextView?
    ) {
        currentOption?.apply {
            setTextColor(resources.getColor(R.color.unuse_font, null))
            background =
                resources.getDrawable(R.drawable.custom_option_box_background_default, null)
        }

        val selectedTextView = view as TextView
        selectedTextView.apply {
            setTextColor(resources.getColor(R.color.main_blue, null))
            background =
                resources.getDrawable(R.drawable.custom_option_box_background_selected_6dp, null)
        }

        saveToSPFInt(key, value)
        updateNextButtonState()
    }

    private fun resetDebounceTimer(action: () -> Unit) {
        runnable?.let { handler.removeCallbacks(it) }  // 기존 타이머 취소
        runnable = Runnable { action() }  // 새로운 작업 설정
        handler.postDelayed(runnable!!, delayInMillis)  // 1초 후 실행
    }

    private fun initPage() {
        initWakeAmpm()
        initWakeTime()
        initSleepAmpm()
        initSleepTime()
        initTurnOffAmpm()
        initTurnOffTime()
        initSmoke()
        initSleepHabit()
        initAc()
        initHeater()
        initLiving()
        initIntimacy()
        initShare()
        initGame()
        initCall()
        initStudy()
        initEating()
        initClean()
        initNoise()
        initCleanFrequency()
        initDrinkingFrequency()
        initPersonality()
        initMbti()
    }

    private fun convertTo24Hour(ampm: String?, hour: Int): Int {
        return when (ampm) {
            "오전" -> if (hour == 12) 0 else hour
            "오후" -> if (hour == 12) 12 else hour + 12
            else -> hour // null 이거나 비정상 값일 경우 그대로
        }
    }

    private fun initWakeAmpm() {
        val wakeAmpmTexts = listOf(
            binding.tvWakeAm to "오전",
            binding.tvWakePm to "오후"
        )
        for ((textView, value) in wakeAmpmTexts) {
            textView.setOnClickListener {
                wakeAmpmOption?.setTextColor(resources.getColor(R.color.unuse_font, null))
                wakeAmpmOption = textView
                wakeAmpmOption?.setTextColor(resources.getColor(R.color.main_blue, null))
                resetDebounceTimer { showSleepLayout() }
            }
        }
    }

    private fun initWakeTime() {
        val wakeUpTimeTexts = listOf(
            binding.tvWakeup1 to 1, binding.tvWakeup2 to 2, binding.tvWakeup3 to 3,
            binding.tvWakeup4 to 4, binding.tvWakeup5 to 5, binding.tvWakeup6 to 6,
            binding.tvWakeup7 to 7, binding.tvWakeup8 to 8, binding.tvWakeup9 to 9,
            binding.tvWakeup10 to 10, binding.tvWakeup11 to 11, binding.tvWakeup12 to 12
        )

        for ((textView, hour) in wakeUpTimeTexts) {
            textView.setOnClickListener {
//                updateSelectedIntOption(it, "user_wakeUpTime", value, wakeTimeOption)
//                wakeTimeOption = it as TextView
//                resetDebounceTimer { showSleepLayout() }
                wakeTimeOption = textView
                val ampm = wakeAmpmOption?.text?.toString()
                val converted = convertTo24Hour(ampm, hour)
                saveToSPFInt("user_wakeUpTime", converted)
                updateNextButtonState()
                resetDebounceTimer { showSleepLayout() }
            }
        }
        showSleepLayout()
    }

    private fun showSleepLayout() {
        if (wakeAmpmOption != null && wakeTimeOption != null) {
            binding.clSleepingTime.showWithSlideDownAnimation()
        }
    }

    private fun initSleepAmpm() {
        val sleepAmpmTexts = listOf(
            binding.tvSleepAm to "오전",
            binding.tvSleepPm to "오후"
        )
        for ((textView, value) in sleepAmpmTexts) {
            textView.setOnClickListener {
                sleepAmpmOption?.setTextColor(resources.getColor(R.color.unuse_font, null))
                sleepAmpmOption = textView
                sleepAmpmOption?.setTextColor(resources.getColor(R.color.main_blue, null))
                resetDebounceTimer { showTurnOffLayout() }
            }
        }
    }

    private fun initSleepTime() {
        val sleepTimeTexts = listOf(
            binding.tvSleep1 to 1, binding.tvSleep2 to 2, binding.tvSleep3 to 3,
            binding.tvSleep4 to 4, binding.tvSleep5 to 5, binding.tvSleep6 to 6,
            binding.tvSleep7 to 7, binding.tvSleep8 to 8, binding.tvSleep9 to 9,
            binding.tvSleep10 to 10, binding.tvSleep11 to 11, binding.tvSleep12 to 12
        )

        for ((textView, value) in sleepTimeTexts) {
            textView.setOnClickListener {
                sleepTimeOption = textView
                val ampm = sleepAmpmOption?.text?.toString()
                val converted = convertTo24Hour(ampm, value)
                saveToSPFInt("user_sleepingTime", converted)
                updateNextButtonState()
                resetDebounceTimer { showTurnOffLayout() }
            }
        }
    }

    private fun showTurnOffLayout() {
        if (sleepAmpmOption != null && sleepTimeOption != null) {
            binding.clTurnOffTime.showWithSlideDownAnimation()
        }
    }

    private fun initTurnOffAmpm() {
        val lightOffAmpmTexts = listOf(
            binding.tvTurnOffAm to "오전",
            binding.tvTurnOffPm to "오후"
        )
        for ((textView, value) in lightOffAmpmTexts) {
            textView.setOnClickListener {
                lightOffAmpmOption?.setTextColor(resources.getColor(R.color.unuse_font, null))
                lightOffAmpmOption = textView
                lightOffAmpmOption?.setTextColor(resources.getColor(R.color.main_blue, null))
                resetDebounceTimer { showSmokeLayout() }
            }
        }
    }

    private fun initTurnOffTime() {
        val lightOffTimeTexts = listOf(
            binding.tvTurnOff1 to 1, binding.tvTurnOff2 to 2, binding.tvTurnOff3 to 3,
            binding.tvTurnOff4 to 4, binding.tvTurnOff5 to 5, binding.tvTurnOff6 to 6,
            binding.tvTurnOff7 to 7, binding.tvTurnOff8 to 8, binding.tvTurnOff9 to 9,
            binding.tvTurnOff10 to 10, binding.tvTurnOff11 to 11, binding.tvTurnOff12 to 12
        )

        for ((textView, value) in lightOffTimeTexts) {
            textView.setOnClickListener {
                lightOffTimeOption = textView
                val ampm = lightOffAmpmOption?.text?.toString()
                val converted = convertTo24Hour(ampm, value)
                saveToSPFInt("user_turnOffTime", converted)
                updateNextButtonState()
                resetDebounceTimer { showSmokeLayout() }
            }
        }
    }

    private fun showSmokeLayout() {
        if (lightOffAmpmOption != null && lightOffTimeOption != null) {
            binding.clSmokingStatus.showWithSlideDownAnimation()
        }
    }

    private fun initSmoke() {
        val smokeTexts = listOf(
            binding.smokingStatusNo to "비흡연자",
            binding.smokingStatusPaper to "연초",
            binding.smokingStatusEletronic to "궐련형 전자담배",
            binding.smokingStatusWater to "액상형 전자담배"
        )
        for ((textView, value) in smokeTexts) {
            textView.setOnClickListener {
                updateSelectedStringOption(it, "user_smokingStatus", value, smokeOption)
                smokeOption = it as TextView
                resetDebounceTimer { showSleepHabitLayout() }
            }
        }
    }

    private fun showSleepHabitLayout() {
        binding.clSleepingHabits.showWithSlideDownAnimation()
    }

    private fun initSleepHabit() {
        val sleepHabitTexts = listOf(
            binding.sleepingHabitsNo to "잠버릇이 없어요",
            binding.sleepingHabitsNoise to "코골이",
            binding.sleepingHabitsTeeth to "이갈이",
            binding.sleepingHabitsMoveSick to "몽유병",
            binding.sleepingHabitsSpeak to "잠꼬대",
            binding.sleepingHabitsMove to "뒤척임"
        )
        for ((textView, value) in sleepHabitTexts) {
            textView.setOnClickListener {
                toggleMultiSelection(it, "user_sleepingHabits", value, selectedSleepHabits)
                resetDebounceTimer { showAcLayout() }
            }
        }
    }

    private fun showAcLayout() {
        binding.clAc.showWithSlideDownAnimation()
    }

    private fun initAc() {
        val acTexts = listOf(
            binding.acStrong to "세게 틀어요",
            binding.acEnough to "적당하게 틀어요",
            binding.acWeak to "약하게 틀어요",
            binding.acNo to "안 틀어요"
        )
        for ((textView, value) in acTexts) {
            textView.setOnClickListener {
                updateSelectedStringOption(it, "user_coolingIntensity", value, acOption)
                acOption = it as TextView
                resetDebounceTimer { showHeaterLayout() }
            }
        }
    }

    private fun showHeaterLayout() {
        binding.clHeater.showWithSlideDownAnimation()
    }

    private fun initHeater() {
        val heaterTexts = listOf(
            binding.heaterStrong to "세게 틀어요",
            binding.heaterEnough to "적당하게 틀어요",
            binding.heaterWeak to "약하게 틀어요",
            binding.heaterNo to "안 틀어요"
        )
        for ((textView, value) in heaterTexts) {
            textView.setOnClickListener {
                updateSelectedStringOption(it, "user_heatingIntensity", value, heaterOption)
                heaterOption = it as TextView
                resetDebounceTimer { showLivingPatternLayout() }
            }
        }
    }

    private fun showLivingPatternLayout() {
        binding.clLifePattern.showWithSlideDownAnimation()
    }

    private fun initLiving() {
        val livingTexts = listOf(
            binding.lifePatternMorning to "아침형 인간",
            binding.lifePatternDawn to "새벽형 인간"
        )
        for ((textView, value) in livingTexts) {
            textView.setOnClickListener {
                updateSelectedStringOption(it, "user_lifePattern", value, livingPatternOption)
                livingPatternOption = it as TextView
                resetDebounceTimer { showFriendlyLayout() }
            }
        }
    }

    private fun showFriendlyLayout() {
        binding.clIntimacy.showWithSlideDownAnimation()
    }

    private fun initIntimacy() {
        val friendlyTexts = listOf(
            binding.intimacyNo to "필요한 말만 했으면 좋겠어요",
            binding.intimacyEnough to "어느정도 친하게 지내요",
            binding.intimacyYes to "완전 친하게 지내요"
        )
        for ((textView, value) in friendlyTexts) {
            textView.setOnClickListener {
                updateSelectedStringOption(it, "user_intimacy", value, friendlyOption)
                friendlyOption = it as TextView
                resetDebounceTimer { showShareLayout() }
            }
        }
    }

    private fun showShareLayout() {
        binding.clSharingStatus.showWithSlideDownAnimation()
    }

    private fun initShare() {
        val shareTexts = listOf(
            binding.sharingStatusNothing to "아무것도 공유하고 싶지 않아요",
            binding.sharingStatusTissue to "휴지정도는 빌려줄 수 있어요",
            binding.sharingStatusCloths to "옷정도는 빌려줄 수 있어요",
            binding.sharingStatusEverything to "칫솔만 아니면 돼요"
        )
        for ((textView, value) in shareTexts) {
            textView.setOnClickListener {
                updateSelectedStringOption(it, "user_sharingStatus", value, shareOption)
                shareOption = it as TextView
                resetDebounceTimer { showGameLayout() }
            }
        }
    }

    private fun showGameLayout() {
        binding.clGamingStatus.showWithSlideDownAnimation()
    }

    private fun initGame() {
        val gameTexts = listOf(
            binding.gamingStatusNo to "아예 하지 않아요",
            binding.gamingStatusKeyboard to "키보드 채팅정도만 쳐요",
            binding.gamingStatusVoice to "보이스 채팅도 해요"

        )
        for ((textView, value) in gameTexts) {
            textView.setOnClickListener {
                updateSelectedStringOption(it, "user_gamingStatus", value, gameOption)
                gameOption = it as TextView
                resetDebounceTimer { showCallLayout() }
            }
        }
    }

    private fun showCallLayout() {
        binding.clCallingStatus.showWithSlideDownAnimation()
    }

    private fun initCall() {
        val callTexts = listOf(
            binding.callingStatusNo to "아예 하지 않아요",
            binding.callingStatusHurry to "급한 전화만 해요",
            binding.callingStatusFrequently to "자주 해요"
        )
        for ((textView, value) in callTexts) {
            textView.setOnClickListener {
                updateSelectedStringOption(it, "user_callingStatus", value, callOption)
                callOption = it as TextView
                resetDebounceTimer { showStudyLayout() }
            }
        }
    }

    private fun showStudyLayout() {
        binding.clStudyingStatus.showWithSlideDownAnimation()
    }

    private fun initStudy() {
        val studyTexts = listOf(
            binding.studyingStatusNo to "아예 하지 않아요",
            binding.studyingStatusExam to "시험기간 때만 해요",
            binding.studyingStatusEveryday to "매일 해요"
        )
        for ((textView, value) in studyTexts) {
            textView.setOnClickListener {
                updateSelectedStringOption(it, "user_studyingStatus", value, studyOption)
                studyOption = it as TextView
                resetDebounceTimer { showEatingLayout() }
            }
        }
    }

    private fun showEatingLayout() {
        binding.clEatingStatus.showWithSlideDownAnimation()
    }

    private fun initEating() {
        val eatingTexts = listOf(
            binding.eatingStatusNo to "아예 안 먹어요",
            binding.eatingStatusDrink to "음료만 마셔요",
            binding.eatingStatusSnack to "간단한 간식정도만 먹어요",
            binding.eatingStatusDelivery to "배달음식도 먹어요"
        )
        for ((textView, value) in eatingTexts) {
            textView.setOnClickListener {
                updateSelectedStringOption(it, "user_eatingStatus", value, eatingOption)
                eatingOption = it as TextView
                resetDebounceTimer { showCleanLayout() }
            }
        }
    }

    private fun showCleanLayout() {
        binding.clCleannessSensitivityCheck.showWithSlideDownAnimation()
    }

    private fun initClean() {
        val cleanTexts = listOf(
            binding.cleannessSensitivity1 to "매우 예민하지 않아요",
            binding.cleannessSensitivity2 to "예민하지 않아요",
            binding.cleannessSensitivity3 to "보통이에요",
            binding.cleannessSensitivity4 to "예민해요",
            binding.cleannessSensitivity5 to "매우 예민해요",
        )
        for ((textView, value) in cleanTexts) {
            textView.setOnClickListener {
                updateSelectedStringOption(it, "user_cleannessSensitivity", value, cleanOption)
                cleanOption = it as TextView
                resetDebounceTimer { showNoiseLayout() }
            }
        }
    }

    private fun showNoiseLayout() {
        binding.clNoiseSensitivity.showWithSlideDownAnimation()
    }

    private fun initNoise() {
        val noiseTexts = listOf(
            binding.noiseSensitivity1 to "매우 예민하지 않아요",
            binding.noiseSensitivity2 to "예민하지 않아요",
            binding.noiseSensitivity3 to "보통이에요",
            binding.noiseSensitivity4 to "예민해요",
            binding.noiseSensitivity5 to "매우 예민해요"
        )
        for ((textView, value) in noiseTexts) {
            textView.setOnClickListener {
                updateSelectedStringOption(it, "user_noiseSensitivity", value, noiseOption)
                noiseOption = it as TextView
                resetDebounceTimer { showCleanFrequencyLayout() }
            }
        }
    }

    private fun showCleanFrequencyLayout() {
        binding.clCleaningFrequency.showWithSlideDownAnimation()
    }

    private fun initCleanFrequency() {
        val cleanFrequencyTexts = listOf(
            binding.cleaningFrequencyMonth to "한 달에 한 번 해요",
            binding.cleaningFrequencyEvery2weeks to "2주에 한 번 해요",
            binding.cleaningFrequencyEveryWeek to "일주일에 한 번 해요",
            binding.cleaningFrequency2days to "이틀에 한 번 정도 해요",
            binding.cleaningFrequencyEveryday to "매일매일 해요"
        )
        for ((textView, value) in cleanFrequencyTexts) {
            textView.setOnClickListener {
                updateSelectedStringOption(
                    it,
                    "user_cleaningFrequency",
                    value,
                    cleanFrequencyOption
                )
                cleanFrequencyOption = it as TextView
                resetDebounceTimer { showDrinkingFrequencyLayout() }
            }
        }
    }

    private fun showDrinkingFrequencyLayout() {
        binding.clDrinkingFrequency.showWithSlideDownAnimation()
    }

    private fun initDrinkingFrequency() {
        val drinkingFrequencyTexts = listOf(
            binding.drinkingFrequencyNo to "아예 안 마셔요",
            binding.drinkingFrequencyMonth to "한 달에 한 두번 마셔요",
            binding.drinkingFrequencyWeek to "일주일에 한 두번 마셔요",
            binding.drinkingFrequency4Weeks to "일주일에 네 번 이상 마셔요",
            binding.drinkingFrequencyEveryday to "거의 매일 마셔요"
        )
        for ((textView, value) in drinkingFrequencyTexts) {
            textView.setOnClickListener {
                updateSelectedStringOption(
                    it,
                    "user_drinkingFrequency",
                    value,
                    drinkingFrequencyOption
                )
                drinkingFrequencyOption = it as TextView
                resetDebounceTimer { showPersonalityLayout() }
            }
        }
    }

    private fun showPersonalityLayout() {
        binding.clPersonalities.showWithSlideDownAnimation()
    }

    private fun initPersonality() {
        val personalityTexts = listOf(
            binding.personalitiesQuite to "조용해요",
            binding.personalitiesActivity to "활발해요",
            binding.personalitiesTmt to "말이 많아요",
            binding.personalitiesClean to "깔끔해요",
            binding.personalitiesShame to "부끄러움이 많아요",
            binding.personalitiesHome to "집이 좋아요",
            binding.personalitiesGoOut to "바깥이 좋아요",
            binding.personalitiesHurry to "급해요",
            binding.personalitiesRelaxed to "느긋해요",
            binding.personalitiesShy to "낯을 가려요",
            binding.personalitiesLazy to "귀차니즘이 있어요",
            binding.personalitiesDiligent to "부지런해요"
        )
        for ((textView, value) in personalityTexts) {
            textView.setOnClickListener {
                toggleMultiSelection(it, "user_personalities", value, selectedPersonalitys)
                resetDebounceTimer { showMbtiLayout() }
            }
        }
    }

    private fun showMbtiLayout() {
        binding.clMbti.showWithSlideDownAnimation()
    }

    private fun initMbti() {
        val mbtiTexts = listOf(
            binding.mbtiIstj to "ISTJ",
            binding.mbtiIsfj to "ISFJ",
            binding.mbtiInfj to "INFJ",
            binding.mbtiIntj to "INTJ",

            binding.mbtiIstp to "ISTP",
            binding.mbtiISFP to "ISFP",
            binding.mbtiInfp to "INFP",
            binding.mbtiINTP to "INTP",

            binding.mbtiEstp to "ESTP",
            binding.mbtiEsfp to "ESFP",
            binding.mbtiEnfp to "ENFP",
            binding.mbtiEntp to "ENTP",

            binding.mbtiEstj to "ESTJ",
            binding.mbtiEsfj to "ESFJ",
            binding.mbtiEnfj to "ENFJ",
            binding.mbtiEntj to "ENTJ",
        )
        for ((textView, value) in mbtiTexts) {
            textView.setOnClickListener {
                updateSelectedStringOption(it, "user_mbti", value, mbtiOption)
                mbtiOption = it as TextView

                updateNextButtonState()
            }
        }
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

    fun updateNextButtonState() {
        // 각 항목이 입력되었는지 확인
        val isWakeSelected = wakeAmpmOption != null && wakeTimeOption != null
        val isSleepSelected = sleepAmpmOption != null && sleepTimeOption != null
        val isLightOffSelected = lightOffAmpmOption != null && lightOffTimeOption != null
        val isSmokeSelected = smokeOption != null
//        val isSleepHabitSelected = sleepHabitOption != null
        val isSleepHabitSelected = selectedSleepHabits.isNotEmpty()
        val isAcSelected = acOption != null
        val isHeaterSelected = heaterOption != null
        val isLivingPatternSelected = livingPatternOption != null
        val isFriendlySelected = friendlyOption != null
        val isShareSelected = shareOption != null
        val isGameSelected = gameOption != null
        val isCallSelected = callOption != null
        val isStudySelected = studyOption != null
        val isEatingSelected = eatingOption != null
        val isCleanSelected = cleanOption != null
        val isNoiseSelected = noiseOption != null
        val isCleanFrequencySelected = cleanFrequencyOption != null
        val isDrinkingFrequencySelected = drinkingFrequencyOption != null
//        val isPersonalitySelected = personalityOption != null
        val isPersonalitySelected = selectedPersonalitys.isNotEmpty()
        val isMbtiSelected = mbtiOption != null

        // 총 항목 수 및 완료된 항목 수 계산
        val totalSteps = 20
        val completedSteps = listOf(
            isWakeSelected,
            isSleepSelected,
            isLightOffSelected,
            isSmokeSelected,
            isSleepHabitSelected,
            isAcSelected,
            isHeaterSelected,
            isLivingPatternSelected,
            isFriendlySelected,
            isShareSelected,
            isGameSelected,
            isCallSelected,
            isStudySelected,
            isEatingSelected,
            isCleanSelected,
            isNoiseSelected,
            isCleanFrequencySelected,
            isDrinkingFrequencySelected,
            isPersonalitySelected,
            isMbtiSelected
        ).count { it }

        // ProgressBar에 반영할 진행도 계산
        val progress = completedSteps / totalSteps.toFloat()

        // ProgressBar 업데이트
        (activity as? RoommateInputInfoActivity)?.updateProgressBar(progress)

        // 모든 항목이 완료되면 다음 버튼을 활성화
        if (completedSteps == totalSteps) {
            (activity as? RoommateInputInfoActivity)?.showNextButton()
        }
    }

}