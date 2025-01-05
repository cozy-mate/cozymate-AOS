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
//    private lateinit var spfHelper: UserInfoSPFHelper
//    private var userInfo = UserInfo()
    private lateinit var spf: SharedPreferences

    private var wakeAmpmOption: TextView? = null
    private var wakeAmpm: String? = null
    private var wakeTimeOption: TextView? = null
    private var wakeTime: Int? = null

    private var sleepAmpmOption: TextView? = null
    private var sleepAmpm: String? = null
    private var sleepTimeOption: TextView? = null
    private var sleepTime: Int? = null

    private var lightOffAmpmOption: TextView? = null
    private var lightOffAmpm: String? = null
    private var lightOffTimeOption: TextView? = null
    private var lightOffTime: Int? = null

    private var smokeOption: TextView? = null
    private var smokeCheck: String? = null

//    private var sleepHabitOption: TextView? = null

    //    private var sleepHabitCheck: String? = null
//    private var selectedSleepHabits: List<String> = emptyList()
    private var selectedSleepHabits: MutableList<String> = mutableListOf()

    private var acOption: TextView? = null
    private var acCheck: Int? = null

    private var heaterOption: TextView? = null
    private var heaterCheck: Int? = null

    private var livingPatternOption: TextView? = null
    private var livingPatternCheck: String? = null

    private var friendlyOption: TextView? = null
    private var friendlyCheck: String? = null

    private var shareOption: TextView? = null
    private var shareCheck: String? = null

    private var gameOption: TextView? = null
    private var gameCheck: String? = null

    private var callOption: TextView? = null
    private var callCheck: String? = null

    private var studyOption: TextView? = null
    private var studyCheck: String? = null

    private var eatingOption: TextView? = null
    private var eatingCheck: String? = null

    private var cleanOption: TextView? = null
    private var cleanCheck: Int? = null

    private var noiseOption: TextView? = null
    private var noiseCheck: Int? = null

    private var cleanFrequencyOption: TextView? = null
    private var cleanFrequencyCheck: String? = null

    private var drinkingFrequencyOption: TextView? = null
    private var drinkingFrequencyCheck: String? = null

//    private var personalityOption: TextView? = null

    //    private var personalityCheck: String? = null
//    private var selectedPerosonalitys: List<String> = emptyList()
    private var selectedPersonalitys: MutableList<String> = mutableListOf()

    private var mbtiOption: TextView? = null
    private var mbtiCheck: String? = null

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
            background = resources.getDrawable(R.drawable.custom_option_box_background_default, null)
        }

        val selectedTextView = view as TextView
        selectedTextView.apply {
            setTextColor(resources.getColor(R.color.main_blue, null))
            background = resources.getDrawable(R.drawable.custom_option_box_background_selected_6dp, null)
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
            background = resources.getDrawable(R.drawable.custom_option_box_background_default, null)
        }

        val selectedTextView = view as TextView
        selectedTextView.apply {
            setTextColor(resources.getColor(R.color.main_blue, null))
            background = resources.getDrawable(R.drawable.custom_option_box_background_selected_6dp, null)
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
        initLightOffAmpm()
        initLightOffTime()
        initSmoke()
        initSleepHabit()
        initAc()
        initHeater()
        initLiving()
        initFriendly()
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

    private fun initWakeAmpm() {
        val wakeAmpmTexts = listOf(
            binding.tvWakeAm to "오전",
            binding.tvWakePm to "오후"
        )
        for ((textView, value) in wakeAmpmTexts) {
            textView.setOnClickListener {
                updateSelectedStringOption(it, "user_wakeUpMeridian", value, wakeAmpmOption)
                wakeAmpmOption = it as TextView
                showSleepLayout()
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

        for ((textView, value) in wakeUpTimeTexts) {
            textView.setOnClickListener {
                updateSelectedIntOption(it, "user_wakeUpTime", value, wakeTimeOption)
                wakeTimeOption = it as TextView
                showSleepLayout()
            }
        }
        showSleepLayout()
    }

    private fun showSleepLayout() {
        if (wakeAmpm != null && wakeTime != null && wakeAmpmOption != null && wakeTimeOption != null) {
            binding.clSleepTime.showWithSlideDownAnimation()
        }
    }

    private fun initSleepAmpm() {
        val sleepAmpmTexts = listOf(
            binding.tvSleepAm to "오전",
            binding.tvSleepPm to "오후"
        )
        for ((textView, value) in sleepAmpmTexts) {
            textView.setOnClickListener {
                updateSelectedStringOption(it, "user_sleepingMeridian", value, sleepAmpmOption)
                sleepAmpmOption = it as TextView
                showLightOffLayout()
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
                updateSelectedIntOption(it, "user_sleepingTime", value, sleepTimeOption)
                sleepTimeOption = it as TextView
                showLightOffLayout()
            }
        }
    }

    private fun showLightOffLayout() {
        if (sleepAmpmOption != null && sleepTimeOption != null) {
            binding.clLightOff.showWithSlideDownAnimation()
        }
    }

    private fun initLightOffAmpm() {
        val lightOffAmpmTexts = listOf(
            binding.tvLightOffAm to "오전",
            binding.tvLightOffPm to "오후"
        )
        for ((textView, value) in lightOffAmpmTexts) {
            textView.setOnClickListener {
                updateSelectedStringOption(it, "user_turnOffMeridian", value, lightOffAmpmOption)
                lightOffAmpmOption = it as TextView
                showSmokeLayout()
            }
        }
    }

    private fun initLightOffTime() {
        val lightOffTimeTexts = listOf(
            binding.tvLightOff1 to 1, binding.tvLightOff2 to 2, binding.tvLightOff3 to 3,
            binding.tvLightOff4 to 4, binding.tvLightOff5 to 5, binding.tvLightOff6 to 6,
            binding.tvLightOff7 to 7, binding.tvLightOff8 to 8, binding.tvLightOff9 to 9,
            binding.tvLightOff10 to 10, binding.tvLightOff11 to 11, binding.tvLightOff12 to 12
        )

        for ((textView, value) in lightOffTimeTexts) {
            textView.setOnClickListener {
                updateSelectedIntOption(it, "user_turnOffTime", value, lightOffTimeOption)
                lightOffTimeOption = it as TextView
                showSmokeLayout()
            }
        }
    }

    private fun showSmokeLayout() {
        if (lightOffAmpmOption != null && lightOffTimeOption != null) {
            binding.clSmoke.showWithSlideDownAnimation()
        }
    }

    private fun initSmoke() {
        val smokeTexts = listOf(
            binding.smokeNo to "비흡연자",
            binding.smokePaper to "연초",
            binding.smokeEletronic to "궐련형 전자담배",
            binding.smokeWater to "액상형 전자담배"
        )
        for ((textView, value) in smokeTexts) {
            textView.setOnClickListener {
                updateSelectedStringOption(it, "user_smoking", value, smokeOption)
                smokeOption = it as TextView
                showSleepHabitLayout()
            }
        }
    }

    private fun showSleepHabitLayout() {
        binding.clSleepHabit.showWithSlideDownAnimation()
    }

    private fun initSleepHabit() {
        val sleepHabitTexts = listOf(
            binding.sleepHabitNo to "잠버릇이 없어요",
            binding.sleepHabitNoise to "코골이",
            binding.sleepHabitTeeth to "이갈이",
            binding.sleepHabitMoveSick to "몽유병",
            binding.sleepHabitSpeak to "잠꼬대",
            binding.sleepHabitMove to "뒤척임"
        )
        for ((textView, value) in sleepHabitTexts) {
            textView.setOnClickListener {
                toggleMultiSelection(it, "user_sleepingHabit", value, selectedSleepHabits)
                showAcLayout()
            }
        }
    }

    private fun showAcLayout() {
        binding.clAc.showWithSlideDownAnimation()
    }

    private fun initAc() {
        val acTexts = listOf(
            binding.acStrong to 3,
            binding.acEnough to 2,
            binding.acWeak to 1,
            binding.acNo to 0
        )
        for ((textView, value) in acTexts) {
            textView.setOnClickListener {
                updateSelectedIntOption(it, "user_airConditioningIntensity", value, acOption)
                acOption = it as TextView
                showHeaterLayout()
            }
        }
    }

    private fun showHeaterLayout() {
        binding.clHeater.showWithSlideDownAnimation()
    }

    private fun initHeater() {
        val heaterTexts = listOf(
            binding.heaterStrong to 3,
            binding.heaterEnough to 2,
            binding.heaterWeak to 1,
            binding.heaterNo to 0
        )
        for ((textView, value) in heaterTexts) {
            textView.setOnClickListener {
                updateSelectedIntOption(it, "user_heatingIntensity", value, heaterOption)
                heaterOption = it as TextView
                showLivingPatternLayout()
            }
        }
    }

    private fun showLivingPatternLayout() {
        binding.clLiving.showWithSlideDownAnimation()
    }

    private fun initLiving() {
        val livingTexts = listOf(
            binding.livingMorning to "아침형 인간",
            binding.livingDawn to "새벽형 인간"
        )
        for ((textView, value) in livingTexts) {
            textView.setOnClickListener {
                updateSelectedStringOption(it, "user_lifePattern", value, livingPatternOption)
                livingPatternOption = it as TextView
                showFriendlyLayout()
            }
        }
    }

    private fun showFriendlyLayout() {
        binding.clFriendly.showWithSlideDownAnimation()
    }

    private fun initFriendly() {
        val friendlyTexts = listOf(
            binding.friendlyNo to "필요한 말만 했으면 좋겠어요",
            binding.friendlyEnough to "어느정도 친하게 지내요",
            binding.friendlyYes to "완전 친하게 지내요"
        )
        for ((textView, value) in friendlyTexts) {
            textView.setOnClickListener {
                updateSelectedStringOption(it, "user_intimacy", value, friendlyOption)
                friendlyOption = it as TextView
                showShareLayout()
            }
        }
    }

    private fun showShareLayout() {
        binding.clShare.showWithSlideDownAnimation()
    }

    private fun initShare() {
        val shareTexts = listOf(
            binding.shareNothing to "아무것도 공유하고 싶지 않아요",
            binding.shareTissue to "휴지정도는 빌려줄 수 있어요",
            binding.shareCloths to "옷정도는 빌려줄 수 있어요",
            binding.shareEverything to "칫솔만 아니면 돼요"
        )
        for ((textView, value) in shareTexts) {
            textView.setOnClickListener {
                updateSelectedStringOption(it, "user_canShare", value, shareOption)
                shareOption = it as TextView
                showGameLayout()
            }
        }
    }

    private fun showGameLayout() {
        binding.clGame.showWithSlideDownAnimation()
    }

    private fun initGame() {
        val gameTexts = listOf(
            binding.gameNo to "아예 하지 않아요",
            binding.gameKeyboard to "키보드 채팅정도만 쳐요",
            binding.gameVoice to "보이스 채팅도 해요"

        )
        for ((textView, value) in gameTexts) {
            textView.setOnClickListener {
                updateSelectedStringOption(it, "user_isPlayGame", value, gameOption)
                gameOption = it as TextView
                showCallLayout()
            }
        }
    }

    private fun showCallLayout() {
        binding.clCall.showWithSlideDownAnimation()
    }

    private fun initCall() {
        val callTexts = listOf(
            binding.callNo to "아예 하지 않아요",
            binding.callHurry to "급한 전화만 해요",
            binding.callFrequently to "자주 해요"
        )
        for ((textView, value) in callTexts) {
            textView.setOnClickListener {
                updateSelectedStringOption(it, "user_isPhoneCall", value, callOption)
                callOption = it as TextView
                showStudyLayout()
            }
        }
    }

    private fun showStudyLayout() {
        binding.clStudy.showWithSlideDownAnimation()
    }

    private fun initStudy() {
        val studyTexts = listOf(
            binding.studyNo to "아예 하지 않아요",
            binding.studyExam to "시험기간 때만 해요",
            binding.studyEveryday to "매일 해요"
        )
        for ((textView, value) in studyTexts) {
            textView.setOnClickListener {
                updateSelectedStringOption(it, "user_studying", value, studyOption)
                studyOption = it as TextView
                showEatingLayout()
            }
        }
    }

    private fun showEatingLayout() {
        binding.clEating.showWithSlideDownAnimation()
    }

    private fun initEating() {
        val eatingTexts = listOf(
            binding.eatingNo to "아예 안 먹어요",
            binding.eatingDrink to "음료만 마셔요",
            binding.eatingSnack to "간단한 간식정도만 먹어요",
            binding.eatingDelivery to "배달음식도 먹어요"
        )
        for ((textView, value) in eatingTexts) {
            textView.setOnClickListener {
                updateSelectedStringOption(it, "user_intake", value, eatingOption)
                eatingOption = it as TextView
                showCleanLayout()
            }
        }
    }

    private fun showCleanLayout() {
        binding.clCleanCheck.showWithSlideDownAnimation()
    }

    private fun initClean() {
        val cleanTexts = listOf(
            binding.clean1 to 1,
            binding.clean2 to 2,
            binding.clean3 to 3,
            binding.clean4 to 4,
            binding.clean5 to 5,
        )
        for ((textView, value) in cleanTexts) {
            textView.setOnClickListener {
                updateSelectedIntOption(it, "user_cleanSensitivity", value, cleanOption)
                cleanOption = it as TextView
                showNoiseLayout()
            }
        }
    }

    private fun showNoiseLayout() {
        binding.clNoise.showWithSlideDownAnimation()
    }

    private fun initNoise() {
        val noiseTexts = listOf(
            binding.noise1 to 1,
            binding.noise2 to 2,
            binding.noise3 to 3,
            binding.noise4 to 4,
            binding.noise5 to 5
        )
        for ((textView, value) in noiseTexts) {
            textView.setOnClickListener {
                updateSelectedIntOption(it, "user_noiseSensitivity", value, noiseOption)
                noiseOption = it as TextView
                showCleanFrequencyLayout()
            }
        }
    }

    private fun showCleanFrequencyLayout() {
        binding.clCleanFrequency.showWithSlideDownAnimation()
    }

    private fun initCleanFrequency() {
        val cleanFrequencyTexts = listOf(
            binding.cleanFrequencyMonth to "한 달에 한 번 해요",
            binding.cleanFrequencyEvery2weeks to "2주에 한 번 해요",
            binding.cleanFrequencyEveryWeek to "일주일에 한 번 해요",
            binding.cleanFrequency2days to "이틀에 한 번 정도 해요",
            binding.cleanFrequencyEveryday to "매일매일 해요"
        )
        for ((textView, value) in cleanFrequencyTexts) {
            textView.setOnClickListener {
                updateSelectedStringOption(it, "user_cleaningFrequency", value, cleanFrequencyOption)
                cleanFrequencyOption = it as TextView
                showDrinkingFrequencyLayout()
            }
        }
    }

    private fun showDrinkingFrequencyLayout() {
        binding.clDrink.showWithSlideDownAnimation()
    }

    private fun initDrinkingFrequency() {
        val drinkingFrequencyTexts = listOf(
            binding.drinkNo to "아예 안 마시요",
            binding.drinkMonth to "한 달에 한 두번 마셔요",
            binding.drinkWeek to "일주일에 한 두번 마셔요",
            binding.drink4Weeks to "일주일에 네 번 이상 마셔요",
            binding.drinkEveryday to "거의 매일 마셔요"
        )
        for ((textView, value) in drinkingFrequencyTexts) {
            textView.setOnClickListener {
                updateSelectedStringOption(it, "user_drinkingFrequency", value, drinkingFrequencyOption)
                drinkingFrequencyOption = it as TextView
                showPersonalityLayout()
            }
        }
    }

    private fun showPersonalityLayout() {
        binding.clPersonality.showWithSlideDownAnimation()
    }

    private fun initPersonality() {
        val personalityTexts = listOf(
            binding.personalityQuite to "조용해요",
            binding.personalityActivity to "활발해요",
            binding.personalityTmt to "말이 많아요",
            binding.personalityClean to "깔끔해요",
            binding.personalityShame to "부끄러움이 많아요",
            binding.personalityHome to "집이 좋아요",
            binding.personalityGoOut to "바깥이 좋아요",
            binding.personalityHurry to "급해요",
            binding.personalityRelaxed to "느긋해요",
            binding.personalityShy to "낯을 가려요",
            binding.personalityLazy to "귀차니즘이 있어요",
            binding.personalityDiligent to "부지런해요"
        )
        for ((textView, value) in personalityTexts) {
            textView.setOnClickListener {
                toggleMultiSelection(it, "user_personality", value, selectedPersonalitys)
                showMbtiLayout()
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
            }
        }
    }

    fun View.showWithSlideDownAnimation(duration: Long = 1300) {
        this.apply {
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

            // visibility를 VISIBLE로 설정
            visibility = View.VISIBLE

            // 애니메이션 시작
            startAnimation(animationSet)
        }
    }


    //    fun updateNextButtonState() {
//        val isNextButtonEnabled = wakeAmpm != null &&
//                mbtiCheck != null &&
//                mbtiOption != null
//
//        if (isNextButtonEnabled) {
//            (activity as? RoommateInputInfoActivity)?.showNextButton()
//        }
//    }
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