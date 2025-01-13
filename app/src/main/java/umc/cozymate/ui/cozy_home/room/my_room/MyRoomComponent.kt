package umc.cozymate.ui.cozy_home.room.my_room

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import umc.cozymate.databinding.FragmentMyRoomComponentBinding
import umc.cozymate.ui.cozy_home.room_detail.RoomDetailActivity
import umc.cozymate.ui.viewmodel.CozyHomeViewModel

@AndroidEntryPoint
class MyRoomComponent : Fragment() {
    private var _binding: FragmentMyRoomComponentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CozyHomeViewModel by viewModels()
    private var roomId: Int? = 0
    companion object {
        fun newInstance() = MyRoomComponent()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        Main: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyRoomComponentBinding.inflate(inflater, Main, false)
        viewLifecycleOwner.lifecycleScope.launch {
            initMyRoomData()
        }
        setListener()
        return binding.root
    }

    private fun setListener() {
        with(binding) {
            clMyRoom.isEnabled = true
            clMyRoom.setOnClickListener {
                // roomId 값을 넘겨주면서 방 상세 화면으로 이동
                val intent = Intent(requireActivity(), RoomDetailActivity::class.java).apply {
                    putExtra(RoomDetailActivity.ARG_ROOM_ID, roomId)
                }
                startActivity(intent)
            }
        }
    }

    private suspend fun initMyRoomData() {
        val nickname = viewModel.getNickname().toString()
        binding.tvMyNickname.text = "${nickname}님이"
        viewModel.getRoomInfoById().observe(viewLifecycleOwner, Observer { roomInfo ->
            roomId = roomInfo?.roomId
            with(binding) {
                tvRoomName.text = roomInfo?.name
                tvCurMemberCount.text = roomInfo?.arrivalMateNum.toString() + "명"
                if (roomInfo?.equality == 0){
                    tvEquality.text = "??%"
                } else {
                    tvEquality.text = roomInfo?.equality.toString() + "%"
                }
                tvHashtag1.visibility = View.GONE
                tvHashtag1.text = ""
                tvHashtag2.visibility = View.GONE
                tvHashtag2.text = ""
                tvHashtag3.visibility = View.GONE
                tvHashtag3.text = ""
                when (roomInfo?.hashtagList?.size) {
                    null -> {
                        tvHashtag1.visibility = View.VISIBLE
                        tvHashtag1.text = "비공개방이에요"
                    }
                    0 -> {
                        tvHashtag1.visibility = View.VISIBLE
                        tvHashtag1.text = "비공개방이에요"
                    }
                    1 -> {
                        if (roomInfo.hashtagList[0] != "") {
                            tvHashtag1.visibility = View.VISIBLE
                            tvHashtag1.text = "#${roomInfo?.hashtagList?.get(0)}"
                        }
                    }
                    2 -> {
                        tvHashtag1.visibility = View.VISIBLE
                        tvHashtag1.text = "#${roomInfo?.hashtagList?.get(0)}"
                        tvHashtag2.visibility = View.VISIBLE
                        tvHashtag2.text = "#${roomInfo?.hashtagList?.get(1)}"
                    }
                    3 -> {
                        tvHashtag1.visibility = View.VISIBLE
                        tvHashtag1.text = "#${roomInfo?.hashtagList?.get(0)}"
                        tvHashtag2.visibility = View.VISIBLE
                        tvHashtag2.text = "#${roomInfo?.hashtagList?.get(1)}"
                        tvHashtag3.visibility = View.VISIBLE
                        tvHashtag3.text = "#${roomInfo?.hashtagList?.get(2)}"
                    }
                }
            }
        })
    }

    fun refreshData() {
        viewLifecycleOwner.lifecycleScope.launch {
            initMyRoomData()
        }
    }
}