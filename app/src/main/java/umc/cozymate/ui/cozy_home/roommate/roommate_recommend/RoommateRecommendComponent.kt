package umc.cozymate.ui.cozy_home.roommate.roommate_recommend

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
import umc.cozymate.databinding.FragmentRoommateRecommendComponentBinding
import umc.cozymate.ui.cozy_home.roommate.roommate_detail.CozyHomeRoommateDetailActivity
import umc.cozymate.ui.viewmodel.RoommateRecommendViewModel

@AndroidEntryPoint
class RoommateRecommendComponent : Fragment() {

    private val TAG = this.javaClass.simpleName
    private var _binding: FragmentRoommateRecommendComponentBinding? = null
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
        // 추천 룸메이트 옵저빙
        viewModel.roommateList.observe(viewLifecycleOwner) { rmList ->
            if (rmList.isNullOrEmpty()) {
                binding.vpRoommate.visibility = View.GONE
                binding.dotsIndicator.visibility = View.GONE
                binding.tvEmptyRoommate.visibility = View.VISIBLE
            } else {
                binding.vpRoommate.visibility = View.VISIBLE
                binding.dotsIndicator.visibility = View.VISIBLE
                binding.tvEmptyRoommate.visibility = View.GONE
                // 룸메이트 추천 뷰페이저 어댑터 설정
                val adapter = RoommateRecommendVPAdapter(rmList, prefList)
                binding.vpRoommate.adapter = adapter
                binding.dotsIndicator.attachTo(binding.vpRoommate)
            }
        }
        // 룸메이트 더보기
        binding.llMore.setOnClickListener {
            val intent = Intent(requireContext(), CozyHomeRoommateDetailActivity::class.java)
            startActivity(intent)
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
        Log.d(TAG, "nickname: $nickname")
    }

    fun refreshData() {
        getPreference()
        binding.tvName.text = "${nickname}님과"
        viewModel.fetchRecommendedRoommateList()
        viewModel.fetchRoommateListByEquality()
    }
}