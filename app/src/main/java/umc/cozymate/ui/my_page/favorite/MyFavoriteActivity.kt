package umc.cozymate.ui.my_page.favorite

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import umc.cozymate.databinding.ActivityMyFavoriteBinding
import umc.cozymate.ui.cozy_home.room.room_detail.VerticalSpaceItemDecoration
import umc.cozymate.ui.cozy_home.room_detail.RoomDetailActivity
import umc.cozymate.ui.cozy_home.roommate.roommate_detail.RoommateDetailActivity
import umc.cozymate.ui.viewmodel.FavoriteViewModel
import umc.cozymate.ui.viewmodel.RoomDetailViewModel
import umc.cozymate.ui.viewmodel.RoommateDetailViewModel
import umc.cozymate.util.SnackbarUtil
import umc.cozymate.util.StatusBarUtil

@AndroidEntryPoint
class MyFavoriteActivity : AppCompatActivity() {
    private val TAG = this.javaClass.simpleName
    private lateinit var binding: ActivityMyFavoriteBinding
    private val favoriteViewModel: FavoriteViewModel by viewModels()
    private val roommateDetailViewModel: RoommateDetailViewModel by viewModels()
    private val roomDetailViewModel: RoomDetailViewModel by viewModels()
    private lateinit var roomAdapter: FavoriteRoomRVAdapter
    private lateinit var roommateAdapter: FavoriteRoommateRVAdapter
    private var isRoommateSelected: Boolean = true
    private var otherRoomId: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        StatusBarUtil.updateStatusBarColor(this, Color.WHITE)
        binding.refreshLayout.isRefreshing = true
        setupObservers()
        setupRVAdapters()
        setClickListeners()
        setOnRefreshListener()
        binding.tvFavoriteRoommate.isSelected = true
        fetchRoommateList()
        binding.refreshLayout.isRefreshing = false
    }

    override fun onResume() {
        super.onResume()
        if (isRoommateSelected) {
            fetchRoommateList()
        } else {
            fetchRoomList()
        }
    }

    private fun setupRVAdapters() {
        setRoommateList()
        setRoomList()
    }

    private fun setRoommateList() {
        roommateAdapter = FavoriteRoommateRVAdapter(emptyList()) { memberId ->
            roommateDetailViewModel.getOtherUserDetailInfo(memberId)
        }
        binding.rvFavoriteRoommate.adapter = roommateAdapter
        binding.rvFavoriteRoommate.layoutManager = LinearLayoutManager(this)
        binding.rvFavoriteRoommate.addItemDecoration(
            VerticalSpaceItemDecoration(32)
        )
    }

    private fun setRoomList() {
        roomAdapter = FavoriteRoomRVAdapter(emptyList()) { roomId ->
            lifecycleScope.launch {
                otherRoomId = roomId
                roomDetailViewModel.getOtherRoomInfo(roomId)
            }
        }
        binding.rvFavoriteRoom.adapter = roomAdapter
        binding.rvFavoriteRoom.layoutManager = LinearLayoutManager(this)
        binding.rvFavoriteRoom.addItemDecoration(
            VerticalSpaceItemDecoration(32)
        )
    }

    private fun setupObservers() {
        observeOtherUserInfo()
        observeRoomDetailInfo()
        observeFavoriteRoomList()
        observeFavoriteMemberList()
        favoriteViewModel.isLoading1.observe(this, Observer {
            setProgressbar(it)
        })
        favoriteViewModel.isLoading2.observe(this, Observer {
            setProgressbar(it)
        })
    }

    private fun observeOtherUserInfo() {
        roommateDetailViewModel.otherUserDetailInfo.observe(this) { otherUserDetail ->
            if (otherUserDetail == null) return@observe
            else {
                val intent = Intent(this@MyFavoriteActivity, RoommateDetailActivity::class.java)
                intent.putExtra("other_user_detail", otherUserDetail)
                startActivity(intent)
            }
        }
        roommateDetailViewModel.isLoading.observe(this@MyFavoriteActivity) { isLoading ->
            setProgressbar(isLoading)
        }
        roommateDetailViewModel.errorResponse.observe(this) { error ->
            SnackbarUtil.showCustomSnackbar(
                context = this@MyFavoriteActivity,
                message = "존재하지 않는 사용자입니다.",
                iconType = SnackbarUtil.IconType.NO,
                extraYOffset = 20
            )
        }
    }

    private fun observeRoomDetailInfo() {
        roomDetailViewModel.roomName.observe(this) { it ->
            if (it == null) return@observe
            else {
                val intent = Intent(this@MyFavoriteActivity, RoomDetailActivity::class.java
                ).apply {
                    putExtra(RoomDetailActivity.ARG_ROOM_ID, otherRoomId)
                }
                startActivity(intent)
            }
        }
        roomDetailViewModel.errorResponse.observe(this) { error ->
            SnackbarUtil.showCustomSnackbar(
                context = this@MyFavoriteActivity,
                message = "존재하지 않는 방입니다.",
                iconType = SnackbarUtil.IconType.NO,
                extraYOffset = 20
            )
        }
    }

    private fun observeFavoriteMemberList() {
        favoriteViewModel.getFavoritesMembersResponse.observe(this, Observer { response ->
            if (response == null) return@Observer
            if (response.result.isNotEmpty()) {
                binding.rvFavoriteRoom.visibility = View.GONE
                binding.tvEmptyList.visibility = View.GONE
                binding.rvFavoriteRoommate.visibility = View.VISIBLE
                roommateAdapter.submitList(response.result)
            } else {
                binding.rvFavoriteRoom.visibility = View.GONE
                binding.tvEmptyList.visibility = View.VISIBLE
                binding.rvFavoriteRoommate.visibility = View.GONE
                binding.tvEmptyList.text = "아직 찜한 룸메이트가 없어요"
            }
        })
    }

    private fun observeFavoriteRoomList() {
        favoriteViewModel.getFavoritesRoomsResponse.observe(this, Observer { response ->
            if (response == null) return@Observer
            if (response.result.isNotEmpty()) {
                binding.rvFavoriteRoom.visibility = View.VISIBLE
                binding.tvEmptyList.visibility = View.GONE
                binding.rvFavoriteRoommate.visibility = View.GONE
                roomAdapter.submitList(response.result)
            } else {
                binding.rvFavoriteRoom.visibility = View.GONE
                binding.tvEmptyList.visibility = View.VISIBLE
                binding.rvFavoriteRoommate.visibility = View.GONE
                binding.tvEmptyList.text = "아직 찜한 방이 없어요"

            }
        })
    }

    private fun setProgressbar(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun setClickListeners() {
        with(binding) {
            val toggleSelection: (selectedView: TextView, unselectedView: TextView) -> Unit =
                { selectedView, unselectedView ->
                    selectedView.isSelected = true
                    unselectedView.isSelected = false
                }

            tvFavoriteRoommate.setOnClickListener {
                isRoommateSelected = true
                toggleSelection(tvFavoriteRoommate, tvFavoriteRoom)
                fetchRoommateList()
            }

            tvFavoriteRoom.setOnClickListener {
                isRoommateSelected = false
                toggleSelection(tvFavoriteRoom, tvFavoriteRoommate)
                fetchRoomList()
            }

            ivBack.setOnClickListener {
                finish()
            }
        }
    }

    private fun setOnRefreshListener() {
        binding.refreshLayout.setOnRefreshListener {
            if (isRoommateSelected) {
                fetchRoommateList()
            } else {
                fetchRoomList()
            }
        }
    }

    private fun fetchRoommateList() {
        lifecycleScope.launch {
            favoriteViewModel.getFavoriteRoommateList()
        }
    }

    private fun fetchRoomList() {
        lifecycleScope.launch {
            favoriteViewModel.getFavoriteRoomList()
        }
    }
}