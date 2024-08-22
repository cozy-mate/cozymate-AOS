package umc.cozymate.ui.roommate

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.R
import umc.cozymate.databinding.FragmentRoommateMakeCrewableBinding
import umc.cozymate.ui.roommate.adapter.RoommateMakeCrewableInfoListRVA
import umc.cozymate.ui.roommate.adapter.RoommateMakeCrewableInfoTableRVA
import umc.cozymate.ui.viewmodel.RoommateViewModel
import umc.cozymate.ui.viewmodel.TodoViewModel

//@AndroidEntryPoint
//class RoommateMakeCrewableFragment : Fragment() {
//    private var _binding: FragmentRoommateMakeCrewableBinding? = null
//    private val binding get() = _binding!!
//    private val viewModel: RoommateViewModel by viewModels()
//    private lateinit var roommateMakeCrewableInfoListRVA: RoommateMakeCrewableInfoListRVA
//    private lateinit var roommateMakeCrewableInfoTableRVA: RoommateMakeCrewableInfoTableRVA
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentRoommateMakeCrewableBinding.inflate(inflater, container, false)
//
//        // 필터가 적용된 리스트 (rvCrewableResultList)
//        roommateMakeCrewableInfoListRVA = RoommateMakeCrewableInfoListRVA(listOf()) { selectDetail ->
//            val intent = Intent(requireActivity(), RoommateDetailActivity::class.java)
//            intent.putExtra("selectDetail", selectDetail)
//            startActivity(intent)
//        }
//        binding.rvCrewableResultList.layoutManager = LinearLayoutManager(requireContext())
//        binding.rvCrewableResultTable.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
//
//        binding.rvCrewableResultList.adapter = roommateMakeCrewableInfoListRVA
//
//        // 필터가 없는 리스트 (rvCrewableResultTable)
//        roommateMakeCrewableInfoTableRVA = RoommateMakeCrewableInfoTableRVA(listOf()) { selectDetail ->
//            val intent = Intent(requireActivity(), RoommateDetailActivity::class.java)
//            intent.putExtra("selectDetail", selectDetail)
//            startActivity(intent)
//        }
//        binding.rvCrewableResultTable.adapter = roommateMakeCrewableInfoTableRVA
//
//        initTextView()
//
//        // 필터가 없는 상태의 데이터를 Table에 적용
//        lifecycleScope.launchWhenStarted {
//            viewModel.unfilteredUserInfo.collect { userInfoList ->
//                roommateMakeCrewableInfoTableRVA.updateData(userInfoList)
//            }
//        }
//
//        // 필터가 적용된 상태의 데이터를 List에 적용
//        lifecycleScope.launchWhenStarted {
//            viewModel.otherUserInfo.collect { userInfoList ->
//                roommateMakeCrewableInfoListRVA.updateData(userInfoList)
//            }
//        }
//
//        // 필터 리스트를 관찰하여 UI 처리
//        viewModel.filterList.observe(viewLifecycleOwner) { filterList ->
//            if (filterList.isNullOrEmpty()) {
//                // 필터가 없으면 안내 메시지 표시
//                showEmptyState()
//            } else {
//                // 필터가 있으면 RecyclerView를 표시
//                binding.rvCrewableResultList.visibility = View.VISIBLE
//                binding.tvNoSelection.visibility = View.GONE // 텍스트 숨김
//                viewModel.getOtherUserInfo(getAccessToken(), 0) // 필터가 적용된 데이터를 가져옴
//            }
//        }
//
//        return binding.root
//    }
//
//    // 필터가 없을 때 보여줄 UI
//    private fun showEmptyState() {
//        binding.rvCrewableResultList.visibility = View.GONE
//        binding.tvNoSelection.visibility = View.VISIBLE
//        binding.tvNoSelection.text = "칩을 선택해보세요"
//    }
//
//    fun initTextView(){
//        setupTextSelection(binding.selectBirth, "birthYear")
//        setupTextSelection(binding.selectNumber, "admissionYear")
//        setupTextSelection(binding.selectMajor, "major")
//        setupTextSelection(binding.selectRoomNum, "numOfRoommate")
//        setupTextSelection(binding.selectAcceptance, "acceptance")
//        setupTextSelection(binding.selectWake, "wakeUpTime")
//        setupTextSelection(binding.selectSleep, "sleepingTime")
//        setupTextSelection(binding.selectLigtOff, "turnOffTime")
//        setupTextSelection(binding.selectSmoke, "smoking")
//        setupTextSelection(binding.selectSleepHabit, "sleepingHabit")
//        setupTextSelection(binding.selectAc, "airConditioningIntensity")
//        setupTextSelection(binding.selectHeater, "heatingIntensity")
//        setupTextSelection(binding.selectLivingPattern, "lifePattern")
//        setupTextSelection(binding.selectFriendly, "intimacy")
//        setupTextSelection(binding.selectShare, "canShare")
//        setupTextSelection(binding.selectStudy, "studying")
//        setupTextSelection(binding.selectIntake, "intake")
//        setupTextSelection(binding.selectGame, "isPlayGame")
//        setupTextSelection(binding.selectCall, "isPhoneCall")
//        setupTextSelection(binding.selectClean, "cleanSensitivity")
//        setupTextSelection(binding.selectNoise, "noiseSensitivity")
//        setupTextSelection(binding.selectCleanFrequency, "cleaningFrequency")
//        setupTextSelection(binding.selectPersonality, "personality")
//        setupTextSelection(binding.selectMbti, "mbti")
//    }
//
//    private fun setupTextSelection(textView: TextView, filterText: String) {
//        textView.setOnClickListener {
//            if (textView.isSelected) {
//                textView.isSelected = false
//                textView.apply {
//                    setTextColor(resources.getColor(R.color.unuse_font, null))
//                    background = resources.getDrawable(R.drawable.custom_select_chip_background)
//                }
//                viewModel.removeFilter(filterText)
//            } else {
//                textView.isSelected = true
//                textView.apply {
//                    setTextColor(resources.getColor(R.color.main_blue, null))
//                    background = resources.getDrawable(R.drawable.custom_select_chip_selected, null)
//                }
//                viewModel.addFilter(filterText)
//            }
//        }
//    }
//
//    private fun getAccessToken(): String {
//        val _accessToken = getString(R.string.access_token_1)
//        return "Bearer $_accessToken"
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}
@AndroidEntryPoint
class RoommateMakeCrewableFragment : Fragment() {
    private var _binding: FragmentRoommateMakeCrewableBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RoommateViewModel by viewModels()
    private val todoViewModel: TodoViewModel by viewModels()
    private lateinit var roommateMakeCrewableInfoListRVA: RoommateMakeCrewableInfoListRVA
    private lateinit var roommateMakeCrewableInfoTableRVA: RoommateMakeCrewableInfoTableRVA

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRoommateMakeCrewableBinding.inflate(inflater, container, false)

        // 필터가 적용된 리스트 (rvCrewableResultList)
        roommateMakeCrewableInfoListRVA = RoommateMakeCrewableInfoListRVA(listOf()) { selectDetail ->
            val intent = Intent(requireActivity(), RoommateDetailActivity::class.java)
            intent.putExtra("selectDetail", selectDetail)
            startActivity(intent)
        }
        binding.rvCrewableResultList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCrewableResultTable.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        binding.rvCrewableResultList.adapter = roommateMakeCrewableInfoListRVA

        // 필터가 없는 리스트 (rvCrewableResultTable)
        roommateMakeCrewableInfoTableRVA = RoommateMakeCrewableInfoTableRVA(listOf()) { selectDetail ->
            val intent = Intent(requireActivity(), RoommateDetailActivity::class.java)
            intent.putExtra("selectDetail", selectDetail)
            startActivity(intent)
        }
        binding.rvCrewableResultTable.adapter = roommateMakeCrewableInfoTableRVA

        initTextView()

        // 필터가 없는 상태의 데이터를 Table에 적용
        lifecycleScope.launchWhenStarted {
            viewModel.unfilteredUserInfo.collect { userInfoList ->
                roommateMakeCrewableInfoTableRVA.updateData(userInfoList)
            }
        }

        // 필터가 적용된 상태의 데이터를 List에 적용
        lifecycleScope.launchWhenStarted {
            viewModel.otherUserInfo.collect { userInfoList ->
                if (userInfoList.isEmpty()) {
                    showNoResultMessage()
                } else {
                    roommateMakeCrewableInfoListRVA.updateData(userInfoList)
                    binding.rvCrewableResultList.visibility = View.VISIBLE
                    binding.tvNoSelection.visibility = View.GONE // 숨김 처리
                }
            }
        }

        // 액세스 토큰을 가져와 API 호출 (필터 적용되지 않은 데이터 먼저 가져오기)
//        val _accessToken = getString(R.string.access_token_1)
        val spf = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val accessToken = spf.getString("access_token", "")
        viewModel.getInitialUserInfo(accessToken!!, 0) // 필터가 없는 데이터 가져오기

        // 필터 리스트를 관찰하여 UI 처리
        viewModel.filterList.observe(viewLifecycleOwner) { filterList ->
            if (filterList.isNullOrEmpty()) {
                showEmptyState()
            } else {
                binding.rvCrewableResultList.visibility = View.VISIBLE
                binding.tvNoSelection.visibility = View.GONE
                viewModel.getOtherUserInfo(accessToken!!, 0) // 필터가 적용된 데이터를 가져옴
            }
        }

        return binding.root
    }

    // 필터가 없을 때 보여줄 UI
    private fun showEmptyState() {
        binding.rvCrewableResultList.visibility = View.GONE
        binding.tvNoSelection.visibility = View.VISIBLE
        binding.tvNoSelection.text = "칩을 선택해보세요"
    }

    // 필터 결과가 없을 때 보여줄 UI
    private fun showNoResultMessage() {
        binding.rvCrewableResultList.visibility = View.GONE
        binding.tvNoSelection.visibility = View.VISIBLE
        binding.tvNoSelection.text = "해당 칩에 같은 답변을 한 사용자가 없어요"
    }

    fun initTextView() {
        setupTextSelection(binding.selectBirth, "birthYear")
        setupTextSelection(binding.selectNumber, "admissionYear")
        setupTextSelection(binding.selectMajor, "major")
        setupTextSelection(binding.selectRoomNum, "numOfRoommate")
        setupTextSelection(binding.selectAcceptance, "acceptance")
        setupTextSelection(binding.selectWake, "wakeUpTime")
        setupTextSelection(binding.selectSleep, "sleepingTime")
        setupTextSelection(binding.selectLigtOff, "turnOffTime")
        setupTextSelection(binding.selectSmoke, "smoking")
        setupTextSelection(binding.selectSleepHabit, "sleepingHabit")
        setupTextSelection(binding.selectAc, "airConditioningIntensity")
        setupTextSelection(binding.selectHeater, "heatingIntensity")
        setupTextSelection(binding.selectLivingPattern, "lifePattern")
        setupTextSelection(binding.selectFriendly, "intimacy")
        setupTextSelection(binding.selectShare, "canShare")
        setupTextSelection(binding.selectStudy, "studying")
        setupTextSelection(binding.selectIntake, "intake")
        setupTextSelection(binding.selectGame, "isPlayGame")
        setupTextSelection(binding.selectCall, "isPhoneCall")
        setupTextSelection(binding.selectClean, "cleanSensitivity")
        setupTextSelection(binding.selectNoise, "noiseSensitivity")
        setupTextSelection(binding.selectCleanFrequency, "cleaningFrequency")
        setupTextSelection(binding.selectPersonality, "personality")
        setupTextSelection(binding.selectMbti, "mbti")
    }

    private fun setupTextSelection(textView: TextView, filterText: String) {
        textView.setOnClickListener {
            if (textView.isSelected) {
                textView.isSelected = false
                textView.apply {
                    setTextColor(resources.getColor(R.color.unuse_font, null))
                    background = resources.getDrawable(R.drawable.custom_select_chip_background)
                }
                viewModel.removeFilter(filterText)
            } else {
                textView.isSelected = true
                textView.apply {
                    setTextColor(resources.getColor(R.color.main_blue, null))
                    background = resources.getDrawable(R.drawable.custom_select_chip_selected, null)
                }
                viewModel.addFilter(filterText)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
