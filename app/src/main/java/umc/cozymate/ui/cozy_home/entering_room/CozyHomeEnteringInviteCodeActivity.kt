package umc.cozymate.ui.cozy_home.entering_room

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.R
import umc.cozymate.databinding.ActivityCozyHomeEnteringInviteCodeBinding
import umc.cozymate.ui.MainActivity
import umc.cozymate.ui.cozy_home.waiting.CozyHomeEnteringFragment
import umc.cozymate.ui.cozy_home.waiting.CozyHomeWaitingFragment
import umc.cozymate.ui.viewmodel.OnboardingViewModel

// 플로우3 : 초대코드 입력창(1) > 룸메이트 대기창(2) > 코지홈 입장창(3) > 코지홈 활성화창
@AndroidEntryPoint
class CozyHomeEnteringInviteCodeActivity : AppCompatActivity() {

    private val TAG = this.javaClass.simpleName
    private val viewModel: OnboardingViewModel by viewModels()

    lateinit var binding: ActivityCozyHomeEnteringInviteCodeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCozyHomeEnteringInviteCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 첫번째로 [초대코드 입력창]을 로드
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.main, CozyHomeEnteringInviteCodeFragment())
                .commit()
        }
    }

    // 두번째 [룸메이트 대기창] 로드
    fun loadFragment2() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main, CozyHomeWaitingFragment())
            .addToBackStack(null)
            .commit()
    }

    // 세번째 [코지홈 입장창] 로드
    fun loadFragment3() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main, CozyHomeEnteringFragment())
            .addToBackStack(null)
            .commit()
    }

    // 네번째 [코지홈_코지홈 활성화]로 화면 전환
    fun loadFragment4() {
        val intent = Intent(baseContext, MainActivity::class.java)
        intent.putExtra("isActive", "true")
        startActivity(intent)
        this.finish()
    }
}
