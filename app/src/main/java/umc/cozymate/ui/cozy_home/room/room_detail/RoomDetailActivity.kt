package umc.cozymate.ui.cozy_home.room.room_detail

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import umc.cozymate.R

class RoomDetailActivity : AppCompatActivity() {
    private val TAG = this.javaClass.simpleName
    // 방 id는 Intent를 통해 불러옵니다
    private var roomId: Int? = 0
    companion object {
        const val ARG_ROOM_ID = "room_id"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_room_detail)
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