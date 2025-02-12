package umc.cozymate.ui.notification

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import umc.cozymate.data.model.response.roomlog.NotificationLogResponse
import umc.cozymate.databinding.ActivityNotificationBinding
import umc.cozymate.ui.MessageDetail.NotificationAdapter
import umc.cozymate.ui.cozy_home.room_detail.RoomDetailActivity
import umc.cozymate.ui.cozy_home.roommate.roommate_detail.RoommateDetailActivity
import umc.cozymate.ui.viewmodel.NotificationViewModel
import umc.cozymate.ui.viewmodel.RoommateDetailViewModel
import umc.cozymate.util.StatusBarUtil

@AndroidEntryPoint
class NotificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotificationBinding
    private lateinit var adapter1: NotificationAdapter
    private val TAG = this.javaClass.simpleName
    private val viewModel: NotificationViewModel by viewModels()
    private val detailViewModel: RoommateDetailViewModel by viewModels()
    private var contents: List<NotificationLogResponse.Result> = emptyList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        StatusBarUtil.updateStatusBarColor(this, Color.WHITE)
        setContentView(binding.root)
        lifecycleScope.launch {
            viewModel.fetchNotification()
        }
        setupObservers()
        initOnClickListener()
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            viewModel.fetchNotification()
        }
    }

    private fun setupObservers() {
        viewModel.notificationResponse.observe(this, Observer { response ->
            if (response == null) return@Observer
            if (response.isSuccess) {
                contents = response.result.reversed() // 알림 리스트 역순 정렬
                updateContents()
            }
        })

        detailViewModel.otherUserDetailInfo.observe(this) { otherUserDetail ->
            if (otherUserDetail == null) return@observe
            else {
                val intent = Intent(this@NotificationActivity, RoommateDetailActivity::class.java)
                intent.putExtra("other_user_detail", otherUserDetail)
                startActivity(intent)
            }
        }
    }

    private fun updateContents() {
        Log.d(TAG, "뷰 생성 : ${contents}")
        if (contents.isNullOrEmpty()) {
            binding.rvNotificationList.visibility = View.GONE
            binding.tvEmpty.visibility = View.VISIBLE
        } else {
            adapter1 = NotificationAdapter(contents.reversed()) { targetId, category ->
                try {
                    when (category) {
                        "방" -> {
                            val intent = Intent(
                                this@NotificationActivity,
                                RoomDetailActivity::class.java
                            ).apply {
                                putExtra(RoomDetailActivity.ARG_ROOM_ID, targetId)
                            }
                            startActivity(intent)
                        }

                        "방 참여요청" -> {
                            navigatorToRoommateDetail(targetId)
                        }

                        "초대요청" -> {
                            navigatorToRoommateDetail(targetId)
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(binding.root.context, "존재하지 않는 방 또는 멤버입니다", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            binding.rvNotificationList.visibility = View.VISIBLE
            binding.tvEmpty.visibility = View.GONE

            // RecyclerView에 어댑터 설정
            binding.rvNotificationList.apply {
                adapter = adapter1
                layoutManager = LinearLayoutManager(context)
            }
        }
    }

    private fun initOnClickListener() {
        binding.ivBack.setOnClickListener {
            finish()
        }
    }

    private fun navigatorToRoommateDetail(memberId: Int) {
        detailViewModel.getOtherUserDetailInfo(memberId)
    }
}
