package umc.cozymate.ui.cozy_home.room.my_room

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.databinding.FragmentMyRoomComponentBinding
import umc.cozymate.ui.cozy_home.room_detail.CozyRoomDetailInfoActivity
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
        roomId = viewModel.fetchRoomIdIfNeeded()
        initMyRoomData()
        setListener()
        return binding.root
    }

    private fun setListener() {
        with(binding) {
            clMyRoom.isEnabled = true
            clMyRoom.setOnClickListener {
                // roomId 값을 넘겨주면서 방 상세 화면으로 이동
                val intent = Intent(requireContext(), CozyRoomDetailInfoActivity::class.java)
                intent.putExtra("room_id", roomId)
                startActivity(intent)
            }
        }
    }

    private fun initMyRoomData() {
        if (roomId == null || roomId == 0) {
            roomId = viewModel.getSavedRoomId()
        }
        val nickname = viewModel.getNickname().toString()
        viewModel.getRoomInfoById(roomId!!).observe(viewLifecycleOwner, Observer { roomInfo ->
            with(binding) {
                tvMyNickname.text = nickname + "님이"
                tvRoomName.text = roomInfo?.name
                tvCurMemberCount.text = roomInfo?.arrivalMateNum.toString() + "명"
                tvEquality.text = roomInfo?.equality.toString() + "%"

                when (roomInfo?.hashtagList?.size) {
                    0 -> {
                        tvHashtag1.visibility = View.GONE
                        tvHashtag2.visibility = View.GONE
                        tvHashtag3.visibility = View.GONE
                    }

                    1 -> {
                        tvHashtag1.text = "#${roomInfo?.hashtagList?.get(0)}"
                        tvHashtag2.visibility = View.GONE
                        tvHashtag3.visibility = View.GONE
                    }

                    2 -> {
                        tvHashtag1.text = "#${roomInfo?.hashtagList?.get(0)}"
                        tvHashtag2.text = "#${roomInfo?.hashtagList?.get(1)}"
                        tvHashtag3.visibility = View.GONE
                    }

                    3 -> {
                        tvHashtag1.text = "#${roomInfo?.hashtagList?.get(0)}"
                        tvHashtag2.text = "#${roomInfo?.hashtagList?.get(1)}"
                        tvHashtag3.text = "#${roomInfo?.hashtagList?.get(2)}"
                    }
                }
            }
        })
    }
}