package umc.cozymate.ui.cozy_home.room.room_detail

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import umc.cozymate.databinding.ActivityRoomDetailBinding
import umc.cozymate.ui.cozy_home.room.search_room.SearchRoomActivity
import umc.cozymate.ui.cozy_home.room_detail.CozyRoomDetailInfoActivity
import umc.cozymate.ui.viewmodel.CozyHomeViewModel
import umc.cozymate.util.StatusBarUtil

@AndroidEntryPoint
class RoomDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRoomDetailBinding
    private val viewModel: CozyHomeViewModel by viewModels() // ViewModel 사용
    private var prefList: List<String> = emptyList()
    private lateinit var roomRecommendListRVA: RoomRecommendListRVA

    companion object {
        const val ARG_ROOM_ID = "room_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoomDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        StatusBarUtil.updateStatusBarColor(this@RoomDetailActivity, Color.WHITE)

        binding.ivBack.setOnClickListener {
            finish()
        }
        // 방검색으로 이동
        binding.lyRoomSearch.setOnClickListener {
            val intent = Intent(this, SearchRoomActivity::class.java)
            startActivity(intent)
        }
        // 닉네임 설정
        binding.tvUserName.text = viewModel.getNickname().toString()

        setupRecyclerView()

        // 데이터 가져오기
        fetchData()
    }

    private fun setupRecyclerView() {
        // 어댑터 초기화
        roomRecommendListRVA = RoomRecommendListRVA(emptyList(), prefList) { selectedRoom ->
            val intent = Intent(this, CozyRoomDetailInfoActivity::class.java).apply {
                putExtra("room_id", selectedRoom.roomId)
                Log.d("RoomDetailActivity", "리사이클러 뷰 클릭 ${selectedRoom.roomId}")
            }
            startActivity(intent)
        }

        // 아이템 간 간격 추가
        binding.rvRoomDetail.addItemDecoration(
            VerticalSpaceItemDecoration(32)
        )

        // RecyclerView 설정
        binding.rvRoomDetail.apply {
            adapter = roomRecommendListRVA
            layoutManager = LinearLayoutManager(this@RoomDetailActivity)
        }
    }

    private fun fetchData() {
        lifecycleScope.launch {
            viewModel.fetchRecommendedRoomList()
        }

        viewModel.roomList.observe(this) { roomList ->
            if (roomList.isNullOrEmpty()) {
                binding.rvRoomDetail.visibility = View.GONE
                binding.tvEmptyRoom.visibility = View.VISIBLE
            } else {
                binding.rvRoomDetail.visibility = View.VISIBLE
                binding.tvEmptyRoom.visibility = View.GONE
                roomRecommendListRVA.updateData(roomList) // 데이터 갱신
            }
        }
    }

}