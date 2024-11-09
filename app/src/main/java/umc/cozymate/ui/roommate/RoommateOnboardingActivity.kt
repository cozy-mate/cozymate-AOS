package umc.cozymate.ui.roommate

import android.app.Activity
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import umc.cozymate.R
import umc.cozymate.databinding.ActivityRoommateOnboardingBinding
import umc.cozymate.ui.viewmodel.SplashViewModel

class RoommateOnboardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRoommateOnboardingBinding
    private val splashViewModel: SplashViewModel by viewModels()

    private var myNickname: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRoommateOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {  // API 34 이상인지 확인
            overrideActivityTransition(
                Activity.OVERRIDE_TRANSITION_OPEN,
                R.anim.horizon_enter,
                R.anim.none
            )
            // 34 버전 이상에서만 사용가능 하기 때문에 버전에 따라 다른 함수 사용
        } else {
            overridePendingTransition(R.anim.horizon_enter, R.anim.none)
            // 기존 함수로, 34이하 버전에서 사용


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
}
