package umc.cozymate.ui.roommate

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.R
import umc.cozymate.data.model.request.UserInfoRequest
import umc.cozymate.databinding.ActivityRoommateInputInfoBinding
import umc.cozymate.ui.onboarding.OnboardingSelectingFragmentActivity
import umc.cozymate.ui.roommate.adapter.RoommateInputInfoVPA
import umc.cozymate.ui.roommate.lifestyle_info.BasicInfoFragment
import umc.cozymate.ui.roommate.lifestyle_info.EssentialInfoFragment
import umc.cozymate.ui.roommate.lifestyle_info.SelectionInfoFragment
import umc.cozymate.ui.viewmodel.RoommateViewModel
import umc.cozymate.util.StatusBarUtil
import umc.cozymate.util.navigationHeight
import umc.cozymate.util.setStatusBarTransparent

@AndroidEntryPoint
class RoommateInputInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRoommateInputInfoBinding
    private lateinit var spf: SharedPreferences

    private lateinit var viewPager: ViewPager2
    private lateinit var progressBar: ProgressBar
    private lateinit var btnNext: Button
    private lateinit var btnBack: ImageView
    private lateinit var tvTitle: TextView

    private val viewModel: RoommateViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtil.updateStatusBarColor(this, R.color.white)
        binding = ActivityRoommateInputInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.setStatusBarTransparent()
        window.navigationBarColor = Color.WHITE
        StatusBarUtil.updateStatusBarColor(this@RoommateInputInfoActivity, Color.WHITE)
        binding.main.setPadding(0, 0, 0, this.navigationHeight())
        spf = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        viewPager = binding.vpInputInfo
        progressBar = binding.pbBasic
        btnNext = binding.btnNext
        btnBack = binding.ivBack
        tvTitle = binding.tvTitle

        viewPager.adapter = RoommateInputInfoVPA(this)

        progressBar.progress = 0  // 초기에는 0으로 설정
        tvTitle.text = "기본정보"

        btnNext.setOnClickListener {
            if (viewPager.currentItem < viewPager.adapter!!.itemCount - 1) {
                viewPager.currentItem += 1
            } else {
//                viewModel.sendUserInfo(
//                    userInfo.toRequest()
//                )
                sendUserDataToViewModel()
                Log.d("RoommateInputInfoActivity", "sendUserInfo")

                this.finish()
            }
        }

        btnBack.setOnClickListener {
            handleBackButton()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackButton()
            }
        })

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                // 첫 번째, 두 번째 프래그먼트에서 ProgressBar 0
                if (position == 0 || position == 1) {
                    resetProgressBar()  // ProgressBar를 0으로 초기화
                }

                // 마지막 프래그먼트에서는 ProgressBar 100
                if (position == viewPager.adapter!!.itemCount - 1) {
                    setProgressBarToFull()  // ProgressBar를 100으로 설정
                }
                updateFragmentTitle(position)
                btnNext.visibility = View.GONE

                val fragment = supportFragmentManager.findFragmentByTag("f$position")
                when (fragment) {
                    is BasicInfoFragment -> fragment.updateNextButtonState()
                    is EssentialInfoFragment -> fragment.updateNextButtonState()
                    is SelectionInfoFragment -> fragment.updateNextButtonState()
                }
            }
        })

        viewPager.run {
            isUserInputEnabled = false
        }
    }

    private fun resetProgressBar() {
        progressBar.progress = 0  // ProgressBar를 0으로 초기화
    }

    private fun setProgressBarToFull() {
        binding.pbBasic.progress = 100  // ProgressBar를 100으로 설정
    }

    private fun handleBackButton() {
        when (viewPager.currentItem) {
            0 -> {
                finish()
            }

            else -> {
                viewPager.currentItem -= 1
            }
        }
    }

    private fun updateFragmentTitle(position: Int) {
        when (position) {
            0 -> tvTitle.text = "기본정보"
            1 -> tvTitle.text = "필수정보"
            2 -> tvTitle.text = "선택정보"
        }
    }

    // 프로그레스바 업데이트 함수 (애니메이션 적용)
    fun updateProgressBar(progress: Float) {
        val progressPercentage = (progress * 100).toInt()

        // ObjectAnimator를 사용하여 ProgressBar의 진행도를 부드럽게 변경
        ObjectAnimator.ofInt(
            binding.pbBasic,
            "progress",
            binding.pbBasic.progress,
            progressPercentage
        ).apply {
            duration = 500  // 애니메이션 지속 시간 (500ms)
            start()
        }
    }

    fun showNextButton() {
        btnNext.visibility = View.VISIBLE
    }

    // 정보 저장 로직인데 수정 필요
    fun sendUserDataToViewModel() {
        val userInfo = UserInfoRequest(
            admissionYear = spf.getString("user_admissionYear", "") ?: "",
            numOfRoommate = spf.getInt("user_numOfRoommate", -1),
            dormitoryName = spf.getString("user_dormitoryName", "") ?: "",
            acceptance = spf.getString("user_acceptance", "") ?: "",
            wakeUpMeridian = spf.getString("user_wakeUpMeridian", "") ?: "",
            wakeUpTime = spf.getInt("user_wakeUpTime", -1),
            sleepingMeridian = spf.getString("user_sleepingMeridian", "") ?: "",
            sleepingTime = spf.getInt("user_sleepingTime", -1),
            turnOffMeridian = spf.getString("user_turnOffMeridian", "") ?: "",
            turnOffTime = spf.getInt("user_turnOffTime", -1),
            smoking = spf.getString("user_smoking", "") ?: "",
            sleepingHabit = spf.getStringSet("user_sleepingHabit", emptySet())?.toList()
                ?: emptyList(),
            airConditioningIntensity = spf.getInt("user_airConditioningIntensity", -1),
            heatingIntensity = spf.getInt("user_heatingIntensity", -1),
            lifePattern = spf.getString("user_lifePattern", "") ?: "",
            intimacy = spf.getString("user_intimacy", "") ?: "",
            canShare = spf.getString("user_canShare", "") ?: "",
            isPlayGame = spf.getString("user_isPlayGame", "") ?: "",
            isPhoneCall = spf.getString("user_isPhoneCall", "") ?: "",
            studying = spf.getString("user_studying", "") ?: "",
            intake = spf.getString("user_intake", "") ?: "",
            cleanSensitivity = spf.getInt("user_cleanSensitivity", -1),
            noiseSensitivity = spf.getInt("user_noiseSensitivity", -1),
            cleaningFrequency = spf.getString("user_cleaningFrequency", "") ?: "",
            drinkingFrequency = spf.getString("user_drinkingFrequency", "") ?: "",
            personality = spf.getStringSet("user_personality", emptySet())?.toList() ?: emptyList(),
            mbti = spf.getString("user_mbti", "") ?: "",
            selfIntroduction = spf.getString("user_selfIntroduction", "") ?: ""
        )
        viewModel.sendUserInfo(userInfo)
        finish() // 결과 전달 후 종료
    }
}
