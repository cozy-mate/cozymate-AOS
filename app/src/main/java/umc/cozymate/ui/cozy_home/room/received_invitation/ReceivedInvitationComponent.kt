package umc.cozymate.ui.cozy_home.room.received_invitation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import umc.cozymate.R
import umc.cozymate.data.domain.UserRoomState
import umc.cozymate.databinding.FragmentMyReceivedInvitationBinding
import umc.cozymate.ui.cozy_home.room_detail.RoomDetailActivity
import umc.cozymate.ui.cozy_home.roommate.roommate_detail.CozyHomeRoommateDetailActivity
import umc.cozymate.ui.viewmodel.RoomRequestViewModel

@AndroidEntryPoint
class ReceivedInvitationComponent : Fragment() {
    private var _binding: FragmentMyReceivedInvitationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RoomRequestViewModel by viewModels()
    var roomId = -1
    companion object {
        fun newInstance() = ReceivedInvitationComponent()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        Main: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyReceivedInvitationBinding.inflate(inflater, Main, false)
        // 방 존재 여부를 spf로 조회한 방 아이디로 확인합니다.
        val spf = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        roomId = spf.getInt("room_id", 0)
        observeRoomList()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getInvitedRoomList()
        }
        // 기본으로 0개 띄우기
        binding.tvRequestNum.text = "0개의"
        binding.tvRequestNum.setTextColor(ContextCompat.getColor(requireContext(), R.color.unuse_font))
        binding.clComponent.visibility = View.GONE
        binding.clEmptyRoommate.visibility = View.VISIBLE
        binding.clEmptyRoommate.isEnabled = true
        binding.clEmptyRoommate.setOnClickListener { // 룸메이트 더보기 페이지로 이동
            val intent = Intent(requireActivity(), CozyHomeRoommateDetailActivity::class.java)
            startActivity(intent)
        }
        binding.rvMyReceived.visibility = View.GONE
        return binding.root
    }

    private fun observeRoomList() {
        // 클릭 시 방 상세 정보 페이지로 이동하도록 어댑터 설정
        val adapter = ReceivedInvitationAdapter { roomId ->
            val intent = Intent(requireActivity(), RoomDetailActivity::class.java).apply {
                putExtra(RoomDetailActivity.ARG_ROOM_ID, roomId)
            }
            startActivity(intent)
        }
        binding.rvMyReceived.adapter = adapter
        binding.rvMyReceived.layoutManager = LinearLayoutManager(requireContext())
        // 초대 요청 받은 방 목록(/rooms/invited) api 응답 옵저빙
        viewModel.invitedRoomResponse.observe(viewLifecycleOwner) { response ->
            val roomList = response?.result?.roomList ?: emptyList()
            if (roomList.isNotEmpty()) {
                binding.tvRequestNum.text = "${response?.result?.requestCount}개의"
                binding.tvRequestNum.setTextColor(ContextCompat.getColor(requireContext(), R.color.main_blue))
                binding.clComponent.visibility = View.VISIBLE
                binding.clEmptyRoommate.visibility = View.GONE
                binding.rvMyReceived.visibility = View.VISIBLE
                adapter.submitList(roomList)
            } else {
                if (roomId == 0 || roomId == -1) {
                    binding.tvRequestNum.text = "0개의"
                    binding.tvRequestNum.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.unuse_font
                        )
                    )
                    binding.clComponent.visibility = View.GONE
                    binding.clEmptyRoommate.visibility = View.VISIBLE
                    binding.clEmptyRoommate.isEnabled = true
                    binding.clEmptyRoommate.setOnClickListener { // 룸메이트 더보기 페이지로 이동
                        val intent =
                            Intent(requireActivity(), CozyHomeRoommateDetailActivity::class.java)
                        startActivity(intent)
                    }
                    binding.rvMyReceived.visibility = View.GONE
                } else {
                    // 방 존재할 때는 0개일 때 가리기
                    binding.clComponent.visibility = View.GONE
                }
            }
        }
        // 로딩중 옵저빙
        viewModel.isLoading3.observe(viewLifecycleOwner) { isLoading ->
            // 로딩 중에는 빈 화면을 띄웁니다
            if (isLoading == true || isLoading == null) {
                binding.clComponent.visibility = View.GONE
            } else {
                binding.clComponent.visibility = View.VISIBLE
            }
        }
    }

    // 홈 화면 시 새로고침하는 함수입니다
    fun refreshData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getInvitedRoomList()
        }
    }
}