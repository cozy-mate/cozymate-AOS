package umc.cozymate.ui.roommate.lifestyle_info

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
import umc.cozymate.ui.roommate.UserInfoSPFHelper
import umc.cozymate.ui.roommate.data_class.UserInfo

class EssentialInfoFragment : Fragment() {

    private lateinit var binding: FragmentEssentialInfoBinding
    private lateinit var spfHelper: UserInfoSPFHelper

    private var userInfo = UserInfo()

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

    private var sleepHabitOption: TextView? = null
    private var sleepHabitCheck: String? = null

    private var acOption: TextView? = null
    private var acCheck: String? = null

    private var heaterOption: TextView? = null
    private var heaterCheck: String? = null

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
    private var cleanCheck: String? = null

    private var noiseOption: TextView? = null
    private var noiseCheck: String? = null

    private var cleanFrequencyOption: TextView? = null
    private var cleanFrequencyCheck: String? = null

    private var drinkingFrequencyOption: TextView? = null
    private var drinkingFrequencyCheck: String? = null

    private var personalityOption: TextView? = null
    private var personalityCheck: String? = null

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

        spfHelper = (activity as RoommateInputInfoActivity).getUserInfoSPFHelper()

        userInfo = spfHelper.loadUserInfo()

        savedInstanceState?.let {
            wakeAmpm = it.getString("wakeAmpm")
            wakeTime = it.getInt("wakeTime")
            sleepAmpm = it.getString("sleepAmpm")
            sleepTime = it.getInt("sleepTime")
            lightOffAmpm = it.getString("lightOffAmpm")
            lightOffTime = it.getInt("lightOffTime")

            smokeCheck = it.getString("smokeCheck")
            sleepHabitCheck = it.getString("sleepHabitCheck")
            acCheck = it.getString("acCheck")
            heaterCheck = it.getString("heaterCheck")
            livingPatternCheck = it.getString("livingPatternCheck")
            friendlyCheck = it.getString("friendlyCheck")
            shareCheck = it.getString("shareCheck")
            gameCheck = it.getString("gameCheck")
            callCheck = it.getString("callCheck")
            studyCheck = it.getString("studyCheck")
            eatingCheck = it.getString("eatingCheck")
            cleanCheck = it.getString("cleanCheck")
            noiseCheck = it.getString("noiseCheck")
            cleanFrequencyCheck = it.getString("cleanFrequencyCheck")
            drinkFrequencyCheck = it.getString("drinkFrequencyCheck")
            personalityCheck = it.getString("personalityCheck")
            mbtiCheck = it.getString("mbtiCheck")
        } ?: run {
            wakeAmpm = userInfo.wakeAmPm
            wakeTime = userInfo.wakeUpTime
            sleepAmpm = userInfo.sleepAmPm
            sleepTime = userInfo.sleepTime
            lightOffAmpm = userInfo.lightOffAmPm
            lightOffTime = userInfo.lightOffTime
            smokeCheck = userInfo.smokingState
            sleepHabitCheck = userInfo.sleepingHabit
            acCheck = userInfo.airConditioningIntensity
            heaterCheck = userInfo.heatingIntensity
            livingPatternCheck = userInfo.lifePattern
            friendlyCheck = userInfo.intimacy
            shareCheck = userInfo.canShare
            gameCheck = userInfo.isPlayGame
            callCheck = userInfo.isPhoneCall
            studyCheck = userInfo.studying
            eatingCheck = userInfo.intake
            cleanCheck = userInfo.cleanSensitivity
            noiseCheck = userInfo.noiseSensitivity
            cleanFrequencyCheck = userInfo.cleaningFrequency
            personalityCheck = userInfo.personality
            mbtiCheck = userInfo.mbti
        }

        spfHelper = (activity as RoommateInputInfoActivity).getUserInfoSPFHelper()

        initPage()

        updateNextButtonState()

        return binding.root
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
        initPersonality()
        initMbti()
    }

    private fun initWakeAmpm() {
        val wakeAmpmTexts = listOf(
            binding.tvWakeAm to "오전",
            binding.tvWakePm to "오후"
        )
        for ((textView, value) in wakeAmpmTexts) {
            textView.setOnClickListener { wakeAmpmSelected(it, value) }
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
            textView.setOnClickListener { wakeUpTimeSelected(it, value) }
        }
    }

    private fun wakeAmpmSelected(view: View, value: String) {
        wakeAmpmOption?.apply {
            setTextColor(resources.getColor(R.color.unuse_font, null))
        }
        wakeAmpmOption = view as TextView
        wakeAmpmOption?.apply {
            setTextColor(resources.getColor(R.color.main_blue, null))
        }
        wakeAmpm = value
        userInfo = userInfo.copy(wakeAmPm = value)
        spfHelper.saveUserInfo(userInfo)
        resetDebounceTimer { showSleepLayout() }
        updateNextButtonState()
    }

    private fun wakeUpTimeSelected(view: View, value: Int) {
        wakeTimeOption?.apply {
            setTextColor(resources.getColor(R.color.unuse_font, null))
            background =
                resources.getDrawable(R.drawable.custom_option_box_background_default, null)
        }
        wakeTimeOption = view as TextView
        wakeTimeOption?.apply {
            setTextColor(resources.getColor(R.color.main_blue, null))
            background =
                resources.getDrawable(R.drawable.custom_option_box_background_selected_6dp, null)
        }
        wakeTime = value
        userInfo = userInfo.copy(wakeUpTime = value)
        spfHelper.saveUserInfo(userInfo)
        resetDebounceTimer { showSleepLayout() }
        updateNextButtonState()
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
            textView.setOnClickListener { sleepAmpmSelected(it, value) }
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
            textView.setOnClickListener { sleepTimeSelected(it, value) }
        }
    }

    private fun sleepAmpmSelected(view: View, value: String) {
        sleepAmpmOption?.apply {
            setTextColor(resources.getColor(R.color.unuse_font, null))
        }
        sleepAmpmOption = view as TextView
        sleepAmpmOption?.apply {
            setTextColor(resources.getColor(R.color.main_blue, null))
        }
        sleepAmpm = value
        userInfo = userInfo.copy(sleepAmPm = value)
        spfHelper.saveUserInfo(userInfo)
        resetDebounceTimer { showLightOffLayout() }
        updateNextButtonState()
    }

    private fun sleepTimeSelected(view: View, value: Int) {
        sleepTimeOption?.apply {
            setTextColor(resources.getColor(R.color.unuse_font, null))
            background =
                resources.getDrawable(R.drawable.custom_option_box_background_default, null)
        }
        sleepTimeOption = view as TextView
        sleepTimeOption?.apply {
            setTextColor(resources.getColor(R.color.main_blue, null))
            background =
                resources.getDrawable(R.drawable.custom_option_box_background_selected_6dp, null)
        }
        sleepTime = value
        userInfo = userInfo.copy(sleepTime = value)
        spfHelper.saveUserInfo(userInfo)
        resetDebounceTimer { showLightOffLayout() }
        updateNextButtonState()
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
            textView.setOnClickListener { lightOffAmpmSelected(it, value) }
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
            textView.setOnClickListener { lightOffTimeSelected(it, value) }
        }
    }

    private fun lightOffAmpmSelected(view: View, value: String) {
        lightOffAmpmOption?.apply {
            setTextColor(resources.getColor(R.color.unuse_font, null))
        }
        lightOffAmpmOption = view as TextView
        lightOffAmpmOption?.apply {
            setTextColor(resources.getColor(R.color.main_blue, null))
        }
        lightOffAmpm = value
        userInfo = userInfo.copy(lightOffAmPm = value)
        spfHelper.saveUserInfo(userInfo)
        resetDebounceTimer { showSmokeLayout() }
        updateNextButtonState()
    }

    private fun lightOffTimeSelected(view: View, value: Int) {
        lightOffTimeOption?.apply {
            setTextColor(resources.getColor(R.color.unuse_font, null))
            background =
                resources.getDrawable(R.drawable.custom_option_box_background_default, null)
        }
        lightOffTimeOption = view as TextView
        lightOffTimeOption?.apply {
            setTextColor(resources.getColor(R.color.main_blue, null))
            background =
                resources.getDrawable(R.drawable.custom_option_box_background_selected_6dp, null)
        }
        lightOffTime = value
        userInfo = userInfo.copy(lightOffTime = value)
        spfHelper.saveUserInfo(userInfo)
        resetDebounceTimer { showSmokeLayout() }
        updateNextButtonState()
    }

    private fun showSmokeLayout() {
        if (lightOffAmpmOption != null && lightOffTimeOption != null) {
            binding.clSmoke.showWithSlideDownAnimation()
        }
    }

    private fun initSmoke() {
        val smokeTexts = listOf(
            binding.smokeNo to "X",
            binding.smokePaper to "연초",
            binding.smokeEletronic to "전자담배",
            binding.smokeWater to "끊는 중이에요"
        )
        for ((textView, value) in smokeTexts) {
            textView.setOnClickListener { smokeSelected(it, value) }
        }
    }

    private fun smokeSelected(view: View, value: String) {
        smokeOption?.apply {
            setTextColor(resources.getColor(R.color.unuse_font, null))
            background =
                resources.getDrawable(R.drawable.custom_option_box_background_default, null)
        }
        smokeOption = view as TextView
        smokeOption?.apply {
            setTextColor(resources.getColor(R.color.main_blue, null))
            background =
                resources.getDrawable(R.drawable.custom_option_box_background_selected_6dp, null)
        }
        smokeCheck = value
        userInfo = userInfo.copy(smokingState = value)
        spfHelper.saveUserInfo(userInfo)
        resetDebounceTimer { showSleepHabitLayout() }
        updateNextButtonState()
    }

    private fun showSleepHabitLayout() {
        binding.clSleepHabit.showWithSlideDownAnimation()
    }

    private fun initSleepHabit() {
        val smokeTexts = listOf(
            binding.sleepHabitNo to "X",
            binding.sleepHabitMove to "잠꼬대",
            binding.sleepHabitNoise to "코골이",
            binding.sleepHabitTeeth to "이갈이",
            binding.sleepHabitMoveSick to "몽유병"
        )
        for ((textView, value) in smokeTexts) {
            textView.setOnClickListener { sleepHabitSelected(it, value) }
        }
    }

    private fun sleepHabitSelected(view: View, value: String) {
        sleepHabitOption?.apply {
            setTextColor(resources.getColor(R.color.unuse_font, null))
            background =
                resources.getDrawable(R.drawable.custom_option_box_background_default, null)
        }
        sleepHabitOption = view as TextView
        sleepHabitOption?.apply {
            setTextColor(resources.getColor(R.color.main_blue, null))
            background =
                resources.getDrawable(R.drawable.custom_option_box_background_selected_6dp, null)
        }
        sleepHabitCheck = value
        userInfo = userInfo.copy(sleepingHabit = value)
        spfHelper.saveUserInfo(userInfo)
        resetDebounceTimer { showAcLayout() }
        updateNextButtonState()
    }

    private fun showAcLayout() {
        binding.clAc.showWithSlideDownAnimation()
    }

    private fun initAc() {
        val acTexts = listOf(
            binding.acStrong to "세게 틀어요",
            binding.acEnough to "적당하게 틀어요",
            binding.acWeak to "약하게 틀어요",
        )
        for ((textView, value) in acTexts) {
            textView.setOnClickListener { acSelected(it, value) }
        }
    }

    private fun acSelected(view: View, value: String) {
        acOption?.apply {
            setTextColor(resources.getColor(R.color.unuse_font, null))
            background =
                resources.getDrawable(R.drawable.custom_option_box_background_default, null)
        }
        acOption = view as TextView
        acOption?.apply {
            setTextColor(resources.getColor(R.color.main_blue, null))
            background =
                resources.getDrawable(R.drawable.custom_option_box_background_selected_6dp, null)
        }
        acCheck = value
        userInfo = userInfo.copy(airConditioningIntensity = value)
        spfHelper.saveUserInfo(userInfo)
        resetDebounceTimer { showHeaterLayout() }
        updateNextButtonState()
    }

    private fun showHeaterLayout() {
        binding.clHeater.showWithSlideDownAnimation()
    }

    private fun initHeater() {
        val heaterTexts = listOf(
            binding.heaterStrong to "세게 틀어요",
            binding.heaterEnough to "적당하게 틀어요",
            binding.heaterWeak to "약하게 틀어요",
        )
        for ((textView, value) in heaterTexts) {
            textView.setOnClickListener { heaterSelected(it, value) }
        }
    }

    private fun heaterSelected(view: View, value: String) {
        heaterOption?.apply {
            setTextColor(resources.getColor(R.color.unuse_font, null))
            background =
                resources.getDrawable(R.drawable.custom_option_box_background_default, null)
        }
        heaterOption = view as TextView
        heaterOption?.apply {
            setTextColor(resources.getColor(R.color.main_blue, null))
            background =
                resources.getDrawable(R.drawable.custom_option_box_background_selected_6dp, null)
        }
        heaterCheck = value
        userInfo = userInfo.copy(heatingIntensity = value)
        spfHelper.saveUserInfo(userInfo)
        resetDebounceTimer { showLivingPatternLayout() }
        updateNextButtonState()
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
            textView.setOnClickListener { livingSelected(it, value) }
        }
    }

    private fun livingSelected(view: View, value: String) {
        livingPatternOption?.apply {
            setTextColor(resources.getColor(R.color.unuse_font, null))
            background =
                resources.getDrawable(R.drawable.custom_option_box_background_default, null)
        }
        livingPatternOption = view as TextView
        livingPatternOption?.apply {
            setTextColor(resources.getColor(R.color.main_blue, null))
            background =
                resources.getDrawable(R.drawable.custom_option_box_background_selected_6dp, null)
        }
        livingPatternCheck = value
        userInfo = userInfo.copy(lifePattern = value)
        spfHelper.saveUserInfo(userInfo)
        resetDebounceTimer { showFriendlyLayout() }
        updateNextButtonState()
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
            textView.setOnClickListener { friendlySelected(it, value) }
        }
    }

    private fun friendlySelected(view: View, value: String) {
        friendlyOption?.apply {
            setTextColor(resources.getColor(R.color.unuse_font, null))
            background =
                resources.getDrawable(R.drawable.custom_option_box_background_default, null)
        }
        friendlyOption = view as TextView
        friendlyOption?.apply {
            setTextColor(resources.getColor(R.color.main_blue, null))
            background =
                resources.getDrawable(R.drawable.custom_option_box_background_selected_6dp, null)
        }
        friendlyCheck = value
        userInfo = userInfo.copy(intimacy = value)
        spfHelper.saveUserInfo(userInfo)
        resetDebounceTimer { showShareLayout() }
        updateNextButtonState()
    }

    private fun showShareLayout() {
        binding.clShare.showWithSlideDownAnimation()
    }

    private fun initShare() {
        val shareTexts = listOf(
            binding.shareNothing to "O",
            binding.shareCloths to "X"
        )
        for ((textView, value) in shareTexts) {
            textView.setOnClickListener { shareSelected(it, value) }
        }
    }

    private fun shareSelected(view: View, value: String) {
        shareOption?.apply {
            setTextColor(resources.getColor(R.color.unuse_font, null))
            background =
                resources.getDrawable(R.drawable.custom_option_box_background_default, null)
        }
        shareOption = view as TextView
        shareOption?.apply {
            setTextColor(resources.getColor(R.color.main_blue, null))
            background =
                resources.getDrawable(R.drawable.custom_option_box_background_selected_6dp, null)
        }
        shareCheck = value
        userInfo = userInfo.copy(canShare = value)
        spfHelper.saveUserInfo(userInfo)
        resetDebounceTimer { showGameLayout() }
        updateNextButtonState()
    }

    private fun showGameLayout() {
        binding.clGame.showWithSlideDownAnimation()
    }

    private fun initGame() {
        val gameTexts = listOf(
            binding.gameNo to "X"
        )
        for ((textView, value) in gameTexts) {
            textView.setOnClickListener { gameSelected(it, value) }
        }
    }

    private fun gameSelected(view: View, value: String) {
        gameOption?.apply {
            setTextColor(resources.getColor(R.color.unuse_font, null))
            background =
                resources.getDrawable(R.drawable.custom_option_box_background_default, null)
        }
        gameOption = view as TextView
        gameOption?.apply {
            setTextColor(resources.getColor(R.color.main_blue, null))
            background =
                resources.getDrawable(R.drawable.custom_option_box_background_selected_6dp, null)
        }
        gameCheck = value
        userInfo = userInfo.copy(isPlayGame = value)
        spfHelper.saveUserInfo(userInfo)
        resetDebounceTimer { showCallLayout() }
        updateNextButtonState()
    }

    private fun showCallLayout() {
        binding.clCall.showWithSlideDownAnimation()
    }

    private fun initCall() {
        val callTexts = listOf(
            binding.callNo to "X"
        )
        for ((textView, value) in callTexts) {
            textView.setOnClickListener { callSelected(it, value) }
        }
    }

    private fun callSelected(view: View, value: String) {
        callOption?.apply {
            setTextColor(resources.getColor(R.color.unuse_font, null))
            background =
                resources.getDrawable(R.drawable.custom_option_box_background_default, null)
        }
        callOption = view as TextView
        callOption?.apply {
            setTextColor(resources.getColor(R.color.main_blue, null))
            background =
                resources.getDrawable(R.drawable.custom_option_box_background_selected_6dp, null)
        }
        callCheck = value
        userInfo = userInfo.copy(isPhoneCall = value)
        spfHelper.saveUserInfo(userInfo)
        resetDebounceTimer { showStudyLayout() }
        updateNextButtonState()
    }

    private fun showStudyLayout() {
        binding.clStudy.showWithSlideDownAnimation()
    }

    private fun initStudy() {
        val studyTexts = listOf(
            binding.studyYes to "O",
            binding.studyNo to "X",
            binding.studySomeTimes to "때마다 다를 거 같아요"
        )
        for ((textView, value) in studyTexts) {
            textView.setOnClickListener { studySelected(it, value) }
        }
    }

    private fun studySelected(view: View, value: String) {
        studyOption?.apply {
            setTextColor(resources.getColor(R.color.unuse_font, null))
            background =
                resources.getDrawable(R.drawable.custom_option_box_background_default, null)
        }
        studyOption = view as TextView
        studyOption?.apply {
            setTextColor(resources.getColor(R.color.main_blue, null))
            background =
                resources.getDrawable(R.drawable.custom_option_box_background_selected_6dp, null)
        }
        studyCheck = value
        userInfo = userInfo.copy(studying = value)
        spfHelper.saveUserInfo(userInfo)
        resetDebounceTimer { showEatingLayout() }
        updateNextButtonState()
    }

    private fun showEatingLayout() {
        binding.clEating.showWithSlideDownAnimation()
    }

    private fun initEating() {
        val eatingTexts = listOf(
            binding.eatingNo to "아예 안 먹었으면 좋겠어요",
            binding.eatingDrink to "음료만 가능해요",
            binding.eatingSnack to "간단한 간식은 괜찮아요",
            binding.eatingFree to "방에서 마음껏 먹어도 돼요"
        )
        for ((textView, value) in eatingTexts) {
            textView.setOnClickListener { eatingSelected(it, value) }
        }
    }

    private fun eatingSelected(view: View, value: String) {
        eatingOption?.apply {
            setTextColor(resources.getColor(R.color.unuse_font, null))
            background =
                resources.getDrawable(R.drawable.custom_option_box_background_default, null)
        }
        eatingOption = view as TextView
        eatingOption?.apply {
            setTextColor(resources.getColor(R.color.main_blue, null))
            background =
                resources.getDrawable(R.drawable.custom_option_box_background_selected_6dp, null)
        }
        eatingCheck = value
        userInfo = userInfo.copy(intake = value)
        spfHelper.saveUserInfo(userInfo)
        resetDebounceTimer { showCleanLayout() }
        updateNextButtonState()
    }

    private fun showCleanLayout() {
        binding.clCleanCheck.showWithSlideDownAnimation()
    }

    private fun initClean() {
        val cleanTexts = listOf(
            binding.clean5 to "매우 예민해요",
            binding.clean4 to "예민해요",
            binding.clean3 to "보통이에요",
            binding.clean2 to "예민하지 않아요",
            binding.clean1 to "매우 예민하지 않아요"
        )
        for ((textView, value) in cleanTexts) {
            textView.setOnClickListener { cleanSelected(it, value) }
        }
    }

    private fun cleanSelected(view: View, value: String) {
        cleanOption?.apply {
            setTextColor(resources.getColor(R.color.unuse_font, null))
            background =
                resources.getDrawable(R.drawable.custom_option_box_background_default, null)
        }
        cleanOption = view as TextView
        cleanOption?.apply {
            setTextColor(resources.getColor(R.color.main_blue, null))
            background =
                resources.getDrawable(R.drawable.custom_option_box_background_selected_6dp, null)
        }
        cleanCheck = value
        userInfo = userInfo.copy(cleanSensitivity = value)
        spfHelper.saveUserInfo(userInfo)
        resetDebounceTimer { showNoiseLayout() }
        updateNextButtonState()
    }

    private fun showNoiseLayout() {
        binding.clNoise.showWithSlideDownAnimation()
    }

    private fun initNoise() {
        val noiseTexts = listOf(
            binding.noise1 to "매우 예민하지 않아요",
            binding.noise2 to "예민하지 않아요",
            binding.noise3 to "보통이에요",
            binding.noise4 to "예민해요",
            binding.noise5 to "매우 예민해요"
        )
        for ((textView, value) in noiseTexts) {
            textView.setOnClickListener { noiseSelected(it, value) }
        }
    }

    private fun noiseSelected(view: View, value: String) {
        noiseOption?.apply {
            setTextColor(resources.getColor(R.color.unuse_font, null))
            background =
                resources.getDrawable(R.drawable.custom_option_box_background_default, null)
        }
        noiseOption = view as TextView
        noiseOption?.apply {
            setTextColor(resources.getColor(R.color.main_blue, null))
            background =
                resources.getDrawable(R.drawable.custom_option_box_background_selected_6dp, null)
        }
        noiseCheck = value
        userInfo = userInfo.copy(noiseSensitivity = value)
        spfHelper.saveUserInfo(userInfo)
        resetDebounceTimer { showCleanFrequencyLayout() }
        updateNextButtonState()
    }

    private fun showCleanFrequencyLayout() {
        binding.clCleanFrequency.showWithSlideDownAnimation()
    }

    private fun initCleanFrequency() {
        val cleanFrequencyTexts = listOf(
            binding.cleanFrequencyEveryDay to "매일매일 해요",
            binding.cleanFrequencyEvery2days to "이틀에 한 번 정도 해요",
            binding.cleanFrequencyEveryWeeks to "일주일에 한 번 정도 해요",
            binding.cleanFrequencyEvery34days to "일주일에 3~4번 하는 거 같아요",
            binding.cleanFrequencyEvery2weeks to "2주에 한 번씩 해요",
            binding.cleanFrequencyEveryMonth to "한 달에 한 번씩 해요",
            binding.cleanFrequencyNo to "거의 안 해요"
        )
        for ((textView, value) in cleanFrequencyTexts) {
            textView.setOnClickListener { cleanFrequencySelected(it, value) }
        }
    }

    private fun cleanFrequencySelected(view: View, value: String) {
        cleanFrequencyOption?.apply {
            setTextColor(resources.getColor(R.color.unuse_font, null))
            background =
                resources.getDrawable(R.drawable.custom_option_box_background_default, null)
        }
        cleanFrequencyOption = view as TextView
        cleanFrequencyOption?.apply {
            setTextColor(resources.getColor(R.color.main_blue, null))
            background =
                resources.getDrawable(R.drawable.custom_option_box_background_selected_6dp, null)
        }
        cleanFrequencyCheck = value
        userInfo = userInfo.copy(cleaningFrequency = value)
        spfHelper.saveUserInfo(userInfo)
        resetDebounceTimer { showDrinkingFrequencyLayout() }
        updateNextButtonState()
    }

    private fun showDrinkingFrequencyLayout() {
        binding.clDrink.showWithSlideDownAnimation()
    }

    private fun initDrinkingFrequency() {
        val drinkingFrequencyTexts = listOf(
            binding.drinkNo to "아예 안 마시요",
            binding.drinkMonth to "",
            binding.drinkWeek to "",
            binding.drink4Weeks to "",
            binding.drinkEveryday to ""
        )
        for ((textView, value) in drinkingFrequencyTexts) {
            textView.setOnClickListener { drinkSelected(it, value) }
        }
    }

    private fun drinkingFrequencySelected(view: View, value: String) {
        drinkingFrequencyOption?.apply {
            setTextColor(resources.getColor(R.color.unuse_font, null))
            background = resources.getDrawable(R.drawable.custom_option_box_background_default, null)
        }
        drinkingFrequencyOption = view as TextView
        drinkingFrequencyOption?.apply {
            setTextColor(resources.getColor(R.color.main_blue, null))
            background = resources.getDrawable(R.drawable.custom_option_box_background_selected_6dp, null)
        }
        drinkingFrequencyCheck = value
        userInfo = userInfo.copy(drinkingFrequency = value)
        resetDebounceTimer { showPersonalityLayout() }
        updateNextButtonState()
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
            binding.personalityGoOut to "바깥이 좋아요"
        )
        for ((textView, value) in personalityTexts) {
            textView.setOnClickListener { personalitySelected(it, value) }
        }
    }

    private fun personalitySelected(view: View, value: String) {
        personalityOption?.apply {
            setTextColor(resources.getColor(R.color.unuse_font, null))
            background =
                resources.getDrawable(R.drawable.custom_option_box_background_default, null)
        }
        personalityOption = view as TextView
        personalityOption?.apply {
            setTextColor(resources.getColor(R.color.main_blue, null))
            background =
                resources.getDrawable(R.drawable.custom_option_box_background_selected_6dp, null)
        }
        personalityCheck = value
        userInfo = userInfo.copy(personality = value)
        spfHelper.saveUserInfo(userInfo)
        resetDebounceTimer { showMbtiLayout() }
        updateNextButtonState()
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
            textView.setOnClickListener { mbtiSelected(it, value) }
        }
    }

    private fun mbtiSelected(view: View, value: String) {
        mbtiOption?.apply {
            setTextColor(resources.getColor(R.color.unuse_font, null))
            background =
                resources.getDrawable(R.drawable.custom_option_box_background_default, null)
        }
        mbtiOption = view as TextView
        mbtiOption?.apply {
            setTextColor(resources.getColor(R.color.main_blue, null))
            background =
                resources.getDrawable(R.drawable.custom_option_box_background_selected_6dp, null)
        }
        mbtiCheck = value
        userInfo = userInfo.copy(mbti = value)
        spfHelper.saveUserInfo(userInfo)
        updateNextButtonState()
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("wakeAmpm", wakeAmpm)
        outState.putInt("wakeTime", wakeTime ?: -1)
        outState.putString("sleepAmPm", sleepAmpm)
        outState.putInt("sleepTime", sleepTime ?: -1)
        outState.putString("lightOffAmPm", lightOffAmpm)
        outState.putInt("lightOffTime", lightOffTime ?: -1)

        outState.putString("smokeCheck", smokeCheck)
        outState.putString("sleepHabitCheck", sleepHabitCheck)
        outState.putString("acCheck", acCheck)
        outState.putString("heaterCheck", heaterCheck)
        outState.putString("livingPatternCheck", livingPatternCheck)
        outState.putString("friendlyCheck", friendlyCheck)
        outState.putString("shareCheck", shareCheck)
        outState.putString("gameCheck", gameCheck)
        outState.putString("callCheck", callCheck)
        outState.putString("studyCheck", studyCheck)
        outState.putString("eatingCheck", eatingCheck)
        outState.putString("cleanCheck", cleanCheck)
        outState.putString("noiseCheck", noiseCheck)
        outState.putString("cleanFrequencyCheck", cleanFrequencyCheck)
        outState.putString("drinkFrequencyCheck", drinkingFrequencyCheck)
        outState.putString("personalityCheck", personalityCheck)
        outState.putString("mbtiCheck", mbtiCheck)
    }

    fun saveUserInfo() {
        spfHelper.saveUserInfo(userInfo)
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
        val isSleepHabitSelected = sleepHabitOption != null
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
        val isPersonalitySelected = personalityOption != null
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