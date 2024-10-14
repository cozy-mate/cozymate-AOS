package umc.cozymate.ui.roommate

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.viewpager2.widget.ViewPager2
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.databinding.ActivityRoommateInputInfoBinding
import umc.cozymate.ui.AnimationActivity
import umc.cozymate.ui.MainActivity
import umc.cozymate.ui.roommate.adapter.RoommateInputInfoVPA
import umc.cozymate.ui.roommate.lifestyle_info.BasicInfoFragment
import umc.cozymate.ui.roommate.lifestyle_info.EssentialInfoFragment
import umc.cozymate.ui.roommate.lifestyle_info.SelectionInfoFragment
import umc.cozymate.ui.viewmodel.RoommateViewModel

@AndroidEntryPoint
class RoommateInputInfoActivity : AnimationActivity(TransitionMode.HORIZON) {

    private lateinit var binding: ActivityRoommateInputInfoBinding
    private lateinit var spfHelper: UserInfoSPFHelper

    private lateinit var viewPager: ViewPager2
    private lateinit var progressBar: ProgressBar
    private lateinit var btnNext: Button
    private lateinit var btnBack: ImageView
    private lateinit var tvTitle: TextView

    private val viewModel: RoommateViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRoommateInputInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewPager = binding.vpInputInfo
        progressBar = binding.pbBasic
        btnNext = binding.btnNext
        btnBack = binding.ivBack
        tvTitle = binding.tvTitle

        viewPager.adapter = RoommateInputInfoVPA(this)

        progressBar.progress = 0  // 초기에는 0으로 설정
        tvTitle.text = "기본정보"

        spfHelper = UserInfoSPFHelper(this)

        val spf = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val accessToken = spf.getString("access_token", "")!!
        Log.d("RoommateInputInfoActivity", "token: ${accessToken}")

        btnNext.setOnClickListener {
            val fragment = supportFragmentManager.findFragmentByTag("f${viewPager.currentItem}")

            if (viewPager.currentItem < viewPager.adapter!!.itemCount - 1) {
                viewPager.currentItem += 1
            } else {
                navigateToRoommateMakeCrewableFragment()
                val userInfo = spfHelper.loadUserInfo()
                viewModel.sendUserInfo(
                    accessToken,
                    userInfo.toRequest()
                )
                Log.d("RoommateInputInfoActivity", "sendUserInfo")
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
                val intent = Intent(this, MainActivity::class.java).apply {
                    putExtra("navigate_to", "RoommateOnboarding")
                }
                startActivity(intent)
                finish()
            }
            else -> {
                viewPager.currentItem -= 1
            }
        }
    }

    private fun navigateToRoommateMakeCrewableFragment() {
        val mainActivityIntent = Intent(this, MainActivity::class.java).apply {
            putExtra("navigate_to", "RoommateMakeCrewable")
        }
        Log.d("RoommateInputInfoActivity", "navigate_to = RoommateMakeCrewable")
        startActivity(mainActivityIntent)
        finish()
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
        ObjectAnimator.ofInt(binding.pbBasic, "progress", binding.pbBasic.progress, progressPercentage).apply {
            duration = 500  // 애니메이션 지속 시간 (500ms)
            start()
        }
    }

    fun showNextButton() {
        btnNext.visibility = View.VISIBLE
    }

    fun getUserInfoSPFHelper(): UserInfoSPFHelper {
        return UserInfoSPFHelper(this)
    }
}
