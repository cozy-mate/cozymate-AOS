package umc.cozymate.ui.roommate

import android.os.Bundle
import androidx.activity.viewModels
import umc.cozymate.databinding.ActivityRoommateOnboardingBinding
import umc.cozymate.ui.AnimationActivity
import umc.cozymate.ui.viewmodel.SplashViewModel

class RoommateOnboardingActivity : AnimationActivity(TransitionMode.HORIZON) {
    private lateinit var binding: ActivityRoommateOnboardingBinding
    private val splashViewModel: SplashViewModel by viewModels()

    private var myNickname: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRoommateOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        binding.btnGoLifestyle.setOnClickListener {
//            Log.d("RoommateOnboardingActivity", "btnGoLifestyle Clicked")
//            val intent = Intent(this, RoommateInputInfoActivity::class.java)
//            startActivity(intent)
//        }

        splashViewModel.memberCheck()
        splashViewModel.membmerInfo.observe(this) { info ->
            myNickname = info.nickname
            binding.tvName1.text = myNickname
        }
    }
}
