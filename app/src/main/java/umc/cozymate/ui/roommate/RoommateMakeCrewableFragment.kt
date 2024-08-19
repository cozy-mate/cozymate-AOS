package umc.cozymate.ui.roommate

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

@AndroidEntryPoint
class RoommateMakeCrewableFragment : Fragment() {
    private var _binding: FragmentRoommateMakeCrewableBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RoommateViewModel by viewModels()
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
                roommateMakeCrewableInfoListRVA.updateData(userInfoList)
            }
        }

        // 액세스 토큰을 가져와 API 호출 (필터 적용되지 않은 데이터 먼저 가져오기)
        val _accessToken = getString(R.string.access_token_1)
        val accessToken = "Bearer $_accessToken"
        viewModel.getInitialUserInfo(accessToken, 0) // 필터가 없는 데이터 가져오기
//        viewModel.getInitialUserInfo(accessToken, 1) // 필터가 없는 데이터 가져오기

        // 필터가 변경될 때마다 필터링된 데이터를 가져옴
        viewModel.filterList.observe(viewLifecycleOwner) {
            viewModel.getOtherUserInfo(accessToken, 0) // 필터가 적용된 데이터를 가져옴
        }

        return binding.root
    }
    fun initTextView(){
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

    private fun setupTextSelection(textView: TextView, filterText: String){
        textView.setOnClickListener {
            if(textView.isSelected) {
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
