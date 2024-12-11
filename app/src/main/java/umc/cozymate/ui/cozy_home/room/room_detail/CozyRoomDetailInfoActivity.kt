package umc.cozymate.ui.cozy_home.room_detail

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import umc.cozymate.R
import umc.cozymate.databinding.ActivityCozyRoomDetailInfoBinding
import umc.cozymate.ui.viewmodel.MakingRoomViewModel

// 방 생성 후, 내방 컴포넌트 클릭 후 화면 전환할 때 room_id를 받아오도록 구현해놨습니다. 이해 안되는거 있음 얘기해주세요
class CozyRoomDetailInfoActivity : AppCompatActivity() {
    private val TAG = this.javaClass.simpleName
    private lateinit var binding: ActivityCozyRoomDetailInfoBinding
    private val viewModel: MakingRoomViewModel by viewModels()
    private var roomId: Int? = 0
    // 방 id는  Intent를 통해 불러옵니다
    companion object {
        const val ARG_ROOM_ID = "room_id"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCozyRoomDetailInfoBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // 방 id 불러오기
        roomId = intent.getIntExtra(ARG_ROOM_ID, -1)
        if (roomId == -1) {
            Log.e(TAG, "Invalid room ID received!")
        } else {
            Log.d(TAG, "Received room ID: $roomId")
        }
    }
}