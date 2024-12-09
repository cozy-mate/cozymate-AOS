package umc.cozymate.ui.cozy_home.room.roommate_recommend

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.databinding.FragmentRoommateRecommendComponentBinding
import umc.cozymate.ui.cozy_home.room.room_recommend.RoomRecommendComponent.Companion.startActivityFromFragment

@AndroidEntryPoint
class RoommateRecommendComponent : Fragment() {

    private val TAG = this.javaClass.simpleName
    private var _binding: FragmentRoommateRecommendComponentBinding?= null
    private val binding get() = _binding!!
    private val viewModel: RoommateRecommendViewModel by viewModels()
    private var nickname: String = ""
    private var prefList: List<String> = mutableListOf()
    companion object {
        fun newInstance() = RoommateRecommendComponent
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRoommateRecommendComponentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getPreference()
        binding.tvName.text = "${nickname}님과"
        viewModel.fetchRecommendedRoommateList()
        viewModel.fetchRoommateListByEquality()
        viewModel.roommateList.observe(viewLifecycleOwner) { rmList ->
            val dotsIndicator = binding.dotsIndicator
            val viewPager = binding.vpRoommate
            val adapter = RoommateRecommendVPAdapter(rmList, prefList)
            viewPager.adapter = adapter
            dotsIndicator.attachTo(viewPager)
        }
        binding.llMore.setOnClickListener {
            startActivityFromFragment(this, "Sample Room Id")
        }
    }

    // sharedpreference에서 데이터 받아오기
    private fun getPreference() {
        val spf = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        nickname = spf.getString("user_nickname", "").toString()
        prefList = arrayListOf(
            spf.getString("pref_1", "").toString(),
            spf.getString("pref_2", "").toString(),
            spf.getString("pref_3", "").toString(),
            spf.getString("pref_4", "").toString(),
        )
        Log.d(TAG, "prefList: $prefList")
    }
}