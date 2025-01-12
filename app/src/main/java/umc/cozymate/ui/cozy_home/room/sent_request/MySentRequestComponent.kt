package umc.cozymate.ui.cozy_home.room.sent_request

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
import umc.cozymate.databinding.FragmentMySentRequestComponentBinding
import umc.cozymate.ui.cozy_home.room.room_detail.CozyRoomDetailInfoActivity
import umc.cozymate.ui.viewmodel.RoomRequestViewModel

@AndroidEntryPoint
class MySentRequestComponent : Fragment() {
    private var _binding: FragmentMySentRequestComponentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RoomRequestViewModel by viewModels()
    companion object {
        fun newInstance() = MySentRequestComponent()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        Main: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMySentRequestComponentBinding.inflate(inflater, Main, false)
        observeRoomList()
        val nickname = viewModel.getNickname().toString()
        binding.tvMyNickname.text = "${nickname}님이"
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getRequestedRoomList()
        }
        return binding.root
    }

    private fun observeRoomList() {
        // 클릭 시 방 상세정보 페이지로 이동하도록 어댑터 설정
        val adapter = SentRequestAdapter { roomId ->
            val intent = Intent(requireActivity(), CozyRoomDetailInfoActivity::class.java).apply {
                putExtra(CozyRoomDetailInfoActivity.ARG_ROOM_ID, roomId)
            }
            startActivity(intent)
        }
        binding.rvMySent.adapter = adapter
        binding.rvMySent.layoutManager = LinearLayoutManager(requireContext())
        // 참여요청한 방 목록 api 응답 옵저빙
        viewModel.RequestedRoomResponse.observe(viewLifecycleOwner) { response ->
            val roomList = response?.result ?: emptyList()
            if (roomList.isNotEmpty()) {
                binding.tvEmptyRoom.visibility = View.GONE
                binding.rvMySent.visibility = View.VISIBLE
                adapter.submitList(roomList)
            } else {
                binding.clComponent.visibility = View.GONE
                binding.tvEmptyRoom.visibility = View.VISIBLE
                binding.rvMySent.visibility = View.GONE
            }
        }
        // 로딩중 옵저빙
        viewModel.isLoading1.observe(viewLifecycleOwner) { isLoading ->
            //binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }
}