package umc.cozymate.ui.roommate

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import umc.cozymate.databinding.ActivityRoommateInputInfoBinding
import umc.cozymate.ui.MainActivity
import umc.cozymate.ui.roommate.adapter.RoommateInputInfoVPA
import umc.cozymate.ui.roommate.lifestyle_info.BasicInfoFragment
import umc.cozymate.ui.roommate.lifestyle_info.EssentialInfoFragment
import umc.cozymate.ui.roommate.lifestyle_info.SelectionInfoFragment

class RoommateInputInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRoommateInputInfoBinding
    private lateinit var spfHelper: UserInfoSPFHelper

    private lateinit var viewPager: ViewPager2
    private lateinit var progressBar: ProgressBar
    private lateinit var btnNext: Button
    private lateinit var btnBack: ImageView
    private lateinit var tvTitle: TextView

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

        progressBar.progress = 1/18
        tvTitle.text = "기본정보"

        spfHelper = UserInfoSPFHelper(this)

        btnNext.setOnClickListener {
            if (viewPager.currentItem < viewPager.adapter!!.itemCount - 1) {
                viewPager.currentItem += 1
            }
            else {
                navigateToRoommateMakeCrewableFragment()
            }
        }

        btnBack.setOnClickListener {
            handleBackButton()
        }

        // Use OnBackPressedDispatcher to handle back navigation
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackButton()
            }
        })

//        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
//            override fun onPageSelected(position: Int) {
//                super.onPageSelected(position)
//                progressBar.progress = (position + 1) * 33
//                btnNext.visibility = View.GONE // 초기 상태는 GONE으로 설정
//                updateFragmentTitle(position)
//
//                // Check the current fragment's state to update the next button
//                val fragment = supportFragmentManager.findFragmentByTag("f$position")
//                if (fragment is BasicInfoFragment) {
//                    fragment.saveUserInfo()
//                    fragment.updateNextButtonState()
//                }
//                if (fragment is EssentialInfoFragment) {
//                    fragment.saveUserInfo()
//                    fragment.updateNextButtonState()
//                }
//                if (fragment is SelectionInfoFragment) {
//                    fragment.saveUserInfo()
//                    fragment.updateNextButtonState()
//                }
//            }
//        })
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                progressBar.progress = (position + 1) * 33
                btnNext.visibility = View.GONE // 초기 상태는 GONE으로 설정
                updateFragmentTitle(position)

                // Fragment를 정확히 식별하여 saveUserInfo 호출
                val fragment = supportFragmentManager.findFragmentByTag("f$position")
                when (fragment) {
                    is BasicInfoFragment -> {
                        fragment.saveUserInfo()
                        fragment.updateNextButtonState()
                    }
                    is EssentialInfoFragment -> {
                        fragment.saveUserInfo()
                        fragment.updateNextButtonState()
                    }
                    is SelectionInfoFragment -> {
                        fragment.saveUserInfo()
                        fragment.updateNextButtonState()
                    }
                }
            }
        })


        // Disable user swiping
        viewPager.run {
            isUserInputEnabled = false
        }
    }

    private fun handleBackButton() {
        when (viewPager.currentItem) {
            0 -> {
                // 첫 번째 프래그먼트일 때 MainActivity로 이동
                val intent = Intent(this, MainActivity::class.java).apply {
                    putExtra("navigate_to", "RoommateOnboarding") // 추가된 인텐트로 데이터를 전달
                }
                startActivity(intent)
                finish()
            }
            else -> {
                // 두 번째, 세 번째 프래그먼트일 때 이전 프래그먼트로 이동
                viewPager.currentItem -= 1
            }
        }
    }

    private fun navigateToRoommateMakeCrewableFragment() {
        val mainActivityIntent = Intent(this, MainActivity::class.java).apply {
            putExtra("navigate_to", "RoommateMakeCrewable")
        }
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

    fun showNextButton() {
        btnNext.visibility = View.VISIBLE
    }

    fun getUserInfoSPFHelper(): UserInfoSPFHelper {
        // Return an instance of UserInfoSPFHelper
        return UserInfoSPFHelper(this)
    }
}
