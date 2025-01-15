package umc.cozymate.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import umc.cozymate.R
import umc.cozymate.databinding.ActivityLaunchBinding

@SuppressLint("CustomSplashScreen")
class LaunchActivity : AppCompatActivity() {

    private val TAG = this.javaClass.simpleName
    lateinit var binding: ActivityLaunchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLaunchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.navigationBarColor = Color.WHITE
        // 스플래시 gif 2.5초동안 로드
        binding.lottieSplash.setAnimation(R.raw.splash_gif)
        binding.lottieSplash.playAnimation()
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, SplashActivity::class.java))
            finish()
        }, 2500)
    }
}