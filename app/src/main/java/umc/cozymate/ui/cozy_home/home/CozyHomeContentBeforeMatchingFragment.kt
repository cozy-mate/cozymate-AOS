package umc.cozymate.ui.cozy_home.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import umc.cozymate.data.model.response.member.stat.GetMemberDetailInfoResponse
import umc.cozymate.databinding.FragmentCozyHomeContentBeforeMatchingBinding
import umc.cozymate.ui.cozy_home.request.BeforeMatchingRequestActivity
import umc.cozymate.ui.cozy_home.room.recommended_room.RecommendedRoomVPAdapter
import umc.cozymate.ui.cozy_home.room.room_detail.CozyRoomDetailInfoActivity
import umc.cozymate.ui.cozy_home.request.SentRequestAdapter
import umc.cozymate.ui.cozy_home.room_detail.RoomDetailActivity
import umc.cozymate.ui.cozy_home.roommate.recommended_roommate.RecommendedRoommateVPAdapter
import umc.cozymate.ui.cozy_home.roommate.roommate_detail.CozyHomeRoommateDetailActivity
import umc.cozymate.ui.cozy_home.roommate.roommate_detail.RoommateDetailActivity
import umc.cozymate.ui.viewmodel.CozyHomeViewModel
import umc.cozymate.ui.viewmodel.RoomRequestViewModel
import umc.cozymate.ui.viewmodel.RoommateDetailViewModel

@AndroidEntryPoint
class CozyHomeContentBeforeMatchingFragment : Fragment() {
    private val TAG = this.javaClass.simpleName
    private var _binding: FragmentCozyHomeContentBeforeMatchingBinding? = null
    private val binding get() = _binding!!
    private val cozyHomeViewModel: CozyHomeViewModel by viewModels()
    private val roommateDetailViewModel: RoommateDetailViewModel by viewModels()
    private val roomRequestViewModel: RoomRequestViewModel by viewModels()
    private var isLifestyleExist: Boolean = true
    val firebaseAnalytics = Firebase.analytics

    override fun onCreateView(
        inflater: LayoutInflater,
        Main: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCozyHomeContentBeforeMatchingBinding.inflate(inflater, Main, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.shimmerLayout1.startShimmer()
        binding.shimmerLayout1.visibility = View.VISIBLE
        binding.clRecommendRoom.visibility = View.GONE
        binding.divider2.visibility = View.GONE
        binding.clRoomParticipantRequest.visibility = View.GONE
        binding.divider3.visibility = View.GONE
        binding.clRecommendRoommate.visibility = View.GONE
        setNickname()
        setRoomParticipantRequest()
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
        binding.tvNickname2.text = "${nickname}님이"
        binding.tvNickname3.text = "${nickname}님과"
        binding.tvNickname4.text = "${nickname}님과"
    }

    private fun setRoomParticipantRequest() {
        binding.clRoomParticipantRequest.visibility = View.GONE
        setRequestList()
        fetchRequest()
    }

    private fun setRequestList() {
        val adapter = SentRequestAdapter { roomId ->
            val intent = Intent(requireActivity(), RoomDetailActivity::class.java).apply {
                putExtra(RoomDetailActivity.ARG_ROOM_ID, roomId)
            }
            startActivity(intent)
        }
        binding.rvRequestList.adapter = adapter
        binding.rvRequestList.layoutManager = LinearLayoutManager(requireContext())
        roomRequestViewModel.requestedRoomResponse.observe(viewLifecycleOwner) { response ->
            val roomList = response?.result?.result
            if (!roomList.isNullOrEmpty()) {
                binding.clRoomParticipantRequest.visibility = View.VISIBLE
                binding.divider2.visibility = View.VISIBLE
                adapter.submitList(roomList!!)
                setMoreRequestBtn()
            } else {
                binding.clRoomParticipantRequest.visibility = View.GONE
                binding.divider2.visibility = View.GONE
            }
        }
        roomRequestViewModel.isLoading1.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading == true || isLoading == null) {
                binding.divider2.visibility = View.GONE
                binding.clRoomParticipantRequest.visibility = View.GONE
            }
        }
    }

    private fun setMoreRequestBtn() {
        binding.btnMoreRequest.setOnClickListener() {
            val intent = Intent(requireActivity(), BeforeMatchingRequestActivity::class.java)
            startActivity(intent)
        }
    }

    private fun fetchRequest() {
        viewLifecycleOwner.lifecycleScope.launch {
            roomRequestViewModel.getRequestedRoomList()
        }
    }

    private fun setRoommateRecommend() {
        fetchRoommateList()
        setRoommateList()
        setMoreRoommateBtn()
    }

    private fun fetchRoommateList() {
        cozyHomeViewModel.fetchRoommateListByEquality()
    }

    private fun setRoommateList() {
        roommateDetailViewModel.otherUserDetailInfo.observe(viewLifecycleOwner) { otherUserDetail ->
            if (otherUserDetail == null) return@observe
            else goToRoommateDetail(otherUserDetail)
        }
        var adapter: RecommendedRoommateVPAdapter
        cozyHomeViewModel.roommateListByEquality.observe(viewLifecycleOwner) { rmList ->
            binding.clRecommendRoommate.visibility = View.VISIBLE
            binding.divider3.visibility = View.VISIBLE
            if (rmList.isNullOrEmpty()) {
                binding.vpRoommate.visibility = View.GONE
                binding.dotsIndicator1.visibility = View.GONE
                binding.tvEmptyRoommate.visibility = View.VISIBLE
            } else {
                binding.vpRoommate.visibility = View.VISIBLE
                binding.dotsIndicator1.visibility = View.VISIBLE
                binding.tvEmptyRoommate.visibility = View.GONE
                adapter = RecommendedRoommateVPAdapter(rmList) { memberId ->
                    roommateDetailViewModel.getOtherUserDetailInfo(memberId)
                }
                binding.vpRoommate.adapter = adapter
                binding.dotsIndicator1.attachTo(binding.vpRoommate)
            }
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
        fetchRequest()
        fetchRoommateList()
        fetchRoomList()
    }
}