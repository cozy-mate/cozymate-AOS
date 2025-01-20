package umc.cozymate.ui.cozy_home.room.recommended_room

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import umc.cozymate.databinding.FragmentRecommendedRoomBinding
import umc.cozymate.ui.cozy_home.room.room_detail.CozyRoomDetailInfoActivity
import umc.cozymate.ui.cozy_home.room_detail.RoomDetailActivity
import umc.cozymate.ui.viewmodel.CozyHomeViewModel
import umc.cozymate.ui.viewmodel.RoommateRecommendViewModel

@AndroidEntryPoint
class RecommendedRoomComponent : Fragment() {
    private val TAG = this.javaClass.simpleName
    private var _binding: FragmentRecommendedRoomBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CozyHomeViewModel by viewModels()
    private val roommateViewModel: RoommateRecommendViewModel by viewModels()
    private var nickname: String = ""
    private var isLifestyleExist = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecommendedRoomBinding.inflate(inflater, container, false)
        getPreference()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        nickname = viewModel.getNickname().toString()
        binding.tvName.text = "${nickname}님과"
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.fetchRecommendedRoomList()
        }
        viewModel.roomList.observe(viewLifecycleOwner) { roomList ->
            if (roomList.isNullOrEmpty()) {
                binding.vpRoom.visibility = View.GONE
                binding.tvEmptyRoom.visibility = View.VISIBLE
            } else {
                val dotsIndicator = binding.dotsIndicator
                val viewPager = binding.vpRoom
                // 클릭 시 방 상세 페이지로 room id 넘겨줌
                val adapter = RecommendedRoomVPAdapter(roomList, isLifestyleExist) { roomId ->
                    val intent = Intent(requireContext(), RoomDetailActivity::class.java).apply {
                        putExtra(CozyRoomDetailInfoActivity.ARG_ROOM_ID, roomId)
                    }
                    startActivity(intent)
                }
                viewPager.adapter = adapter
                dotsIndicator.attachTo(viewPager)
            }
        }
        // 방 더보기 페이지로 이동
        binding.llMore.setOnClickListener {
            val intent = Intent(requireContext(), CozyRoomDetailInfoActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getPreference() {
        val spf = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        isLifestyleExist = spf.getBoolean("is_lifestyle_exist", false)
        Log.d(TAG, "라이프스타일 입력 여부: $isLifestyleExist")
    }

    fun refreshData() {
        getPreference()
        nickname = viewModel.getNickname().toString()
        binding.tvName.text = "${nickname}님과"
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.fetchRecommendedRoomList()
        }
    }
}