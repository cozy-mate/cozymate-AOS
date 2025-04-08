package umc.cozymate.ui.roommate

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.data.model.entity.MemberDetailInfo
import umc.cozymate.databinding.ActivityRoommateOnboardingBinding
import umc.cozymate.ui.viewmodel.CozyHomeViewModel
import umc.cozymate.ui.viewmodel.SplashViewModel
import umc.cozymate.util.setStatusBarTransparent

@AndroidEntryPoint
class RoommateOnboardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRoommateOnboardingBinding
    private val cozyHomeViewModel: CozyHomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRoommateOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setStatusBarTransparent()
        setNickname()

        binding.btnGoLifestyle.setOnClickListener {
            Log.d("RoommateOnboardingActivity", "btnGoLifestyle Clicked")
            val intent = Intent(this, RoommateInputInfoActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    private fun setNickname() {
        binding.tvName1.text = cozyHomeViewModel.getNickname().toString()
    }
}
