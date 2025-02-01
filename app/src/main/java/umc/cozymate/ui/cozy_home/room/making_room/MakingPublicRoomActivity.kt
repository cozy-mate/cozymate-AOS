package umc.cozymate.ui.cozy_home.room.making_room

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
import umc.cozymate.databinding.ActivityMakingPublicRoomBinding
import umc.cozymate.ui.MainActivity
import umc.cozymate.ui.cozy_home.room.room_detail.CozyRoomDetailInfoActivity
import umc.cozymate.ui.viewmodel.MakingRoomViewModel

// 방정보 입력 > 방상세 (자기방) > 코지홈 (방장)
@AndroidEntryPoint
class MakingPublicRoomActivity : AppCompatActivity() {

    private val TAG = this.javaClass.simpleName
    private val viewModel: MakingRoomViewModel by viewModels()
    private lateinit var binding: ActivityMakingPublicRoomBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMakingPublicRoomBinding.inflate(layoutInflater)
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
                .replace(R.id.fragment_container_making, MakingPublicRoomFragment())
                .commit()
        }
    }

    // 두번째 [자기 방 상세 페이지] 로드
    fun loadMyRoomDetailActivity(roomId: Int) {
        val intent = Intent(baseContext, CozyRoomDetailInfoActivity::class.java)
        intent.putExtra(CozyRoomDetailInfoActivity.ARG_ROOM_ID, roomId)
        intent.putExtra("isMyRoom", true)
        startActivity(intent)
        this.finish()
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