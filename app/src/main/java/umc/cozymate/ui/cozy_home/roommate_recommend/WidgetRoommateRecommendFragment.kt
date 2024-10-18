package umc.cozymate.ui.cozy_home.roommate_recommend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import umc.cozymate.databinding.FragmentWidgetRoommateRecommendBinding

class WidgetRoommateRecommendFragment : Fragment() {

    private var _binding: FragmentWidgetRoommateRecommendBinding?= null
    private val binding get() = _binding!!

    private val viewModel: WidgetRoommateRecommendViewModel by viewModels()
    private val rrData = listOf(
        RoommateRecommendItem("델로", "75%", "-", "-", "-", ""),
        RoommateRecommendItem("델로", "75%", "-", "-", "-", ""),
        RoommateRecommendItem("델로", "75%", "-", "-", "-", ""),
        RoommateRecommendItem("델로", "75%", "-", "-", "-", ""),
        RoommateRecommendItem("델로", "75%", "-", "-", "-", ""),
    )
    companion object {
        fun newInstance() = WidgetRoommateRecommendFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWidgetRoommateRecommendBinding.inflate(inflater, container, false)

        val dotsIndicator = binding.dotsIndicator
        val viewPager = binding.vpRoommate
        val adapter = RoommateRecommendVPAdapter(rrData)
        viewPager.adapter = adapter
        dotsIndicator.attachTo(viewPager)

        return binding.root
    }
}