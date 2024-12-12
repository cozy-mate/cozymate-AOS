//package umc.cozymate.ui.cozy_home.roommate_recommend
//
//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.viewModels
//import androidx.lifecycle.lifecycleScope
//import dagger.hilt.android.AndroidEntryPoint
//import kotlinx.coroutines.flow.collectLatest
//import kotlinx.coroutines.launch
//import umc.cozymate.R
//import umc.cozymate.databinding.ComponentRoommateRecommendBinding
//import umc.cozymate.ui.cozy_home.roommate_detail.CozyHomeRoommateDetailActivity
//import umc.cozymate.ui.viewmodel.RoommateViewModel
//
//@AndroidEntryPoint
//class RoommateRecommendComponent : Fragment() {
//
//    private var _binding: ComponentRoommateRecommendBinding? = null
//    private val binding get() = _binding!!
//
//    private val RecommendViewModel: RoommateRecommendViewModel by viewModels()
//    private val viewModel: RoommateViewModel by viewModels()
//
//    private val rrData = listOf(
//        RoommateRecommendItem("델로", "75%", "기상시간", "-", "-", ""),
//        RoommateRecommendItem("델로", "75%", "기상시간", "-", "-", ""),
//        RoommateRecommendItem("델로", "75%", "기상시간", "-", "-", ""),
//        RoommateRecommendItem("델로", "75%", "기상시간", "-", "-", ""),
//        RoommateRecommendItem("델로", "75%", "기상시간", "-", "-", ""),
//    )
////    companion object {
////        fun newInstance() = RoommateRecommendComponent
////    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        loadRandomMemberData()
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = ComponentRoommateRecommendBinding.inflate(inflater, container, false)
//
//        val dotsIndicator = binding.dotsIndicator
//        val viewPager = binding.vpRoommate
//        val adapter = RoommateRecommendVPAdapter(mutableListOf())
//        viewPager.adapter = adapter
//        dotsIndicator.attachTo(viewPager)
//
//        updateUsersInfo(adapter)
//
//        moveActivity()
//
//        return binding.root
//    }
//
//    private fun loadRandomMemberData() {
//        viewModel.getRandomMember()
//        viewModel.getFirstPageUsersInfo()
//    }
//
//    private fun updateUsersInfo(adapter: RoommateRecommendVPAdapter) {
//        viewLifecycleOwner.lifecycleScope.launch {
//            try {
//                viewModel.randomMemberList.collectLatest { randomMembers ->
//                    val updatedData = randomMembers.map { member ->
//                        RoommateRecommendItem(
//                            member.memberDetail.nickname,
//                            member.equality.toString(),
//                            member.preferenceStats.getOrNull(0)?.value ?: "-",
//                            member.preferenceStats.getOrNull(1)?.value ?: "-",
//                            member.preferenceStats.getOrNull(2)?.value ?: "-",
//                            member.preferenceStats.getOrNull(3)?.value ?: "-"
//                        )
//                    }
//                    adapter.updateData(updatedData)
//                    Log.d("RoommateRecommendComponent", "RandomMemberList")
//                }
//            } catch (e: Exception) {
//                // 오류 발생 시 getFirstPageUsersInfo 호출
//                viewModel.unfilteredUserInfo.collectLatest { unfilteredMembers ->
//                    val updatedData = unfilteredMembers.map { member ->
//                        RoommateRecommendItem(
//                            member.memberDetail.nickname,
//                            member.equality.toString(),
//                            member.preferenceStats.getOrNull(0)?.value ?: "-",
//                            member.preferenceStats.getOrNull(1)?.value ?: "-",
//                            member.preferenceStats.getOrNull(2)?.value ?: "-",
//                            member.preferenceStats.getOrNull(3)?.value ?: "-"
//                        )
//                    }
//                    adapter.updateData(updatedData)
//                    Log.d("RoommateRecommendComponent", "getFirstPage")
//                }
//            }
//        }
//    }
//
//    private fun moveActivity() {
//        binding.llMore.setOnClickListener {
//            val intent = Intent(requireContext(), CozyHomeRoommateDetailActivity::class.java)
//            startActivity(intent)
//            // 화면 전환 애니메이션
//            requireActivity().overridePendingTransition(R.anim.horizon_enter, R.anim.horizon_exit)
//        }
//    }
//}
package umc.cozymate.ui.cozy_home.roommate_recommend

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
import umc.cozymate.R
import umc.cozymate.databinding.ComponentRoommateRecommendBinding
import umc.cozymate.ui.cozy_home.roommate_detail.CozyHomeRoommateDetailActivity
import umc.cozymate.ui.viewmodel.RoommateViewModel

@AndroidEntryPoint
class RoommateRecommendComponent : Fragment() {

    private var _binding: ComponentRoommateRecommendBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RoommateViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadRandomMemberData()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ComponentRoommateRecommendBinding.inflate(inflater, container, false)

        binding.root.visibility = View.INVISIBLE

        val dotsIndicator = binding.dotsIndicator
        val viewPager = binding.vpRoommate
        val adapter = RoommateRecommendVPAdapter(mutableListOf()) // 초기 어댑터 설정
        viewPager.adapter = adapter
        dotsIndicator.attachTo(viewPager)

        updateUsersInfo(adapter)
        moveActivity()

        return binding.root
    }

    // 데이터 로드
    private fun loadRandomMemberData() {
        viewModel.getRandomMember()
        viewModel.getFirstPageUsersInfo()
    }

    // 사용자 정보 업데이트
    private fun updateUsersInfo(adapter: RoommateRecommendVPAdapter) {

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.randomMemberList.collectLatest { randomMembers ->
                if (randomMembers.isNotEmpty()) {
                    // RandomMemberList가 정상적으로 수집될 경우 데이터 업데이트
                    val updatedData = randomMembers.map { member ->
                        RoommateRecommendItem(
                            member.memberDetail.nickname,
                            member.equality.toString(),
                            member.preferenceStats.getOrNull(0)?.value ?: "-",
                            member.preferenceStats.getOrNull(1)?.value ?: "-",
                            member.preferenceStats.getOrNull(2)?.value ?: "-",
                            member.preferenceStats.getOrNull(3)?.value ?: "-"
                        )
                    }
                    adapter.updateData(updatedData)
                    Log.d("RoommateRecommendComponent", "RandomMemberList 업데이트 완료")
                    binding.root.visibility = View.VISIBLE
                } else {
                    // RandomMemberList에서 오류가 발생한 경우 unfilteredUserInfo 사용
                    collectUnfilteredUserInfo(adapter)
                }
            }
        }
    }

    // unfilteredUserInfo를 이용한 데이터 수집
    private fun collectUnfilteredUserInfo(adapter: RoommateRecommendVPAdapter) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.unfilteredUserInfo.collectLatest { unfilteredMembers ->
                if (unfilteredMembers.isNotEmpty()) {
                    val updatedData = unfilteredMembers.map { member ->
                        RoommateRecommendItem(
                            member.memberDetail.nickname,
                            member.equality.toString(),
                            member.preferenceStats.getOrNull(0)?.value ?: "-",
                            member.preferenceStats.getOrNull(1)?.value ?: "-",
                            member.preferenceStats.getOrNull(2)?.value ?: "-",
                            member.preferenceStats.getOrNull(3)?.value ?: "-"
                        )
                    }
                    adapter.updateData(updatedData)
                    Log.d("RoommateRecommendComponent", "UnfilteredUserInfo 업데이트 완료")
                    binding.root.visibility = View.VISIBLE
                } else {
                    Log.d("RoommateRecommendComponent", "UnfilteredUserInfo가 비어 있음")
                }
            }
        }
    }

    // 액티비티 이동
    private fun moveActivity() {
        binding.llMore.setOnClickListener {
            val intent = Intent(requireContext(), CozyHomeRoommateDetailActivity::class.java)
            startActivity(intent)
            requireActivity().overridePendingTransition(R.anim.horizon_enter, R.anim.horizon_exit)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
