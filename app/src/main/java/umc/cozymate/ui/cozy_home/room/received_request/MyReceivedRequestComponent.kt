package umc.cozymate.ui.cozy_home.room.received_request

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import umc.cozymate.databinding.FragmentMyReceivedRequestComponentBinding
import umc.cozymate.ui.cozy_home.room.room_detail.RoomDetailActivity
import umc.cozymate.ui.cozy_home.room.sent_request.RoomRequestViewModel
import umc.cozymate.ui.cozy_home.roommate.roommate_detail.CozyHomeRoommateDetailActivity

@AndroidEntryPoint
class MyReceivedRequestComponent : Fragment() {
    private var _binding: FragmentMyReceivedRequestComponentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RoomRequestViewModel by viewModels()
    companion object {
        fun newInstance() = MyReceivedRequestComponent()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        Main: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyReceivedRequestComponentBinding.inflate(inflater, Main, false)
        observeMemberList()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getPendingMemberList()
        }
        return binding.root
    }

    private fun observeMemberList() {
        // 클릭 시 방 상세정보 페이지로 이동하도록 어댑터 설정
        val adapter = ReceivedRequestAdapter { roomId ->
            val intent = Intent(requireActivity(), CozyHomeRoommateDetailActivity::class.java).apply {
                putExtra(RoomDetailActivity.ARG_ROOM_ID, roomId)
            }
            startActivity(intent)
        }
        binding.rvMyReceived.adapter = adapter
        binding.rvMyReceived.layoutManager = LinearLayoutManager(requireContext())
        // 참여요청한 방 목록 api 응답 옵저빙
        viewModel.PendingMemberResponse.observe(viewLifecycleOwner) { response ->
            val roomList = response?.result ?: emptyList()
            if (roomList.isNotEmpty()) {
                binding.clEmptyRoommate.visibility = View.GONE
                binding.rvMyReceived.visibility = View.VISIBLE
                adapter.submitList(roomList)
            } else {
                binding.clEmptyRoommate.visibility = View.VISIBLE
                binding.clEmptyRoommate.isEnabled = true
                binding.clEmptyRoommate.setOnClickListener { // 룸메이트 더보기 페이지로 이동
                    val intent = Intent(requireActivity(), CozyHomeRoommateDetailActivity::class.java)
                    startActivity(intent)
                }
                binding.rvMyReceived.visibility = View.GONE
            }
        }
        // 로딩중 옵저빙
        viewModel.isLoading2.observe(viewLifecycleOwner) { isLoading ->
            //binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }
}