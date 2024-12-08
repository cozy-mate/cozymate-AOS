package umc.cozymate.ui.cozy_home.room.room_recommend

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.databinding.FragmentRoomRecommendComponentBinding
import umc.cozymate.ui.cozy_home.room.room_detail.RoomDetailActivity
import umc.cozymate.ui.viewmodel.CozyHomeViewModel

@AndroidEntryPoint
class RoomRecommendComponent : Fragment() {

    companion object {
        // 방 더보기 페이지로 이동
        fun startActivityFromFragment(fragment: Fragment, roomId: String) {
            val intent = Intent(fragment.requireContext(), RoomDetailActivity::class.java).apply {
                putExtra("ROOM_ID", "Sample room id")
            }
            fragment.startActivity(intent)
        }

        // private const val EXTRA_DATA = "EXTRA_DATA"
    }

    private val TAG = this.javaClass.simpleName
    private var _binding: FragmentRoomRecommendComponentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CozyHomeViewModel by viewModels()
    private var nickname: String = ""
    private var prefList: List<String> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRoomRecommendComponentBinding.inflate(inflater, container, false)

        getPreference()
        binding.tvName.text = "${nickname}님과"
        viewModel.fetchRecommendedRoomList()
        viewModel.roomList.observe(viewLifecycleOwner) { roomList ->
            val dotsIndicator = binding.dotsIndicator
            val viewPager = binding.vpRoom
            val adapter = RoomRecommendVPAdapter(roomList, prefList)
            viewPager.adapter = adapter
            dotsIndicator.attachTo(viewPager)
        }
        binding.llMore.setOnClickListener {
            startActivityFromFragment(this, "Sample Room Id")
        }

        return binding.root
    }

    // sharedpreference에서 데이터 받아오기
    private fun getPreference() {
        val spf = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        nickname = spf.getString("user_nickname", "No user found").toString()
        prefList = arrayListOf(
            spf.getString("pref_1", "No pref found").toString(),
            spf.getString("pref_2", "No pref found").toString(),
            spf.getString("pref_3", "No pref found").toString(),
            spf.getString("pref_4", "No pref found").toString(),
        )
        Log.d(TAG, "prefList: $prefList")
    }
}