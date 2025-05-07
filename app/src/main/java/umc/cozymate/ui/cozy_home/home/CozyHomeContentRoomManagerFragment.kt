package umc.cozymate.ui.cozy_home.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import umc.cozymate.data.local.RoomInfoEntity
import umc.cozymate.data.model.entity.RecommendedMemberInfo
import umc.cozymate.data.model.response.member.stat.GetMemberDetailInfoResponse
import umc.cozymate.databinding.FragmentCozyHomeContentRoomManagerBinding
import umc.cozymate.ui.cozy_home.request.ReceivedJoinRequestAdapter
import umc.cozymate.ui.cozy_home.request.RoomManagerRequestActivity
import umc.cozymate.ui.cozy_home.room.recommended_room.RecommendedRoomVPAdapter
import umc.cozymate.ui.cozy_home.room.room_detail.CozyRoomDetailInfoActivity
import umc.cozymate.ui.cozy_home.room_detail.RoomDetailActivity
import umc.cozymate.ui.cozy_home.roommate.recommended_roommate.RecommendedRoommateVPAdapter
import umc.cozymate.ui.cozy_home.roommate.roommate_detail.CozyHomeRoommateDetailActivity
import umc.cozymate.ui.cozy_home.roommate.roommate_detail.RoommateDetailActivity
import umc.cozymate.ui.viewmodel.CozyHomeViewModel
import umc.cozymate.ui.viewmodel.RoomRequestViewModel
import umc.cozymate.ui.viewmodel.RoommateDetailViewModel
import umc.cozymate.ui.viewmodel.RoommateRecommendViewModel
import umc.cozymate.util.PreferencesUtil.KEY_IS_LIFESTYLE_EXIST
import umc.cozymate.util.PreferencesUtil.PREFS_NAME

@AndroidEntryPoint
class CozyHomeContentRoomManagerFragment : Fragment() {
    private val TAG = this.javaClass.simpleName
    private var _binding: FragmentCozyHomeContentRoomManagerBinding? = null
    private val binding get() = _binding!!
    private val cozyHomeViewModel: CozyHomeViewModel by viewModels()
    private val roommateRecommendViewModel: RoommateRecommendViewModel by viewModels()
    private val roommateDetailViewModel: RoommateDetailViewModel by viewModels()
    private val roomRequestViewModel: RoomRequestViewModel by viewModels()
    private var isLifestyleExist: Boolean = true
    private lateinit var roomInfoData: RoomInfoEntity
    val firebaseAnalytics = Firebase.analytics

    override fun onCreateView(
        inflater: LayoutInflater,
        Main: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCozyHomeContentRoomManagerBinding.inflate(inflater, Main, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.shimmerLayout1.stopShimmer()
        binding.shimmerLayout1.visibility = View.VISIBLE
        binding.clMyRoom.visibility = View.GONE
        binding.clRoomManagerRequest.visibility = View.GONE
        binding.clRecommendRoom.visibility = View.GONE
        binding.clRecommendRoommate.visibility = View.GONE
        binding.divider1.visibility = View.GONE
        binding.divider2.visibility = View.GONE
        binding.divider3.visibility = View.GONE
        setNickname()
        setMyRoom()
        setRoomManagerRequest()
        setRoommateRecommend()
        setRoomRecommend()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setNickname() {
        val nickname = cozyHomeViewModel.getNickname().toString()
        binding.tvNickname1.text = "${nickname}님이"
        binding.tvNickname2.text = "${nickname}님과"
        binding.tvNickname3.text = "${nickname}님과"
    }

    private fun setMyRoom() {
        viewLifecycleOwner.lifecycleScope.launch {
            fetchMyRoomData()
        }
    }

    private suspend fun fetchMyRoomData() {
        cozyHomeViewModel.getRoomInfoById().observe(viewLifecycleOwner, Observer { roomInfo ->
            if (roomInfo != null) {
                binding.clMyRoom.visibility = View.VISIBLE
                binding.divider1.visibility = View.VISIBLE
                roomInfoData = roomInfo
                setHashtagList(roomInfo.hashtagList)
                setRoomName(roomInfo.name)
                setArrivalNum(roomInfo.arrivalMateNum)
                setEquality(roomInfo.equality, roomInfo.arrivalMateNum)
                setMyRoomBtn(roomInfo.roomId)
            }
        })
    }

    private fun setRoomName(name: String) {
        binding.tvRoomName.text = name
    }

    private fun setHashtagList(hashtags: List<String>) {
        with(binding) {
            tvHashtag1.visibility = View.GONE
            tvHashtag2.visibility = View.GONE
            tvHashtag3.visibility = View.GONE
            when (hashtags.size) {
                1 -> {
                    if (hashtags[0] != "") {
                        tvHashtag1.visibility = View.VISIBLE
                        tvHashtag1.text = "#${hashtags.get(0)}"
                    }
                }

                2 -> {
                    tvHashtag1.visibility = View.VISIBLE
                    tvHashtag1.text = "#${hashtags.get(0)}"
                    tvHashtag2.visibility = View.VISIBLE
                    tvHashtag2.text = "#${hashtags.get(1)}"
                }

                3 -> {
                    tvHashtag1.visibility = View.VISIBLE
                    tvHashtag1.text = "#${hashtags.get(0)}"
                    tvHashtag2.visibility = View.VISIBLE
                    tvHashtag2.text = "#${hashtags.get(1)}"
                    tvHashtag3.visibility = View.VISIBLE
                    tvHashtag3.text = "#${hashtags.get(2)}"
                }
            }
        }
    }

    private fun setArrivalNum(num: Int) {
        binding.tvArrivalNum.text = "${num.toString()}명"
    }

    private fun setEquality(equality: Int, arrivalNum: Int) {
        binding.tvEquality.text = when {
            arrivalNum == 1 -> "- %" // 나만 있는 경우
            equality == 0 -> "?? %" // 라이프스타일 없는 경우
            else -> "${equality.toString()}%"
        }
    }

    private fun setMyRoomBtn(roomId: Int) {
        binding.clMyRoom.isEnabled = true
        binding.clMyRoom.setOnClickListener {
            val intent = Intent(requireActivity(), RoomDetailActivity::class.java).apply {
                putExtra(RoomDetailActivity.ARG_ROOM_ID, roomId)
            }
            startActivity(intent)
        }
    }

    private fun setRoomManagerRequest() {
        setRequestList()
        setMoreRequestBtn()
        fetchRequest()
    }

    private fun setRequestList() {
        val adapter = ReceivedJoinRequestAdapter { memberId ->
            roommateDetailViewModel.getOtherUserDetailInfo(memberId)
        }
        binding.rvRequestList.adapter = adapter
        binding.rvRequestList.layoutManager = LinearLayoutManager(requireContext())
        roomRequestViewModel.pendingMemberResponse.observe(viewLifecycleOwner) { response ->
            binding.clRoomManagerRequest.visibility = View.VISIBLE
            binding.divider2.visibility = View.VISIBLE
            val roomList = response?.result ?: emptyList()
            if (roomList.isNotEmpty()) {
                binding.tvRequestNum.text = "${roomList.size}개의"
                binding.clEmptyRequest.visibility = View.GONE
                adapter.submitList(roomList)
            } else if (response != null || response?.result?.isEmpty() == true) {
                binding.tvRequestNum.text = "0개의"
                binding.btnMoreRequest.visibility = View.GONE
                binding.clEmptyRequest.visibility = View.VISIBLE
                binding.clEmptyRequest.isEnabled = true
                binding.clEmptyRequest.setOnClickListener {
                    val intent =
                        Intent(requireActivity(), CozyHomeRoommateDetailActivity::class.java)
                    startActivity(intent)
                }
            }
        }
        roomRequestViewModel.isLoading2.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading == true || isLoading == null) {
                binding.clRoomManagerRequest.visibility = View.GONE
            }
        }
    }

    private fun setMoreRequestBtn() {
        binding.btnMoreRequest.setOnClickListener() {
            val intent = Intent(requireActivity(), RoomManagerRequestActivity::class.java)
            startActivity(intent)
        }
    }

    private fun fetchRequest() {
        viewLifecycleOwner.lifecycleScope.launch {
            roomRequestViewModel.getPendingMemberList()
        }
    }

    private fun setRoommateRecommend() {
        fetchRoommateList()
        setRoommateList()
        setMoreRoommateBtn()
    }

    private fun fetchRoommateList() {
        roommateRecommendViewModel.fetchRoommateListByEquality()
    }

    private fun setRoommateList() {
        roommateDetailViewModel.otherUserDetailInfo.observe(viewLifecycleOwner) { otherUserDetail ->
            if (otherUserDetail == null) return@observe
            else goToRoommateDetail(otherUserDetail)
        }
        var adapter: RecommendedRoommateVPAdapter
        cozyHomeViewModel.roommateListByEquality.observe(viewLifecycleOwner) { rmList ->
            binding.clRecommendRoommate.visibility = View.VISIBLE
            binding.divider3.visibility= View.VISIBLE
            updateRoommateUI(rmList)
        }
    }

    private fun updateRoommateUI(rmList: List<RecommendedMemberInfo>) {
        if (rmList.isNullOrEmpty()) {
            binding.vpRoommate.visibility = View.GONE
            binding.dotsIndicator1.visibility = View.GONE
            binding.tvEmptyRoommate.visibility = View.VISIBLE
        } else {
            val adapter = RecommendedRoommateVPAdapter(rmList) { memberId ->
                roommateDetailViewModel.getOtherUserDetailInfo(memberId)
            }
            binding.vpRoommate.adapter = adapter
            binding.dotsIndicator1.attachTo(binding.vpRoommate)
            binding.vpRoommate.visibility = View.VISIBLE
            binding.dotsIndicator1.visibility = View.VISIBLE
            binding.tvEmptyRoommate.visibility = View.GONE
        }
    }

    private fun goToRoommateDetail(otherUserDetail: GetMemberDetailInfoResponse.Result) {
        val intent = Intent(requireActivity(), RoommateDetailActivity::class.java)
        intent.putExtra("other_user_detail", otherUserDetail)
        startActivity(intent)
    }

    private fun setMoreRoommateBtn() {
        binding.btnMoreRoommate.setOnClickListener() {
            val intent = Intent(requireContext(), CozyHomeRoommateDetailActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setRoomRecommend() {
        fetchRoomList()
        setRoomList()
        setMoreRoomBtn()
    }

    private fun fetchRoomList() {
        viewLifecycleOwner.lifecycleScope.launch {
            cozyHomeViewModel.fetchRecommendedRoomList()
        }
    }

    private fun setRoomList() {
        var adapter: RecommendedRoomVPAdapter
        cozyHomeViewModel.recommendedRoomList.observe(viewLifecycleOwner) { roomList ->
            if (roomList.isNullOrEmpty()) {
                binding.shimmerLayout1.stopShimmer()
                binding.shimmerLayout1.visibility = View.GONE
                binding.clRecommendRoom.visibility = View.VISIBLE
                binding.vpRoom.visibility = View.GONE
                binding.dotsIndicator2.visibility = View.GONE
                binding.tvEmptyRoom.visibility = View.VISIBLE
            } else {
                binding.shimmerLayout1.stopShimmer()
                binding.shimmerLayout1.visibility = View.GONE
                binding.clRecommendRoom.visibility = View.VISIBLE
                binding.vpRoom.visibility = View.VISIBLE
                binding.dotsIndicator2.visibility = View.VISIBLE
                binding.tvEmptyRoom.visibility = View.GONE
                adapter = RecommendedRoomVPAdapter(roomList, isLifestyleExist) { roomId ->
                    goToRoomDetail(roomId)
                }
                binding.vpRoom.adapter = adapter
                binding.dotsIndicator2.attachTo(binding.vpRoom)
            }
        }
    }

    private fun goToRoomDetail(roomId: Int) {
        val intent = Intent(requireContext(), RoomDetailActivity::class.java).apply {
            putExtra(RoomDetailActivity.ARG_ROOM_ID, roomId)
        }
        startActivity(intent)
    }

    private fun setMoreRoomBtn() {
        binding.btnMoreRoom.setOnClickListener {
            val intent = Intent(requireContext(), CozyRoomDetailInfoActivity::class.java)
            startActivity(intent)
        }
    }

    fun refreshData() {
        setNickname()
        setMyRoom()
        fetchRequest()
        fetchRoommateList()
        fetchRoomList()
    }
}