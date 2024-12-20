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
import umc.cozymate.ui.viewmodel.CozyHomeViewModel

@AndroidEntryPoint
class RoomDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRoomDetailBinding
    private val viewModel: CozyHomeViewModel by viewModels() // ViewModel 사용
    private var prefList: List<String> = emptyList()
    private lateinit var roomRecommendListRVA: RoomRecommendListRVA

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoomDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 닉네임 설정
        binding.tvUserName.text = viewModel.getNickname().toString()

        setupRecyclerView()

        // 데이터 가져오기
        fetchData()
    }

    private fun setupRecyclerView() {
        // 어댑터 초기화
        roomRecommendListRVA = RoomRecommendListRVA(emptyList(), prefList)

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

    //    private fun fetchData() {
//        lifecycleScope.launch {
//            viewModel.fetchRecommendedRoomList()
//        }
//        viewModel.roomList.observe(this) { roomList ->
//            if (roomList.isNullOrEmpty()) {
//                binding.rvRoomDetail.visibility = View.GONE
//                binding.tvEmptyRoom.visibility = View.VISIBLE
//            } else {
////                val adapter = RoomRecommendListRVA(roomList, prefList)
//                binding.rvRoomDetail.adapter = roomRecommendListRVA
//            }
//        }
//    }
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
