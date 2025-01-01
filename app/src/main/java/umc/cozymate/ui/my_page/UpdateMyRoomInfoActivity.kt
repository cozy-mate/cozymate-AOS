package umc.cozymate.ui.cozy_home.room_detail

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.R
import umc.cozymate.databinding.ActivityUpdateCozyRoomDetailInfoBinding
import umc.cozymate.ui.MainActivity
import umc.cozymate.ui.pop_up.OneButtonPopup
import umc.cozymate.ui.pop_up.PopupClick
import umc.cozymate.ui.viewmodel.MakingRoomViewModel

// TODO: 방 수정, 방 나가기, 방 전환(공개방/비공개방)
@AndroidEntryPoint
class UpdateMyRoomInfoActivity : AppCompatActivity() {
    private val TAG = this.javaClass.simpleName
    private lateinit var binding: ActivityUpdateCozyRoomDetailInfoBinding
    private val viewModel: MakingRoomViewModel by viewModels()
    private var roomId: Int? = 0
    // 방 id는  Intent를 통해 불러옵니다
    companion object {
        const val ARG_ROOM_ID = "room_id"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateCozyRoomDetailInfoBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // 방 id 불러오기
        roomId = intent.getIntExtra(ARG_ROOM_ID, -1)
        // 방 나가기
        setQuitRoom(roomId!!)
    }
    fun setQuitRoom(roomId: Int) {
        with(binding) {
            tvQuit.setOnClickListener {
                // 방 나가기 요청
                viewModel.quitRoom(roomId)
            }
        }
        // 방 삭제 옵저빙
        viewModel.roomQuitResult.observe(this) { result ->
            if (result.isSuccess) {
                showQuitRoomPopup()
            } else {
                Toast.makeText(this, "방 나가기를 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }
    // 삭제 확인 팝업 띄우기
    fun showQuitRoomPopup() {
        val text = listOf("방을 나갔어요", "", "확인")
        val dialog = OneButtonPopup(text, object : PopupClick {
            override fun clickFunction() {
                loadMainActivity()
            }
        }, false)
        dialog.show(supportFragmentManager, "roomDeletionPopup")
    }
    // 코지홈으로 화면 전환
    fun loadMainActivity() {
        val intent = Intent(this@UpdateMyRoomInfoActivity, MainActivity::class.java)
        // 방 나가기 후에 상태변수를 설정해줍니다.
        intent.putExtra("isRoomExist", false)
        intent.putExtra("isRoomManager", false)
        startActivity(intent)
        this.finish()
    }
}