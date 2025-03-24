package umc.cozymate.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.R
import umc.cozymate.databinding.ActivityLaunchBinding
import umc.cozymate.ui.MainActivity
import umc.cozymate.ui.viewmodel.SplashViewModel

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class LaunchActivity : AppCompatActivity() {
    private val TAG = this.javaClass.simpleName
    lateinit var binding: ActivityLaunchBinding
    private val splashViewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLaunchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.navigationBarColor = Color.WHITE
        binding.lottieSplash.setAnimation(R.raw.splash_gif)
        binding.lottieSplash.playAnimation()

        // 저장한 토큰이 유효하다면 자동 로그인을 합니다.
        attemptAutoLogin()
    }

    private fun attemptAutoLogin() {
        val tokenInfo = splashViewModel.getToken()
        if (tokenInfo != null) {
            splashViewModel.memberCheck()
            splashViewModel.isMember.observe(this) { isMember ->
                if (isMember == true) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }, 2500) // 2.5초동안 대기
                } else if (isMember == false) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        startActivity(Intent(this, SplashActivity::class.java))
                        finish()
                    }, 2500) // 2.5초동안 대기
                }
            }
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this, SplashActivity::class.java))
                finish()
            }, 2500) // 2.5초동안 대기
        }
    }
}