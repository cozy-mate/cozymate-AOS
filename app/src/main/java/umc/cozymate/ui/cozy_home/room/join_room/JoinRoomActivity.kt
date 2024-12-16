package umc.cozymate.ui.cozy_home.room.join_room

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.R
import umc.cozymate.databinding.ActivityJoinRoomBinding
import umc.cozymate.ui.MainActivity
import umc.cozymate.ui.viewmodel.OnboardingViewModel

// 플로우3 : 초대코드 입력창(1) > 룸메이트 대기창(2) > 코지홈 입장창(3) > 코지홈 활성화창
@AndroidEntryPoint
class JoinRoomActivity : AppCompatActivity() {

    private val TAG = this.javaClass.simpleName
    private val viewModel: OnboardingViewModel by viewModels()
    lateinit var binding: ActivityJoinRoomBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityJoinRoomBinding.inflate(layoutInflater)
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
                .replace(R.id.main, EnterInviteCodeFragment())
                .commit()
        }
    }

    // [코지홈_참여요청 보냄]으로 화면 전환
    fun loadMainActivity() {
        val intent = Intent(baseContext, MainActivity::class.java)
        intent.putExtra("isActive", "true")
        startActivity(intent)
        this.finish()
    }
}
