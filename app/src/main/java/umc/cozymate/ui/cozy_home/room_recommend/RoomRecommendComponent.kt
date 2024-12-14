package umc.cozymate.ui.cozy_home.room_recommend

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import umc.cozymate.databinding.ComponentRoomRecommendBinding
import umc.cozymate.ui.cozy_home.room_detail.RoomDetailActivity

class RoomRecommendComponent : Fragment() {

    companion object {
        // 방 더보기 페이지로 이동
        fun startActivityFromFragment(fragment: Fragment, roomId: String) {
            val intent = Intent(fragment.requireContext(), RoomDetailActivity::class.java).apply {
                putExtra("ROOM_ID", "Sample room id")
            }
            fragment.startActivity(intent)
        }

        private const val EXTRA_DATA = "EXTRA_DATA"
    }

    private var _binding: ComponentRoomRecommendBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RoomRecommendViewModel by viewModels()
    private val rrData = listOf(
        RoomRecommendItem("방이름", "75%", "기상시간", "-", "-", "", arrayListOf(), 2, 3),
        RoomRecommendItem("방이룸", "73%", "기상시간", "-", "-", "", arrayListOf(), 2, 3),
        RoomRecommendItem("방이름", "73%", "기상시간", "-", "-", "", arrayListOf(), 2, 3),
        RoomRecommendItem("방이룸", "63%", "기상시간", "-", "-", "", arrayListOf(), 2, 3),
        RoomRecommendItem("방이름", "60%", "기상시간", "-", "-", "", arrayListOf(), 2, 3),
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ComponentRoomRecommendBinding.inflate(inflater, container, false)

        val dotsIndicator = binding.dotsIndicator
        val viewPager = binding.vpRoom
        val adapter = RoomRecommendVPAdapter(rrData)
        viewPager.adapter = adapter
        dotsIndicator.attachTo(viewPager)

        binding.llMore.setOnClickListener {
            startActivityFromFragment(this, "Sample Room Id")
        }

        return binding.root
    }
}