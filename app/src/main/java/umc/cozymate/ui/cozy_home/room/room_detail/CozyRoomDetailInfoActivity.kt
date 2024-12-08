package umc.cozymate.ui.cozy_home.room_detail

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import umc.cozymate.R
import umc.cozymate.databinding.ActivityCozyRoomDetailInfoBinding
import umc.cozymate.ui.MainActivity
import umc.cozymate.ui.pop_up.OneButtonPopup
import umc.cozymate.ui.pop_up.PopupClick
import umc.cozymate.ui.viewmodel.MakingRoomViewModel

class CozyRoomDetailInfoActivity : AppCompatActivity() {
    private val TAG = this.javaClass.simpleName
    private lateinit var binding: ActivityCozyRoomDetailInfoBinding
    private val viewModel: MakingRoomViewModel by viewModels()
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
        // 방 삭제하기
        setDeleteRoom()
    }
    fun setDeleteRoom() {
        with(binding) {
            ivBack.visibility = View.GONE
            fabRequestEnterRoom.visibility = View.GONE
            btnDeleteRoom.visibility = View.VISIBLE
            btnDeleteRoom.isEnabled = true
            btnDeleteRoom.setOnClickListener {
                // 방 삭제 요청
                viewModel.deleteRoom()
            }
        }
        // 방 삭제 옵저빙
        viewModel.roomDeletionResult.observe(this) { result ->
            if (result.isSuccess) {
                showDeleteRoomPopup()
            } else {
                Toast.makeText(this, "방 삭제에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }
    // 삭제 확인 팝업 띄우기
    fun showDeleteRoomPopup() {
        val text = listOf("방이 삭제 됐어요", "", "확인")
        val dialog = OneButtonPopup(text, object : PopupClick {
            override fun clickFunction() {
                loadMainActivity()
            }
        }, false)
        dialog.show(supportFragmentManager, "roomDeletionPopup")
    }
    // 코지홈으로 화면 전환
    fun loadMainActivity() {
        val intent = Intent(this@CozyRoomDetailInfoActivity, MainActivity::class.java)
        // 방 삭제 후에 상태변수를 설정해줍니다.
        intent.putExtra("isRoomExist", false)
        intent.putExtra("isRoomManager", false)
        startActivity(intent)
        this.finish()
    }
}