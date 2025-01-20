package umc.cozymate.ui.cozy_home.room.received_join_request

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
import umc.cozymate.databinding.FragmentMyReceivedJoinRequestBinding
import umc.cozymate.ui.cozy_home.roommate.roommate_detail.CozyHomeRoommateDetailActivity
import umc.cozymate.ui.cozy_home.roommate.roommate_detail.RoommateDetailActivity
import umc.cozymate.ui.viewmodel.RoomRequestViewModel

@AndroidEntryPoint
class ReceivedJoinRequestComponent : Fragment() {
    private var _binding: FragmentMyReceivedJoinRequestBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RoomRequestViewModel by viewModels()

    companion object {
        fun newInstance() = ReceivedJoinRequestComponent()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        Main: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyReceivedJoinRequestBinding.inflate(inflater, Main, false)
        observeRoomList()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getPendingMemberList()
        }
        return binding.root
    }

    private fun observeRoomList() {
        // 클릭 시 멤버 상세 정보 페이지로 이동하도록 어댑터 설정
        val adapter = ReceivedJoinRequestAdapter { memberId ->
            val intent = Intent(requireActivity(), RoommateDetailActivity::class.java).apply {
                    putExtra("other_user_detail", memberId)
                }
            startActivity(intent)
        }
        binding.rvMyReceived.adapter = adapter
        binding.rvMyReceived.layoutManager = LinearLayoutManager(requireContext())
        // 참여요청한 멤버 목록 api 응답 옵저빙
        viewModel.PendingMemberResponse.observe(viewLifecycleOwner) { response ->
            val roomList = response?.result ?: emptyList()
            if (roomList.isNotEmpty()) {
                binding.tvRequestNum.text = "${roomList.size}개의"
                binding.clComponent.visibility = View.VISIBLE
                binding.clEmptyRoommate.visibility = View.GONE
                binding.rvMyReceived.visibility = View.VISIBLE
                adapter.submitList(roomList)
            } else {
                binding.clComponent.visibility = View.GONE
                /*binding.tvRequestNum.text = "0개의"
                binding.clComponent.visibility = View.GONE
                binding.clEmptyRoommate.visibility = View.VISIBLE
                binding.clEmptyRoommate.isEnabled = true
                binding.clEmptyRoommate.setOnClickListener { // 룸메이트 더보기 페이지로 이동
                    val intent =
                        Intent(requireActivity(), CozyHomeRoommateDetailActivity::class.java)
                    startActivity(intent)
                }
                binding.rvMyReceived.visibility = View.GONE*/
            }
        }
        // 방장이 아닌 경우에는 constraintlayout이 보이지 않도록 합니다.
        viewModel.errorResponse.observe(viewLifecycleOwner) { response ->
            if (response.message == "방장이 아닙니다") {
                binding.clComponent.visibility = View.GONE
            }
        }
        // 로딩중 옵저빙
        viewModel.isLoading2.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading == true || isLoading == null) {
                binding.clComponent.visibility = View.GONE
            } else {
                binding.clComponent.visibility = View.VISIBLE
            }

        }
    }

    fun refreshData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getPendingMemberList()
        }
    }
}