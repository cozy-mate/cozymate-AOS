package umc.cozymate.ui.cozy_home.room.making_room

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.R
import umc.cozymate.databinding.ActivityCozyHomeInvitingRoommateBinding
import umc.cozymate.util.navigationHeight
import umc.cozymate.util.setStatusBarTransparent

// 플로우1 : 방정보 입력창(1) > 룸메이트 선택창(2) > 룸메이트 대기창(3) > 코지홈 입장창(4) > 코지홈 활성화창
@AndroidEntryPoint
class CozyHomeInvitingRoommateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCozyHomeInvitingRoommateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCozyHomeInvitingRoommateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // initScreen()

        // 첫번째로 [방정보 입력창]을 로드
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MakingPrivateRoomFragment())
                .commit()
        }

    }

    private fun initScreen() {
        this.setStatusBarTransparent()
        binding.main.setPadding(0, 0, 0, this.navigationHeight())
    }

    // 두번째 [룸메이트 선택창] 로드
    fun loadFragment2() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, CozyHomeSelectingRoommateFragment())
            .addToBackStack(null)
            .commit()
    }

}