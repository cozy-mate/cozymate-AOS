package umc.cozymate.ui.cozy_home.making_room

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.R
import umc.cozymate.databinding.ActivityMakingPrivateRoomBinding
import umc.cozymate.ui.MainActivity
import umc.cozymate.ui.cozy_home.waiting.CozyHomeEnteringFragment
import umc.cozymate.ui.cozy_home.waiting.CozyHomeWaitingFragment
import umc.cozymate.ui.viewmodel.MakingRoomViewModel

// 플로우2 : 방정보 입력창(1) > 초대코드 발급창(2) > 룸메이트 대기창(3) > 코지홈 입장창(4) > 코지홈 활성화창
@AndroidEntryPoint
class MakingPrivateRoomActivity : AppCompatActivity() {

    private val TAG = this.javaClass.simpleName
    private val viewModel: MakingRoomViewModel by viewModels()
    private lateinit var binding: ActivityMakingPrivateRoomBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMakingPrivateRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 첫번째로 [방정보 입력창]을 로드
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_giving, MakingPrivateRoomFragment())
                .commit()
        }

    }

    fun showProgressBar(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
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