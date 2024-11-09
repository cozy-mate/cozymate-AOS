package umc.cozymate.ui.cozy_home.room_recommend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import umc.cozymate.databinding.ComponentRoomRecommendBinding
import umc.cozymate.ui.cozy_home.roommate_recommend.RoommateRecommendComponent

class RoomRecommendComponent : Fragment() {

    private var _binding: ComponentRoomRecommendBinding?= null
    private val binding get() = _binding!!

    private val viewModel: RoomRecommendViewModel by viewModels()
    private val rrData = listOf(
        RoomRecommendItem("방이룸", "75%", "기상시간", "-", "-", "", arrayListOf(), 2, 3),
        RoomRecommendItem("방이름", "73%", "기상시간", "-", "-", "", arrayListOf(), 2, 3),
        RoomRecommendItem("방이름름", "73%", "기상시간", "-", "-", "", arrayListOf(), 2, 3),
        RoomRecommendItem("방이름룸", "63%", "기상시간", "-", "-", "", arrayListOf(), 2, 3),
        RoomRecommendItem("방이룸룸", "60%", "기상시간", "-", "-", "", arrayListOf(), 2, 3),
    )
    companion object {
        fun newInstance() = RoommateRecommendComponent()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

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

        return binding.root
    }
}