package umc.cozymate.ui.cozy_home.making_room

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import umc.cozymate.R
import umc.cozymate.databinding.ActivityCozyHomeGivingInviteCodeBinding
import umc.cozymate.ui.MainActivity
import umc.cozymate.ui.cozy_home.waiting.CozyHomeEnteringFragment
import umc.cozymate.ui.cozy_home.waiting.CozyHomeWaitingFragment
import umc.cozymate.util.navigationHeight
import umc.cozymate.util.setStatusBarTransparent

// 플로우2 : 방정보 입력창(1) > 초대코드 발급창(2) > 룸메이트 대기창(3) > 코지홈 입장창(4) > 코지홈 활성화창
class CozyHomeGivingInviteCodeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCozyHomeGivingInviteCodeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCozyHomeGivingInviteCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //initScreen()

        // 첫번째로 [방정보 입력창]을 로드
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_giving, CozyHomeRoomInfoFragment())
                .commit()
        }

    }

    private fun initScreen() {
        this.setStatusBarTransparent()
        binding.main.setPadding(0, 0, 0, this.navigationHeight())
    }

    // 두번째 [초대코드 발급창] 로드
    fun loadFragment2() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_giving, CozyHomeGivingInviteCodeFragment())
            .addToBackStack(null)
            .commit()
    }

    // 세번째 [룸메이트 대기창] 로드
    fun loadFragment3() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_giving, CozyHomeWaitingFragment())
            .addToBackStack(null)
            .commit()
    }

    // 네번째 [코지홈 입장창] 로드
    fun loadFragment4() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_giving, CozyHomeEnteringFragment())
            .addToBackStack(null)
            .commit()
    }

    // 다섯번째 [코지홈_코지홈 활성화]로 화면 전환
    fun loadFragment5() {
        val intent = Intent(baseContext, MainActivity::class.java)
        intent.putExtra("isActive", "true")
        startActivity(intent)
        this.finish()
    }
}