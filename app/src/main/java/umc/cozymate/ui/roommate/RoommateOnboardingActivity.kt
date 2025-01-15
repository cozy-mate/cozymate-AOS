package umc.cozymate.ui.roommate

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.data.model.entity.MemberDetailInfo
import umc.cozymate.databinding.ActivityRoommateOnboardingBinding
import umc.cozymate.ui.viewmodel.SplashViewModel

@AndroidEntryPoint
class RoommateOnboardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRoommateOnboardingBinding
    private val splashViewModel: SplashViewModel by viewModels()

    private var myNickname: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRoommateOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGoLifestyle.setOnClickListener {
            Log.d("RoommateOnboardingActivity", "btnGoLifestyle Clicked")
            val intent = Intent(this, RoommateInputInfoActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        splashViewModel.memberCheck()
        splashViewModel.memberInfo.observe(this) { info: MemberDetailInfo? ->
            myNickname = info!!.nickname
            binding.tvName1.text = myNickname
        }
    }
}
