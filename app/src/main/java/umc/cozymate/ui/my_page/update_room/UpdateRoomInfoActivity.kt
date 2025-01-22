package umc.cozymate.ui.my_page.update_room

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.R
import umc.cozymate.databinding.ActivityUpdateRoomInfoBinding
import umc.cozymate.ui.cozy_home.room.my_room.MyRoomComponent
import umc.cozymate.util.StatusBarUtil

@AndroidEntryPoint
class UpdateRoomInfoActivity : AppCompatActivity() {
    lateinit var binding: ActivityUpdateRoomInfoBinding
    var roomState = ""

    companion object {
        const val ROOM_STATE = "room_state"
        const val PUBLIC = "PUBLIC"
        const val PRIVATE = "PRIVATE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateRoomInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        StatusBarUtil.updateStatusBarColor(this, Color.WHITE)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // intent로 방 상태 불러오기
        roomState = intent.getStringExtra(ROOM_STATE) ?: ""
        if (roomState == PUBLIC) loadUpdatePublicRoomFragment()
        else if (roomState == PRIVATE || roomState == "") loadUpdatePrivateRoomFragment()

    }

    // [공개방 수정 페이지] 로드
    fun loadUpdatePublicRoomFragment() {
        val fragment = UpdatePublicRoomFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_update_room_info, fragment)
            .commit()
    }
    // [비공개방 수정 페이지] 로드
    fun loadUpdatePrivateRoomFragment() {
        val fragment = UpdatePrivateRoomFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_update_room_info, fragment)
            .commit()
    }
}