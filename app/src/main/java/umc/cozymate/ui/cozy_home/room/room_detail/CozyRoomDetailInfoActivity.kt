package umc.cozymate.ui.cozy_home.room.room_detail

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import umc.cozymate.data.domain.SortType
import umc.cozymate.databinding.ActivityCozyRoomDetailInfoBinding
import umc.cozymate.ui.cozy_home.home.FilterBottomSheetDialog
import umc.cozymate.ui.cozy_home.room.search_room.SearchRoomActivity
import umc.cozymate.ui.cozy_home.room_detail.RoomDetailActivity
import umc.cozymate.ui.viewmodel.RoomDetailViewModel
import umc.cozymate.util.AnalyticsConstants
import umc.cozymate.util.AnalyticsEventLogger
import umc.cozymate.util.StatusBarUtil
import umc.cozymate.util.setStatusBarTransparent

@AndroidEntryPoint
class CozyRoomDetailInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCozyRoomDetailInfoBinding
    private val viewModel: RoomDetailViewModel by viewModels() // ViewModel 사용
    private var prefList: List<String> = emptyList()
    private lateinit var roomRecommendListRVA: RoomRecommendListRVA
    private var screenEnterTime: Long = 0

    companion object {
        const val ARG_ROOM_ID = "room_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCozyRoomDetailInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(bottom = systemBarsInsets.bottom) // 네비게이션 바 높이만큼 패딩 추가
            insets
        }
        this.setStatusBarTransparent()
        StatusBarUtil.updateStatusBarColor(this@CozyRoomDetailInfoActivity, Color.WHITE)

        binding.ivBack.setOnClickListener {
            finish()
        }
        // 방검색으로 이동
        binding.lyRoomSearch.setOnClickListener {
            // GA 이벤트 로그 추가
            AnalyticsEventLogger.logEvent(
                eventName = AnalyticsConstants.Event.INPUT_BOX_ROOM_SEARCH,
                category = AnalyticsConstants.Category.CONTENT_ROOM,
                action = AnalyticsConstants.Action.INPUT_BOX,
                label = AnalyticsConstants.Label.ROOM_SEARCH,
            )

            val intent = Intent(this, SearchRoomActivity::class.java)
            startActivity(intent)
        }
        // 닉네임 설정
        binding.tvUserName.text = viewModel.getNickname().toString()

        binding.layoutSearchFilter.setOnClickListener {
            // GA 이벤트 로그 추가
            AnalyticsEventLogger.logEvent(
                eventName = AnalyticsConstants.Event.BUTTON_CLICK_SORTING,
                category = AnalyticsConstants.Category.CONTENT_ROOM,
                action = AnalyticsConstants.Action.BUTTON_CLICK,
                label = AnalyticsConstants.Label.SORTING,
            )

            val sortBottomSheet = FilterBottomSheetDialog(
                onSortSelected = { selectedSortType ->
                    // 한국어로 변환 후 텍스트 업데이트
                    binding.tvRoomSearchFilter.text = getSortTypeInKorean(selectedSortType)

                    // ViewModel의 sortType 업데이트
                    viewModel.updateSortType(selectedSortType)

                    // 데이터 다시 로드
                    lifecycleScope.launch {
                        viewModel.fetchRecommendedRoomList()
                    }
                },
                currentSortType = viewModel.getSortType() // 현재 선택된 정렬 값 전달
            )

            sortBottomSheet.show(supportFragmentManager, "SortBottomSheetDialog")
        }

        setupRecyclerView()

        // 데이터 가져오기
        fetchData()
    }

    override fun onResume() {
        super.onResume()
        screenEnterTime = System.currentTimeMillis()
    }

    override fun onPause() {
        super.onPause()
        val screenLeaveTime = System.currentTimeMillis()
        val sessionDuration = screenLeaveTime - screenEnterTime // 밀리초 단위

        // GA 이벤트 로그 추가
        AnalyticsEventLogger.logEvent(
            eventName = AnalyticsConstants.Event.SESSION_TIME_ROOM_CONTENT,
            category = AnalyticsConstants.Category.CONTENT_ROOM,
            action = AnalyticsConstants.Action.SESSION_TIME,
            label = AnalyticsConstants.Label.ROOM_CONTENT,
            duration = sessionDuration
        )
    }

    private fun getSortTypeInKorean(sortType: String): String {
        return when (sortType) {
            SortType.LATEST.value -> "최신순"
            SortType.AVERAGE_RATE.value -> "평균일치율순"
            SortType.CLOSING_SOON.value -> "마감임박순"
            else -> "최신순"
        }
    }
    private fun setupRecyclerView() {
        // 어댑터 초기화
        roomRecommendListRVA = RoomRecommendListRVA(emptyList(), prefList) { selectedRoom ->

            // GA 이벤트 로그 추가
            AnalyticsEventLogger.logEvent(
                eventName = AnalyticsConstants.Event.BUTTON_CLICK_ROOM_COMPONENT,
                category = AnalyticsConstants.Category.CONTENT_ROOM,
                action = AnalyticsConstants.Action.BUTTON_CLICK,
                label = AnalyticsConstants.Label.ROOM_COMPONENT,
            )

            val intent = Intent(this, RoomDetailActivity::class.java).apply {
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
            layoutManager = LinearLayoutManager(this@CozyRoomDetailInfoActivity)
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