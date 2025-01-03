package umc.cozymate.ui.cozy_home.room.making_room

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.R
import umc.cozymate.databinding.ActivityMakingPrivateRoomBinding
import umc.cozymate.ui.MainActivity

// 방정보 입력 > 초대코드 발급 > 코지홈 (방장)
@AndroidEntryPoint
class MakingPrivateRoomActivity : AppCompatActivity() {
    private val TAG = this.javaClass.simpleName
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
        // 첫번째로 [방정보 입력 페이지] 로드
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_giving, MakingPrivateRoomFragment())
                .commit()
        }
    }
    // 로딩중일 때 프로그레스바를 띄웁니다
    fun showProgressBar(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }
    // 두번째 [초대코드 발급 페이지] 로드
    // 방 캐릭터 아이디와 초대코드를 넘겨줍니다
    fun loadGivingInviteCodeFragment(roomCharId: Int, code: String) {
        val fragment = GivingInviteCodeFragment.newInstance(roomCharId, code)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_giving, fragment)
            .addToBackStack(null)
            .commit()
    }
    // 세번째 [코지홈_방장]으로 화면 전환
    fun loadMainActivity() {
        val intent = Intent(baseContext, MainActivity::class.java)
        intent.putExtra("isRoomExist", true)
        intent.putExtra("isRoomManager", true)
        startActivity(intent)
        this.finish()
    }
}