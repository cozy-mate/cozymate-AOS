package umc.cozymate.ui.notification

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import umc.cozymate.databinding.ActivityNotificationBinding
import umc.cozymate.ui.MainActivity
import umc.cozymate.ui.cozy_home.room_detail.RoomDetailActivity
import umc.cozymate.ui.cozy_home.roommate.roommate_detail.RoommateDetailActivity
import umc.cozymate.ui.viewmodel.NotificationViewModel
import umc.cozymate.ui.viewmodel.RoomDetailViewModel
import umc.cozymate.ui.viewmodel.RoommateDetailViewModel
import umc.cozymate.util.SnackbarUtil
import umc.cozymate.util.StatusBarUtil

@AndroidEntryPoint
class NotificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotificationBinding
    private lateinit var notificationAdapter: NotificationAdapter
    private val TAG = this.javaClass.simpleName
    private val notificationViewModel: NotificationViewModel by viewModels()
    private val roomDetailViewModel: RoomDetailViewModel by viewModels()
    private val roommateDetailViewModel: RoommateDetailViewModel by viewModels()
    private var otherRoomId: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        StatusBarUtil.updateStatusBarColor(this, Color.WHITE)
        setContentView(binding.root)
        binding.refreshLayout.isRefreshing = true
        setupObservers()
        setonRefreshListener()
        binding.ivBack.setOnClickListener {
            finish()
        }
        fetchData()
        binding.refreshLayout.isRefreshing = false
    }

    override fun onResume() {
        super.onResume()
        binding.refreshLayout.isRefreshing = true
        fetchData()
        binding.refreshLayout.isRefreshing = false
    }

    private fun setupObservers() {
        roomDetailViewModel.roomName.observe(this) { it ->
            if (it == null) return@observe
            else {
                val intent = Intent(this@NotificationActivity, RoomDetailActivity::class.java
                ).apply {
                    putExtra(RoomDetailActivity.ARG_ROOM_ID, otherRoomId)
                }
                startActivity(intent)
            }
        }

        roommateDetailViewModel.errorResponse.observe(this) { error ->
            SnackbarUtil.showCustomSnackbar(
                context = this@NotificationActivity,
                message = "존재하지 않는 사용자입니다.",
                iconType = SnackbarUtil.IconType.NO,
                extraYOffset = 20
            )
        }

        roommateDetailViewModel.otherUserDetailInfo.observe(this) { otherUserDetail ->
            if (otherUserDetail == null) return@observe
            else {
                val intent = Intent(this@NotificationActivity, RoommateDetailActivity::class.java)
                intent.putExtra("other_user_detail", otherUserDetail)
                startActivity(intent)
            }
        }

        roomDetailViewModel.errorResponse.observe(this) { error ->
            SnackbarUtil.showCustomSnackbar(
                context = this@NotificationActivity,
                message = "존재하지 않는 방입니다.",
                iconType = SnackbarUtil.IconType.NO,
                extraYOffset = 20
            )
        }
    }

    private fun setonRefreshListener() {
        binding.refreshLayout.setOnRefreshListener {
            fetchData()
            binding.refreshLayout.isRefreshing = false
        }
    }

    private fun fetchData() {
        notificationAdapter = NotificationAdapter { targetId, category ->
            when (category) {
                NotificationType.TYPE_NOTICE.value -> {
                    // TODO: 공지사항 화면으로 이동
                }
                NotificationType.TYPE_ROOM.value -> {
                    val intent = Intent(this, MainActivity::class.java).apply {
                        putExtra("destination", "cozybot")
                    }
                    startActivity(intent)
                }

                NotificationType.TYPE_REQUEST_JOIN.value -> {
                    roommateDetailViewModel.getOtherUserDetailInfo(targetId)
                }

                NotificationType.TYPE_REQUEST_INVITATION.value -> {
                    lifecycleScope.launch {
                        otherRoomId = targetId
                        roomDetailViewModel.getOtherRoomInfo(targetId)
                    }
                }
            }
        }

        binding.rvNotificationList.apply {
            adapter = notificationAdapter.withLoadStateFooter(
                footer = NotificationLoadingStateAdapter()
            )
            layoutManager = LinearLayoutManager(this@NotificationActivity)
        }

        lifecycleScope.launch {
            notificationViewModel.notifications.collectLatest { data ->
                notificationAdapter.submitData(data)
            }
        }

        notificationAdapter.addLoadStateListener { loadStates ->
            val isEmpty = loadStates.refresh is LoadState.NotLoading &&
                    notificationAdapter.itemCount == 0
            binding.rvNotificationList.isVisible = !isEmpty
            binding.ivEmptyList.isVisible = isEmpty

            (loadStates.refresh as? LoadState.Error)?.let {
                SnackbarUtil.showCustomSnackbar(
                    this, it.error.localizedMessage ?: "알 수 없는 오류",
                    SnackbarUtil.IconType.NO
                )
            }
        }
    }
}
