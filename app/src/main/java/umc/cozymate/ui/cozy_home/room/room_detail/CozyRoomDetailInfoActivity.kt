package umc.cozymate.ui.cozy_home.room_detail

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import umc.cozymate.R
import umc.cozymate.databinding.ActivityCozyRoomDetailInfoBinding

class CozyRoomDetailInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCozyRoomDetailInfoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCozyRoomDetailInfoBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cozy_room_detail_info)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // 방 삭제하기
        setDeleteRoom()

    }
    fun setDeleteRoom() {
        with(binding) {
            fabRequestEnterRoom.visibility = View.GONE
            btnDeleteRoom.visibility  = View.VISIBLE
            btnDeleteRoom.isEnabled = true
            btnDeleteRoom.setOnClickListener {
                // 방 삭제 요청

            }
        }
    }
}