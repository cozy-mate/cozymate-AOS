package umc.cozymate.ui.cozy_home.request

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import umc.cozymate.R
import umc.cozymate.databinding.ActivityBeforeMatchingRequestBinding
import umc.cozymate.ui.cozy_home.room_detail.RoomDetailActivity
import umc.cozymate.ui.viewmodel.CozyHomeViewModel
import umc.cozymate.ui.viewmodel.RoomRequestViewModel

@AndroidEntryPoint
class BeforeMatchingRequestActivity : AppCompatActivity() {
    private val TAG = this.javaClass.simpleName
    lateinit var binding: ActivityBeforeMatchingRequestBinding
    private val cozyHomeViewModel: CozyHomeViewModel by viewModels()
    private val roomRequestViewModel: RoomRequestViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityBeforeMatchingRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setBackBtn()
        setNickname()
        setRequestList()
        fetchRequest()
    }

    private fun setBackBtn() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setNickname() {
        val nickname = cozyHomeViewModel.getNickname()
        binding.tvNickname.text = nickname + "님이"
    }

    private fun setRequestList() {
        val adapter = SentRequestAdapter { roomId ->
            val intent = Intent(this, RoomDetailActivity::class.java).apply {
                putExtra(RoomDetailActivity.ARG_ROOM_ID, roomId)
            }
            startActivity(intent)
        }
        binding.rvRequestList.adapter = adapter
        binding.rvRequestList.layoutManager = LinearLayoutManager(baseContext)
        roomRequestViewModel.requestedRoomResponse.observe(this) { response ->
            val roomList = response?.result?.result ?: emptyList()
            if (roomList.isNotEmpty()) {
                adapter.submitList(roomList)
            }
        }
        roomRequestViewModel.isLoading1.observe(this) { isLoading ->
            if (isLoading == true || isLoading == null) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun fetchRequest() {
        this.lifecycleScope.launch {
            roomRequestViewModel.getRequestedRoomList()
        }
    }
}