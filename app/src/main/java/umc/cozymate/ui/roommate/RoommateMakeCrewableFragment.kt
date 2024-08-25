package umc.cozymate.ui.roommate

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
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
//                if (userInfoList.isEmpty()) {
//                    showNoResultMessage()
//                } else {
//                    roommateMakeCrewableInfoListRVA.updateData(userInfoList)
//                    binding.rvCrewableResultList.visibility = View.VISIBLE
//                    binding.tvNoSelection.visibility = View.GONE // 숨김 처리
//                }
//            }
//        }
//
//        // 액세스 토큰을 가져와 API 호출 (필터 적용되지 않은 데이터 먼저 가져오기)
////        val _accessToken = getString(R.string.access_token_1)
//        val spf = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
//        val accessToken = spf.getString("access_token", "")
//        viewModel.getInitialUserInfo(accessToken!!, 0) // 필터가 없는 데이터 가져오기
//
//        // 필터 리스트를 관찰하여 UI 처리
//        viewModel.filterList.observe(viewLifecycleOwner) { filterList ->
//            if (filterList.isNullOrEmpty()) {
//                showEmptyState()
//            } else {
//                binding.rvCrewableResultList.visibility = View.VISIBLE
//                binding.tvNoSelection.visibility = View.GONE
//                viewModel.getOtherUserInfo(accessToken!!, 0) // 필터가 적용된 데이터를 가져옴
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
//    // 필터 결과가 없을 때 보여줄 UI
//    private fun showNoResultMessage() {
//        binding.rvCrewableResultList.visibility = View.GONE
//        binding.tvNoSelection.visibility = View.VISIBLE
//        binding.tvNoSelection.text = "해당 칩에 같은 답변을 한 사용자가 없어요"
//    }
//
//    fun initTextView() {
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
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}
//@AndroidEntryPoint
//class RoommateMakeCrewableFragment : Fragment() {
//    private var _binding: FragmentRoommateMakeCrewableBinding? = null
//    private val binding get() = _binding!!
//    private val viewModel: RoommateViewModel by viewModels()
//    private lateinit var roommateMakeCrewableInfoListRVA: RoommateMakeCrewableInfoListRVA
//    private lateinit var roommateMakeCrewableInfoTableRVA: RoommateMakeCrewableInfoTableRVA
//
//    private var isShowingAllListData = false // 필터 리스트 전체 보여주기 상태를 관리
//    private var isShowingAllTableData = false // 필터 없는 테이블 전체 보여주기 상태를 관리
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
//        binding.rvCrewableResultList.adapter = roommateMakeCrewableInfoListRVA
//
//        // 필터가 없는 리스트 (rvCrewableResultTable)
//        roommateMakeCrewableInfoTableRVA = RoommateMakeCrewableInfoTableRVA(listOf()) { selectDetail ->
//            val intent = Intent(requireActivity(), RoommateDetailActivity::class.java)
//            intent.putExtra("selectDetail", selectDetail)
//            startActivity(intent)
//        }
//        binding.rvCrewableResultTable.layoutManager =
//            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
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
//                if (userInfoList.isEmpty()) {
//                    showNoResultMessage()
//                } else {
//                    roommateMakeCrewableInfoListRVA.updateData(userInfoList)
//                    binding.rvCrewableResultList.visibility = View.VISIBLE
//                    binding.tvNoSelection.visibility = View.GONE // 숨김 처리
//                }
//            }
//        }
//
//        // 액세스 토큰을 가져와 API 호출 (필터 적용되지 않은 데이터 먼저 가져오기)
//        val spf = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
//        val accessToken = spf.getString("access_token", "")
//        viewModel.getOtherUserInfo(accessToken!!, 0) // 필터가 없는 데이터 가져오기
//
//        // 필터 리스트를 관찰하여 UI 처리
//        viewModel.filterList.observe(viewLifecycleOwner) { filterList ->
//            if (filterList.isNullOrEmpty()) {
//                showEmptyState()
//                resetListMoreButton() // 필터가 없을 때 버튼 상태 초기화
//            } else {
//                binding.rvCrewableResultList.visibility = View.VISIBLE
//                binding.tvNoSelection.visibility = View.GONE
//                viewModel.getOtherUserInfo(accessToken, 0) // 필터가 적용된 데이터를 가져옴
//                enableListMoreButton() // 필터가 있을 때만 버튼 활성화
//            }
//        }
//
//        // 더보기 버튼 클릭 이벤트 처리
//        setupMoreButtons(accessToken)
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
//    // 필터 결과가 없을 때 보여줄 UI
//    private fun showNoResultMessage() {
//        binding.rvCrewableResultList.visibility = View.GONE
//        binding.tvNoSelection.visibility = View.VISIBLE
//        binding.tvNoSelection.text = "해당 칩에 같은 답변을 한 사용자가 없어요"
//    }
//
//    // 텍스트 선택 초기화
//    fun initTextView() {
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
//    // 더보기 버튼 처리 설정
//    private fun setupMoreButtons(accessToken: String) {
//        // 필터 적용된 리스트의 더보기 버튼 처리
//        binding.btnCrewableChipListMore.setOnClickListener {
//            if (!binding.btnCrewableChipListMore.isEnabled) return@setOnClickListener // 필터 없으면 클릭 안됨
//
//            if (!isShowingAllListData) {
//                viewModel.getAllFilteredUserInfo(accessToken) // 모든 데이터를 가져옴
//                rotateButton(binding.btnCrewableChipListMore, -90f)
//            } else {
//                viewModel.getFilteredUserInfo(accessToken, 0) // 초기 데이터 5개만 가져옴
//                rotateButton(binding.btnCrewableChipListMore, 0f)
//            }
//            isShowingAllListData = !isShowingAllListData
//        }
//
//        // 필터 없는 테이블의 더보기 버튼 처리
//        binding.btnCrewableChipTableMore.setOnClickListener {
//            if (!isShowingAllTableData) {
//                viewModel.getAllOtherUserInfo(accessToken) // 필터 없는 모든 데이터를 가져옴
//                rotateButton(binding.btnCrewableChipTableMore, -90f)
//            } else {
//                viewModel.getOtherUserInfo(accessToken, 0) // 필터 없는 초기 5개만 가져옴
//                rotateButton(binding.btnCrewableChipTableMore, 0f)
//            }
//            isShowingAllTableData = !isShowingAllTableData
//        }
//    }
//
//    // 필터 리스트에 데이터가 없는 경우 btnCrewableChipListMore를 초기 상태로 되돌림
//    private fun resetListMoreButton() {
//        isShowingAllListData = false
//        rotateButton(binding.btnCrewableChipListMore, 0f)
//        binding.btnCrewableChipListMore.isEnabled = false // 버튼 비활성화
//    }
//
//    // 필터가 있을 때 btnCrewableChipListMore를 활성화
//    private fun enableListMoreButton() {
//        binding.btnCrewableChipListMore.isEnabled = true // 버튼 활성화
//    }
//
//    // 버튼 회전 애니메이션 설정
//    private fun rotateButton(button: ImageButton, angle: Float) {
//        button.animate().rotation(angle).setDuration(300).start()
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
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}
//
//@AndroidEntryPoint
//class RoommateMakeCrewableFragment : Fragment() {
//    private var _binding: FragmentRoommateMakeCrewableBinding? = null
//    private val binding get() = _binding!!
//    private val viewModel: RoommateViewModel by viewModels()
//    private lateinit var roommateMakeCrewableInfoListRVA: RoommateMakeCrewableInfoListRVA
//    private lateinit var roommateMakeCrewableInfoTableRVA: RoommateMakeCrewableInfoTableRVA
//
//    private var isShowingAllListData = false // 필터 리스트 전체 보여주기 상태를 관리
//    private var isShowingAllTableData = false // 필터 없는 테이블 전체 보여주기 상태를 관리
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
//        binding.rvCrewableResultList.adapter = roommateMakeCrewableInfoListRVA
//
//        // 필터가 없는 리스트 (rvCrewableResultTable)
//        roommateMakeCrewableInfoTableRVA = RoommateMakeCrewableInfoTableRVA(listOf()) { selectDetail ->
//            val intent = Intent(requireActivity(), RoommateDetailActivity::class.java)
//            intent.putExtra("selectDetail", selectDetail)
//            startActivity(intent)
//        }
//        binding.rvCrewableResultTable.layoutManager =
//            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
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
//                if (userInfoList.isEmpty()) {
//                    showNoResultMessage()
//                } else {
//                    roommateMakeCrewableInfoListRVA.updateData(userInfoList)
//                    binding.rvCrewableResultList.visibility = View.VISIBLE
//                    binding.tvNoSelection.visibility = View.GONE // 숨김 처리
//                }
//            }
//        }
//
//        // 액세스 토큰을 가져와 API 호출 (필터 적용되지 않은 데이터 먼저 가져오기)
//        val spf = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
//        val accessToken = spf.getString("access_token", "")
//        viewModel.getOtherUserInfo(accessToken!!, 0) // 필터가 없는 데이터 가져오기
//
//        // 필터 리스트를 관찰하여 UI 처리
//        viewModel.filterList.observe(viewLifecycleOwner) { filterList ->
//            if (filterList.isNullOrEmpty()) {
//                showEmptyState()
//                resetListMoreButton() // 필터가 없을 때 버튼 상태 초기화
//            } else {
//                binding.rvCrewableResultList.visibility = View.VISIBLE
//                binding.tvNoSelection.visibility = View.GONE
//                viewModel.getOtherUserInfo(accessToken, 0) // 필터가 적용된 데이터를 가져옴
//                enableListMoreButton() // 필터가 있을 때만 버튼 활성화
//            }
//        }
//
//        // 더보기 버튼 클릭 이벤트 처리
//        setupMoreButtons(accessToken)
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
//    // 필터 결과가 없을 때 보여줄 UI
//    private fun showNoResultMessage() {
//        binding.rvCrewableResultList.visibility = View.GONE
//        binding.tvNoSelection.visibility = View.VISIBLE
//        binding.tvNoSelection.text = "해당 칩에 같은 답변을 한 사용자가 없어요"
//    }
//
//    // 텍스트 선택 초기화
//    fun initTextView() {
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
//    // 더보기 버튼 처리 설정
//    private fun setupMoreButtons(accessToken: String) {
//        // 필터 적용된 리스트의 더보기 버튼 처리
//        binding.btnCrewableChipListMore.setOnClickListener {
//            Log.d("RoommateMakeCrewableFragment", "btnCrewableChipListMore Clicked")
//            if (!binding.btnCrewableChipListMore.isEnabled) return@setOnClickListener // 필터 없으면 클릭 안됨
//
//            if (!isShowingAllListData) {
//                viewModel.getAllFilteredUserInfo(accessToken) // 필터 적용된 모든 데이터를 가져옴
//                rotateButton(binding.btnCrewableChipListMore, -90f)
//            } else {
//                viewModel.getFilteredUserInfo(accessToken, 0) // 초기 데이터 5개만 가져옴
//                rotateButton(binding.btnCrewableChipListMore, 0f)
//            }
//            isShowingAllListData = !isShowingAllListData
//        }
//
//        // 필터 없는 테이블의 더보기 버튼 처리
//        binding.btnCrewableChipTableMore.setOnClickListener {
//            Log.d("RoommateMakeCrewableFragment", "btnCrewableChipTableMore Clicked")
//            if (!isShowingAllTableData) {
//                viewModel.getAllOtherUserInfo(accessToken) // 필터 없는 모든 데이터를 가져옴
//                // **Table에만 적용**
//                lifecycleScope.launchWhenStarted {
//                    viewModel.unfilteredUserInfo.collect { userInfoList ->
//                        roommateMakeCrewableInfoTableRVA.updateData(userInfoList) // Table에만 적용
//                    }
//                }
//                rotateButton(binding.btnCrewableChipTableMore, -90f)
//            } else {
//                viewModel.getOtherUserInfo(accessToken, 0) // 필터 없는 초기 5개만 가져옴
//                lifecycleScope.launchWhenStarted {
//                    viewModel.unfilteredUserInfo.collect { userInfoList ->
//                        roommateMakeCrewableInfoTableRVA.updateData(userInfoList) // Table에만 적용
//                    }
//                }
//                rotateButton(binding.btnCrewableChipTableMore, 0f)
//            }
//            isShowingAllTableData = !isShowingAllTableData
//        }
//    }
//
//    // 필터 리스트에 데이터가 없는 경우 btnCrewableChipListMore를 초기 상태로 되돌림
//    private fun resetListMoreButton() {
//        isShowingAllListData = false
//        rotateButton(binding.btnCrewableChipListMore, 0f)
//        binding.btnCrewableChipListMore.isEnabled = false // 버튼 비활성화
//    }
//
//    // 필터가 있을 때 btnCrewableChipListMore를 활성화
//    private fun enableListMoreButton() {
//        binding.btnCrewableChipListMore.isEnabled = true // 버튼 활성화
//    }
//
//    // 버튼 회전 애니메이션 설정
//    private fun rotateButton(button: ImageButton, angle: Float) {
//        button.animate().rotation(angle).setDuration(300).start()
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
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}
//@AndroidEntryPoint
//class RoommateMakeCrewableFragment : Fragment() {
//    private var _binding: FragmentRoommateMakeCrewableBinding? = null
//    private val binding get() = _binding!!
//    private val viewModel: RoommateViewModel by viewModels()
//    private lateinit var roommateMakeCrewableInfoListRVA: RoommateMakeCrewableInfoListRVA
//    private lateinit var roommateMakeCrewableInfoTableRVA: RoommateMakeCrewableInfoTableRVA
//
//    private var isShowingAllListData = false // 필터 리스트 전체 보여주기 상태를 관리
//    private var isShowingAllTableData = false // 필터 없는 테이블 전체 보여주기 상태를 관리
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentRoommateMakeCrewableBinding.inflate(inflater, container, false)
//
//        // 필터가 적용된 리스트 (rvCrewableResultList)
//        roommateMakeCrewableInfoListRVA =
//            RoommateMakeCrewableInfoListRVA(listOf()) { selectDetail ->
//                val intent = Intent(requireActivity(), RoommateDetailActivity::class.java)
//                intent.putExtra("selectDetail", selectDetail)
//                startActivity(intent)
//            }
//        binding.rvCrewableResultList.layoutManager = LinearLayoutManager(requireContext())
//        binding.rvCrewableResultList.adapter = roommateMakeCrewableInfoListRVA
//
//        // 필터가 없는 리스트 (rvCrewableResultTable)
//        roommateMakeCrewableInfoTableRVA =
//            RoommateMakeCrewableInfoTableRVA(listOf()) { selectDetail ->
//                val intent = Intent(requireActivity(), RoommateDetailActivity::class.java)
//                intent.putExtra("selectDetail", selectDetail)
//                startActivity(intent)
//            }
//        binding.rvCrewableResultTable.layoutManager =
//            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
//        binding.rvCrewableResultTable.adapter = roommateMakeCrewableInfoTableRVA
//
//        initTextView()
//
//        // 필터가 없는 상태의 데이터를 Table에 적용
//        lifecycleScope.launchWhenStarted {
//            viewModel.unfilteredUserInfo.collect { userInfoList ->
//                roommateMakeCrewableInfoTableRVA.updateData(userInfoList)  // Table에만 데이터를 적용
//            }
//        }
//
//        // 필터가 적용된 상태의 데이터를 List에 적용
//        lifecycleScope.launchWhenStarted {
//            viewModel.otherUserInfo.collect { userInfoList ->
//                if (userInfoList.isEmpty()) {
//                    showNoResultMessage()
//                } else {
//                    roommateMakeCrewableInfoListRVA.updateData(userInfoList)  // 필터가 적용된 리스트에 데이터 적용
//                    binding.rvCrewableResultList.visibility = View.VISIBLE
//                    binding.tvNoSelection.visibility = View.GONE // 숨김 처리
//                }
//            }
//        }
//
//        // 액세스 토큰을 가져와 API 호출 (필터 적용되지 않은 데이터 먼저 가져오기)
//        val spf = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
//        val accessToken = spf.getString("access_token", "")
//        viewModel.getOtherUserInfo(accessToken!!, 0) // 필터가 없는 데이터 가져오기
//
//        // 필터 리스트를 관찰하여 필터가 변경될 때마다 API 호출 및 UI 처리
//        viewModel.filterList.observe(viewLifecycleOwner) { filterList ->
//            if (filterList.isNullOrEmpty()) {
//                // 필터가 없을 때 빈 상태 UI 표시 및 리스트 숨기기
//                showEmptyState()
//                resetListMoreButton()
//
//                // 필터 해제 시 리스트의 데이터를 비우고 RecyclerView 자체를 숨김
//                roommateMakeCrewableInfoListRVA.updateData(emptyList()) // 데이터 비움
//                binding.rvCrewableResultList.visibility = View.GONE // RecyclerView 숨김
//            } else {
//                // 필터가 있을 때 RecyclerView를 다시 보여줌
//                binding.rvCrewableResultList.visibility = View.VISIBLE
//                binding.tvNoSelection.visibility = View.GONE
//                val accessToken =
//                    requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
//                        .getString("access_token", "")
//                accessToken?.let {
//                    viewModel.getFilteredUserInfo(it, 0) // 필터가 있을 때 첫 페이지 데이터 가져오기
//                }
//                enableListMoreButton() // 필터가 있을 때만 더보기 버튼 활성화
//            }
//        }
//
//
//        // 더보기 버튼 클릭 이벤트 처리
//        setupMoreButtons(accessToken)
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
//    // 필터 결과가 없을 때 보여줄 UI
//    private fun showNoResultMessage() {
//        binding.rvCrewableResultList.visibility = View.GONE
//        binding.tvNoSelection.visibility = View.VISIBLE
//        binding.tvNoSelection.text = "해당 칩에 같은 답변을 한 사용자가 없어요"
//    }
//
//    // 텍스트 선택 초기화
//    fun initTextView() {
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
//    // 더보기 버튼 처리 설정
//    private fun setupMoreButtons(accessToken: String) {
//        // 필터 적용된 리스트의 더보기 버튼 처리
//        binding.btnCrewableChipListMore.setOnClickListener {
//            Log.d("RoommateMakeCrewableFragment", "btnCrewableChipListMore Clicked")
//            if (!binding.btnCrewableChipListMore.isEnabled) return@setOnClickListener // 필터 없으면 클릭 안됨
//
//            if (!isShowingAllListData) {
//                viewModel.getAllFilteredUserInfo(accessToken) // 필터 적용된 모든 데이터를 가져옴
//                rotateButton(binding.btnCrewableChipListMore, -90f)
//            } else {
//                viewModel.getFilteredUserInfo(accessToken, 0) // 초기 데이터 5개만 가져옴
//                rotateButton(binding.btnCrewableChipListMore, 0f)
//            }
//            isShowingAllListData = !isShowingAllListData
//        }
//
//        // 필터 없는 테이블의 더보기 버튼 처리
//        binding.btnCrewableChipTableMore.setOnClickListener {
//            Log.d("RoommateMakeCrewableFragment", "btnCrewableChipTableMore Clicked")
//            if (!isShowingAllTableData) {
//                viewModel.getAllOtherUserInfo(accessToken) // 필터 없는 모든 데이터를 가져옴
//                lifecycleScope.launchWhenStarted {
//                    viewModel.unfilteredUserInfo.collect { userInfoList ->
//                        roommateMakeCrewableInfoTableRVA.updateData(userInfoList) // Table에만 적용
//                    }
//                }
//                rotateButton(binding.btnCrewableChipTableMore, -90f)
//            } else {
//                viewModel.getOtherUserInfo(accessToken, 0) // 필터 없는 초기 5개만 가져옴
//                lifecycleScope.launchWhenStarted {
//                    viewModel.unfilteredUserInfo.collect { userInfoList ->
//                        roommateMakeCrewableInfoTableRVA.updateData(userInfoList) // Table에만 적용
//                    }
//                }
//                rotateButton(binding.btnCrewableChipTableMore, 0f)
//            }
//            isShowingAllTableData = !isShowingAllTableData
//        }
//
//    }
//
//    // 필터 리스트에 데이터가 없는 경우 btnCrewableChipListMore를 초기 상태로 되돌림
//    private fun resetListMoreButton() {
//        isShowingAllListData = false
//        rotateButton(binding.btnCrewableChipListMore, 0f)
//        binding.btnCrewableChipListMore.isEnabled = false // 버튼 비활성화
//    }
//
//    // 필터가 있을 때 btnCrewableChipListMore를 활성화
//    private fun enableListMoreButton() {
//        binding.btnCrewableChipListMore.isEnabled = true // 버튼 활성화
//    }
//
//    // 버튼 회전 애니메이션 설정
//    private fun rotateButton(button: ImageButton, angle: Float) {
//        button.animate().rotation(angle).setDuration(300).start()
//    }
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
//
//            // 필터가 변경될 때마다 즉시 API를 호출하여 필터링된 데이터를 가져옴
//            val accessToken =
//                requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
//                    .getString("access_token", "")
//            accessToken?.let {
//                viewModel.getFilteredUserInfo(it, 0)  // 필터에 맞는 첫 페이지 데이터 호출
//            }
//        }
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
    private lateinit var roommateMakeCrewableInfoListRVA: RoommateMakeCrewableInfoListRVA
    private lateinit var roommateMakeCrewableInfoTableRVA: RoommateMakeCrewableInfoTableRVA

    private var isShowingAllListData = false // 필터 리스트 전체 보여주기 상태를 관리
    private var isShowingAllTableData = false // 필터 없는 테이블 전체 보여주기 상태를 관리

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRoommateMakeCrewableBinding.inflate(inflater, container, false)

        // 필터가 적용된 리스트 (rvCrewableResultList)
        roommateMakeCrewableInfoListRVA =
            RoommateMakeCrewableInfoListRVA(listOf()) { selectDetail ->
                val intent = Intent(requireActivity(), RoommateDetailActivity::class.java)
                intent.putExtra("selectDetail", selectDetail)
                startActivity(intent)
            }
        binding.rvCrewableResultList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCrewableResultList.adapter = roommateMakeCrewableInfoListRVA

        // 필터가 없는 리스트 (rvCrewableResultTable)
        roommateMakeCrewableInfoTableRVA =
            RoommateMakeCrewableInfoTableRVA(listOf()) { selectDetail ->
                val intent = Intent(requireActivity(), RoommateDetailActivity::class.java)
                intent.putExtra("selectDetail", selectDetail)
                startActivity(intent)
            }
        binding.rvCrewableResultTable.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvCrewableResultTable.adapter = roommateMakeCrewableInfoTableRVA

        initTextView()

        // 필터가 없는 상태의 데이터를 Table에 적용
        lifecycleScope.launchWhenStarted {
            viewModel.unfilteredUserInfo.collect { userInfoList ->
                roommateMakeCrewableInfoTableRVA.updateData(userInfoList)  // Table에만 데이터를 적용
                binding.tvTableNum.text = "${userInfoList.size} 명" // 테이블 데이터 개수 설정
            }
        }

        // 필터가 적용된 상태의 데이터를 List에 적용
        lifecycleScope.launchWhenStarted {
            viewModel.otherUserInfo.collect { userInfoList ->
                if (userInfoList.isEmpty()) {
                    showNoResultMessage()
                } else {
                    roommateMakeCrewableInfoListRVA.updateData(userInfoList)  // 필터가 적용된 리스트에 데이터 적용
                    binding.rvCrewableResultList.visibility = View.VISIBLE
                    binding.tvNoSelection.visibility = View.GONE // 숨김 처리
                }
                binding.tvListNum.text = "${userInfoList.size} 명" // 필터 적용된 리스트 데이터 개수 설정
            }
        }

        // 액세스 토큰을 가져와 API 호출 (필터 적용되지 않은 데이터 먼저 가져오기)
        val spf = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val accessToken = spf.getString("access_token", "")
        viewModel.getOtherUserInfo(accessToken!!, 0) // 필터가 없는 데이터 가져오기

        // 필터 리스트를 관찰하여 필터가 변경될 때마다 API 호출 및 UI 처리
        viewModel.filterList.observe(viewLifecycleOwner) { filterList ->
            if (filterList.isNullOrEmpty()) {
                // 필터가 없을 때 빈 상태 UI 표시 및 리스트 숨기기
                showEmptyState()
                resetListMoreButton()

                // 필터 해제 시 리스트의 데이터를 비우고 RecyclerView 자체를 숨김
                roommateMakeCrewableInfoListRVA.updateData(emptyList()) // 데이터 비움
                binding.rvCrewableResultList.visibility = View.GONE // RecyclerView 숨김
                binding.tvListNum.text = "리스트 데이터 수: 0" // 필터 해제 시 리스트 개수 0으로 설정
            } else {
                // 필터가 있을 때 RecyclerView를 다시 보여줌
                binding.rvCrewableResultList.visibility = View.VISIBLE
                binding.tvNoSelection.visibility = View.GONE
                val accessToken =
                    requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                        .getString("access_token", "")
                accessToken?.let {
                    viewModel.getFilteredUserInfo(it, 0) // 필터가 있을 때 첫 페이지 데이터 가져오기
                }
                enableListMoreButton() // 필터가 있을 때만 더보기 버튼 활성화
            }
        }

        // 더보기 버튼 클릭 이벤트 처리
        setupMoreButtons(accessToken)

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

    // 텍스트 선택 초기화
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

    // 더보기 버튼 처리 설정
    private fun setupMoreButtons(accessToken: String) {
        // 필터 적용된 리스트의 더보기 버튼 처리
        binding.btnCrewableChipListMore.setOnClickListener {
            Log.d("RoommateMakeCrewableFragment", "btnCrewableChipListMore Clicked")
            if (!binding.btnCrewableChipListMore.isEnabled) return@setOnClickListener // 필터 없으면 클릭 안됨

            if (!isShowingAllListData) {
                viewModel.getAllFilteredUserInfo(accessToken) // 필터 적용된 모든 데이터를 가져옴
                rotateButton(binding.btnCrewableChipListMore, -90f)
            } else {
                viewModel.getFilteredUserInfo(accessToken, 0) // 초기 데이터 5개만 가져옴
                rotateButton(binding.btnCrewableChipListMore, 0f)
            }
            isShowingAllListData = !isShowingAllListData
        }

        // 필터 없는 테이블의 더보기 버튼 처리
        binding.btnCrewableChipTableMore.setOnClickListener {
            Log.d("RoommateMakeCrewableFragment", "btnCrewableChipTableMore Clicked")
            if (!isShowingAllTableData) {
                viewModel.getAllOtherUserInfo(accessToken) // 필터 없는 모든 데이터를 가져옴
                lifecycleScope.launchWhenStarted {
                    viewModel.unfilteredUserInfo.collect { userInfoList ->
                        roommateMakeCrewableInfoTableRVA.updateData(userInfoList) // Table에만 적용
                        binding.tvTableNum.text = "${userInfoList.size} 명" // 테이블 데이터 개수 설정
                    }
                }
                rotateButton(binding.btnCrewableChipTableMore, -90f)
            } else {
                viewModel.getOtherUserInfo(accessToken, 0) // 필터 없는 초기 5개만 가져옴
                lifecycleScope.launchWhenStarted {
                    viewModel.unfilteredUserInfo.collect { userInfoList ->
                        roommateMakeCrewableInfoTableRVA.updateData(userInfoList) // Table에만 적용
                        binding.tvTableNum.text = "${userInfoList.size} 명" // 테이블 데이터 개수 설정
                    }
                }
                rotateButton(binding.btnCrewableChipTableMore, 0f)
            }
            isShowingAllTableData = !isShowingAllTableData
        }

    }

    // 필터 리스트에 데이터가 없는 경우 btnCrewableChipListMore를 초기 상태로 되돌림
    private fun resetListMoreButton() {
        isShowingAllListData = false
        rotateButton(binding.btnCrewableChipListMore, 0f)
        binding.btnCrewableChipListMore.isEnabled = false // 버튼 비활성화
        binding.tvListNum.text = "리스트 데이터 수: 0" // 필터가 없을 때 리스트 개수를 0으로 설정
    }

    // 필터가 있을 때 btnCrewableChipListMore를 활성화
    private fun enableListMoreButton() {
        binding.btnCrewableChipListMore.isEnabled = true // 버튼 활성화
    }

    // 버튼 회전 애니메이션 설정
    private fun rotateButton(button: ImageButton, angle: Float) {
        button.animate().rotation(angle).setDuration(300).start()
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

            // 필터가 변경될 때마다 즉시 API를 호출하여 필터링된 데이터를 가져옴
            val accessToken =
                requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    .getString("access_token", "")
            accessToken?.let {
                viewModel.getFilteredUserInfo(it, 0)  // 필터에 맞는 첫 페이지 데이터 호출
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
