package umc.cozymate.ui.cozy_home.room.room_detail

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import umc.cozymate.databinding.ActivityRoomDetailBinding
import umc.cozymate.ui.cozy_home.room.room_recommend.RoomRecommendVPAdapter
import umc.cozymate.ui.viewmodel.CozyHomeViewModel

@AndroidEntryPoint
class RoomDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRoomDetailBinding
    private val viewModel: CozyHomeViewModel by viewModels() // ViewModel 사용
    private lateinit var adapter: RoomRecommendVPAdapter
    private var prefList: List<String> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoomDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 닉네임 설정
        binding.tvUserName.text = viewModel.getNickname().toString()

        // RecyclerView 초기화
        setupRecyclerView()

        // 데이터 가져오기
        fetchData()
    }

    private fun setupRecyclerView() {
        // RecyclerView에 어댑터 설정
        adapter = RoomRecommendVPAdapter(emptyList(), prefList)
        binding.rvRoomDetail.adapter = adapter

        // 세로 리스트 형태로 설정
        binding.rvRoomDetail.layoutManager = LinearLayoutManager(this)

        // 아이템 간 간격 추가 (선택적)
        binding.rvRoomDetail.addItemDecoration(VerticalSpaceItemDecoration(-90))

        binding.rvRoomDetail.visibility = View.VISIBLE
    }

    private fun fetchData() {
        // ViewModel에서 데이터 가져오기
        viewModel.roomList.observe(this) { roomList ->
            if (roomList.isNullOrEmpty()) {
                // 데이터가 없을 때 처리
                binding.rvRoomDetail.visibility = View.GONE
            } else {
                // 데이터가 있을 때 어댑터에 데이터 전달
                adapter.updateData(roomList)
                binding.rvRoomDetail.visibility = View.VISIBLE
            }
        }

        // 데이터 요청
        lifecycleScope.launch {
            viewModel.fetchRecommendedRoomList()
        }
    }
}
