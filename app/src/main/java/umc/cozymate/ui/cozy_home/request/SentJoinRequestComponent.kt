package umc.cozymate.ui.cozy_home.request

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
import umc.cozymate.databinding.FragmentMySentJoinRequestBinding
import umc.cozymate.ui.cozy_home.room_detail.RoomDetailActivity
import umc.cozymate.ui.viewmodel.RoomRequestViewModel

@AndroidEntryPoint
class SentJoinRequestComponent : Fragment() {
    private var _binding: FragmentMySentJoinRequestBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RoomRequestViewModel by viewModels()
    companion object {
        fun newInstance() = SentJoinRequestComponent()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        Main: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMySentJoinRequestBinding.inflate(inflater, Main, false)
        observeRoomList()
        binding.clComponent.visibility = View.GONE
        binding.tvTitle.visibility = View.GONE
        binding.divider.visibility = View.GONE
        binding.tvMyNickname.visibility = View.GONE
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getRequestedRoomList()
        }
        return binding.root
    }

    private fun observeRoomList() {
        // 클릭 시 방 상세정보 페이지로 이동하도록 어댑터 설정
        val adapter = SentRequestAdapter { roomId ->
            val intent = Intent(requireActivity(), RoomDetailActivity::class.java).apply {
                putExtra(RoomDetailActivity.ARG_ROOM_ID, roomId)
            }
            startActivity(intent)
        }
        binding.rvMySent.adapter = adapter
        binding.rvMySent.layoutManager = LinearLayoutManager(requireContext())
        // 참여요청한 방 목록 api 응답 옵저빙
        viewModel.requestedRoomResponse.observe(viewLifecycleOwner) { response ->
            val roomList = response?.result?.result ?: emptyList()
            if (roomList.isNotEmpty()) {
                binding.clComponent.visibility = View.VISIBLE
                binding.tvTitle.visibility = View.VISIBLE
                binding.divider.visibility = View.VISIBLE
                binding.tvMyNickname.visibility = View.VISIBLE
                val nickname = viewModel.getNickname().toString()
                binding.tvMyNickname.text = "${nickname}님이"
                binding.tvEmptyRoom.visibility = View.GONE
                binding.rvMySent.visibility = View.VISIBLE
                adapter.submitList(roomList)
            } else {
                binding.clComponent.visibility = View.GONE
                binding.tvEmptyRoom.visibility = View.GONE
                binding.rvMySent.visibility = View.GONE
            }
        }
        // 로딩중 옵저빙
        viewModel.isLoading1.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading == true || isLoading == null) {
                binding.clComponent.visibility = View.GONE
            } else {
                binding.clComponent.visibility = View.VISIBLE
            }
        }
    }

    fun refreshData() {
        val nickname = viewModel.getNickname().toString()
        binding.tvMyNickname.text = "${nickname}님이"
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getRequestedRoomList()
        }
    }
}