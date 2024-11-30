package umc.cozymate.ui.cozy_home.room.roommate_recommend

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.databinding.FragmentRoommateRecommendComponentBinding
import umc.cozymate.ui.cozy_home.room.room_recommend.RoomRecommendComponent.Companion.startActivityFromFragment

@AndroidEntryPoint
class RoommateRecommendComponent : Fragment() {

    private var _binding: FragmentRoommateRecommendComponentBinding?= null
    private val binding get() = _binding!!

    private val viewModel: RoommateRecommendViewModel by viewModels()
    companion object {
        fun newInstance() = RoommateRecommendComponent
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRoommateRecommendComponentBinding.inflate(inflater, container, false)

        val spf = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val nickname =  spf.getString("user_nickname", "No user found").toString()
        val json = spf.getString("pref_list", null)
        val listType = object : TypeToken<List<String>>() {}.type
        try{val prefList: List<String> = Gson().fromJson(json, listType)
        binding.tvName.text = "${nickname}님과"
        viewModel.fetchRecommendedRoommateList()
        viewModel.fetchRoommateListByEquality()
        viewModel.roommateList.observe(viewLifecycleOwner) { rmList ->
            val dotsIndicator = binding.dotsIndicator
            val viewPager = binding.vpRoommate
            val adapter = RoommateRecommendVPAdapter(rmList, prefList)
            viewPager.adapter = adapter
            dotsIndicator.attachTo(viewPager)
        }} catch (e: Exception){
            Log.e("Roommate", "ㅠㅠ")
        }
        binding.llMore.setOnClickListener {
            startActivityFromFragment(this, "Sample Room Id")
        }

        return binding.root
    }
}