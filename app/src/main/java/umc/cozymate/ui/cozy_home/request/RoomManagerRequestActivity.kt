package umc.cozymate.ui.cozy_home.request

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import umc.cozymate.R
import umc.cozymate.data.model.response.member.stat.GetMemberDetailInfoResponse
import umc.cozymate.databinding.ActivityRoomManagerRequestBinding
import umc.cozymate.ui.cozy_home.roommate.roommate_detail.RoommateDetailActivity
import umc.cozymate.ui.viewmodel.CozyHomeViewModel
import umc.cozymate.ui.viewmodel.RoomRequestViewModel
import umc.cozymate.ui.viewmodel.RoommateDetailViewModel

@AndroidEntryPoint
class RoomManagerRequestActivity : AppCompatActivity() {
    private val TAG = this.javaClass.simpleName
    lateinit var binding: ActivityRoomManagerRequestBinding
    private val cozyHomeViewModel: CozyHomeViewModel by viewModels()
    private val roomRequestViewModel: RoomRequestViewModel by viewModels()
    private val roommateDetailViewModel: RoommateDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_room_manager_request)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setBackBtn()
        setRequestList()
        fetchRequest()
    }

    private fun setBackBtn() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setRequestList() {
        roommateDetailViewModel.otherUserDetailInfo.observe(this) { otherUserDetail ->
            if (otherUserDetail == null) return@observe
            else goToRoommateDetail(otherUserDetail)
        }
        val adapter = ReceivedJoinRequestAdapter { memberId ->
            roommateDetailViewModel.getOtherUserDetailInfo(memberId)
        }
        binding.rvRequestList.adapter = adapter
        binding.rvRequestList.layoutManager = LinearLayoutManager(baseContext)
        roomRequestViewModel.pendingMemberResponse.observe(this) { response ->
            val roomList = response?.result ?: emptyList()
            if (roomList.isNotEmpty()) {
                binding.tvRequestNum.text = "${roomList.size}개의"
                adapter.submitList(roomList)
            } else {
                binding.tvRequestNum.text = "0개의"
            }
        }
        roomRequestViewModel.isLoading2.observe(this) { isLoading ->
            if (isLoading == true || isLoading == null) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun goToRoommateDetail(otherUserDetail: GetMemberDetailInfoResponse.Result) {
        val intent = Intent(this, RoommateDetailActivity::class.java)
        intent.putExtra("other_user_detail", otherUserDetail)
        startActivity(intent)
    }

    private fun fetchRequest() {
        this.lifecycleScope.launch {
            roomRequestViewModel.getPendingMemberList()
        }
    }
}