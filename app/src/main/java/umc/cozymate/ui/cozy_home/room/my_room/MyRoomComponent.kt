package umc.cozymate.ui.cozy_home.room.my_room

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.databinding.FragmentMyRoomComponentBinding
import umc.cozymate.ui.viewmodel.CozyHomeViewModel

@AndroidEntryPoint
class MyRoomComponent : Fragment() {
    private var _binding: FragmentMyRoomComponentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CozyHomeViewModel by viewModels()
    companion object {
        fun newInstance() = MyRoomComponent()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        Main: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyRoomComponentBinding.inflate(inflater, Main, false)
        viewModel.fetchRoomIdIfNeeded()
        initData()
        return binding.root
    }

    private fun initData() {
        viewModel.fetchRoomIdIfNeeded()
        val roomId = viewModel.getSavedRoomId()
        val spf = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val nickname =  spf.getString("user_nickname", "No user found").toString()
        viewModel.getRoomInfoById(roomId).observe(viewLifecycleOwner, Observer { roomInfo ->
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