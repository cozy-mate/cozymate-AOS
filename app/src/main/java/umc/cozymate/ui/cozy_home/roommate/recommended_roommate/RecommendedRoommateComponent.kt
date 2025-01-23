package umc.cozymate.ui.cozy_home.roommate.recommended_roommate

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import umc.cozymate.data.model.entity.RecommendedMemberInfo
import umc.cozymate.databinding.FragmentRecommendRoommateBinding
import umc.cozymate.ui.cozy_home.roommate.roommate_detail.CozyHomeRoommateDetailActivity
import umc.cozymate.ui.cozy_home.roommate.roommate_detail.RoommateDetailActivity
import umc.cozymate.ui.viewmodel.RoommateDetailViewModel
import umc.cozymate.ui.viewmodel.RoommateRecommendViewModel

@AndroidEntryPoint
class RecommendedRoommateComponent : Fragment() {
    private val TAG = this.javaClass.simpleName
    private var _binding: FragmentRecommendRoommateBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RoommateRecommendViewModel by viewModels()
    private val detailViewModel : RoommateDetailViewModel by viewModels()
    private var nickname: String = ""
    private var prefList: List<String> = mutableListOf()
    private var isLifestyleExist : Boolean = false
    private var memberList : List<RecommendedMemberInfo> = emptyList()
    companion object {
        fun newInstance() = RecommendedRoommateComponent
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecommendRoommateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getPreference()
        binding.tvName.text = "${nickname}님과"
        updateData()

        // 추천 룸메이트 옵저빙

        viewModel.roommateList.observe(viewLifecycleOwner) { rmList ->
            if (rmList.isNullOrEmpty()) {
                binding.vpRoommate.visibility = View.GONE
                binding.dotsIndicator.visibility = View.GONE
                binding.tvEmptyRoommate.visibility = View.VISIBLE
            } else {
                memberList = rmList
                binding.vpRoommate.visibility = View.VISIBLE
                binding.dotsIndicator.visibility = View.VISIBLE
                binding.tvEmptyRoommate.visibility = View.GONE
                // 룸메이트 추천 뷰페이저 어댑터 설정
                val adapter = RecommendedRoommateVPAdapter(rmList){ memberId ->
                    navigatorToRoommateDetail(memberId)
                }
                binding.vpRoommate.adapter = adapter
                binding.dotsIndicator.attachTo(binding.vpRoommate)
            }
        }

        detailViewModel.otherUserDetailInfo.observe(viewLifecycleOwner) {otherUserDetail ->
            if(otherUserDetail == null) return@observe
            else{
                val intent = Intent(requireActivity(), RoommateDetailActivity::class.java)
                intent.putExtra("other_user_detail", otherUserDetail)
                startActivity(intent)
            }
        }

        // 룸메이트 더보기
        binding.llMore.setOnClickListener {
            val intent = Intent(requireContext(), CozyHomeRoommateDetailActivity::class.java)
            startActivity(intent)
        }
    }

    private fun updateData() {
        if(isLifestyleExist)  viewModel.fetchRoommateListByEquality()
        else viewModel.fetchRecommendedRoommateList()
    }


    private fun navigatorToRoommateDetail(memberId: Int) {
        detailViewModel.getOtherUserDetailInfo(memberId)
    }

    // sharedpreference에서 데이터 받아오기
    private fun getPreference() {
        val spf = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        nickname = spf.getString("user_nickname", "").toString()
        isLifestyleExist = spf.getBoolean("is_lifestyle_exist", false)
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
        updateData()
    }

}