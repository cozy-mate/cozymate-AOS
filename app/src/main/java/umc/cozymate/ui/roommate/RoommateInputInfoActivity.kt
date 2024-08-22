package umc.cozymate.ui.roommate

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
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.databinding.ActivityRoommateInputInfoBinding
import umc.cozymate.ui.MainActivity
import umc.cozymate.ui.roommate.adapter.RoommateInputInfoVPA
import umc.cozymate.ui.roommate.lifestyle_info.BasicInfoFragment
import umc.cozymate.ui.roommate.lifestyle_info.EssentialInfoFragment
import umc.cozymate.ui.roommate.lifestyle_info.SelectionInfoFragment
import umc.cozymate.ui.viewmodel.RoommateViewModel
import umc.cozymate.ui.viewmodel.TodoViewModel

@AndroidEntryPoint
class RoommateInputInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRoommateInputInfoBinding
    private lateinit var spfHelper: UserInfoSPFHelper

    private lateinit var viewPager: ViewPager2
    private lateinit var progressBar: ProgressBar
    private lateinit var btnNext: Button
    private lateinit var btnBack: ImageView
    private lateinit var tvTitle: TextView

    //    private lateinit var viewModel: ViewModel
    private val viewModel: RoommateViewModel by viewModels()
    private val todoViewModel: TodoViewModel by viewModels()

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

        progressBar.progress = 33
        tvTitle.text = "기본정보"

        spfHelper = UserInfoSPFHelper(this)

//        val _accessToken = getString(R.string.access_token_1)
        val _accessToken = todoViewModel.getToken()
        val accessToken = _accessToken
        Log.d("RoommateInputInfoActivity", accessToken!!)

        btnNext.setOnClickListener {
            // 현재 페이지의 프래그먼트를 가져옵니다.
            val fragment = supportFragmentManager.findFragmentByTag("f${viewPager.currentItem}")

            // 다음 페이지로 이동
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
                progressBar.progress = (position + 1) * 33
                btnNext.visibility = View.GONE
                updateFragmentTitle(position)

                // Fragment를 정확히 식별하여 saveUserInfo 호출
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
        return UserInfoSPFHelper(this)
    }
}
