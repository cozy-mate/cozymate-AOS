package umc.cozymate.ui.cozy_home.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import umc.cozymate.data.model.response.member.stat.GetMemberDetailInfoResponse
import umc.cozymate.databinding.FragmentCozyHomeContentDefaultBinding
import umc.cozymate.ui.cozy_home.room.recommended_room.RecommendedRoomVPAdapter
import umc.cozymate.ui.cozy_home.room.room_detail.CozyRoomDetailInfoActivity
import umc.cozymate.ui.cozy_home.room_detail.RoomDetailActivity
import umc.cozymate.ui.cozy_home.roommate.recommended_roommate.RecommendedRoommateVPAdapter
import umc.cozymate.ui.cozy_home.roommate.roommate_detail.CozyHomeRoommateDetailActivity
import umc.cozymate.ui.cozy_home.roommate.roommate_detail.RoommateDetailActivity
import umc.cozymate.ui.viewmodel.CozyHomeViewModel
import umc.cozymate.ui.viewmodel.RoommateDetailViewModel
import umc.cozymate.util.AnalyticsConstants
import umc.cozymate.util.AnalyticsEventLogger

@AndroidEntryPoint
class CozyHomeContentDefaultFragment : Fragment() {
    private val TAG = this.javaClass.simpleName
    private var _binding: FragmentCozyHomeContentDefaultBinding? = null
    private val binding get() = _binding!!
    private val cozyHomeViewModel: CozyHomeViewModel by viewModels()
    private val roommateDetailViewModel: RoommateDetailViewModel by viewModels()
    private var isLifestyleExist: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        Main: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCozyHomeContentDefaultBinding.inflate(inflater, Main, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.shimmerLayout1.startShimmer()
        binding.shimmerLayout1.visibility = View.VISIBLE
        binding.clRecommendRoom.visibility = View.GONE
        binding.divider.visibility = View.GONE
        binding.clRecommendRoommate.visibility = View.GONE
        setNickname()
        setRoommateRecommend()
        setRoomRecommend()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setNickname() {
        val nickname = cozyHomeViewModel.getNickname().toString()
        binding.tvName1.text = "${nickname}님과"
        binding.tvName2.text = "${nickname}님과"
        Log.d(TAG, "닉네임: $nickname")
    }

    private fun setRoommateRecommend() {
        fetchRoommateList()
        setRoommateList()
        setMoreRoommateBtn()
    }

    private fun fetchRoommateList() {
        cozyHomeViewModel.fetchRandomRoommateList()
    }

    private fun setRoommateList() {
        roommateDetailViewModel.otherUserDetailInfo.observe(viewLifecycleOwner) { otherUserDetail ->
            if (otherUserDetail == null) return@observe
            else {
                goToRoommateDetail(otherUserDetail)
            }
        }
        var adapter: RecommendedRoommateVPAdapter
        cozyHomeViewModel.randomRoommateList.observe(viewLifecycleOwner) { rmList ->
            if (rmList.isNullOrEmpty()) {
                binding.clRecommendRoommate.visibility = View.VISIBLE
                binding.divider.visibility = View.VISIBLE
                binding.vpRoommate.visibility = View.GONE
                binding.dotsIndicator1.visibility = View.GONE
                binding.tvEmptyRoommate.visibility = View.VISIBLE
            } else {
                binding.clRecommendRoommate.visibility = View.VISIBLE
                binding.divider.visibility = View.VISIBLE
                binding.vpRoommate.visibility = View.VISIBLE
                binding.dotsIndicator1.visibility = View.VISIBLE
                binding.tvEmptyRoommate.visibility = View.GONE
                adapter = RecommendedRoommateVPAdapter(rmList, false, isLifestyleExist) { memberId ->
                    // GA 이벤트 로그 추가
                    AnalyticsEventLogger.logEvent(
                        eventName = AnalyticsConstants.Event.BUTTON_CLICK_MATE_COMPONENT,
                        category = AnalyticsConstants.Category.HOME_CONTENT,
                        action = AnalyticsConstants.Action.BUTTON_CLICK,
                        label = AnalyticsConstants.Label.MATE_COMPONENT,
                    )

                    roommateDetailViewModel.getOtherUserDetailInfo(memberId)
                }
                binding.vpRoommate.adapter = adapter
                binding.dotsIndicator1.attachTo(binding.vpRoommate)

                // 스와이프 GA 이벤트 로그 추가
                binding.vpRoommate.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)

                        AnalyticsEventLogger.logEvent(
                            eventName = AnalyticsConstants.Event.GESTURE_MATE_SWIPE,
                            category = AnalyticsConstants.Category.HOME_CONTENT,
                            action = AnalyticsConstants.Action.GESTURE,
                            label = AnalyticsConstants.Label.MATE_SWIPE
                        )
                    }
                })
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
            // GA 이벤트 로그 추가
            AnalyticsEventLogger.logEvent(
                eventName = AnalyticsConstants.Event.BUTTON_CLICK_MATE_MORE,
                category = AnalyticsConstants.Category.HOME_CONTENT,
                action = AnalyticsConstants.Action.BUTTON_CLICK,
                label = AnalyticsConstants.Label.MATE_MORE,
            )

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
                    // GA 이벤트 로그 추가
                    AnalyticsEventLogger.logEvent(
                        eventName = AnalyticsConstants.Event.BUTTON_CLICK_ROOM_COMPONENT,
                        category = AnalyticsConstants.Category.HOME_CONTENT,
                        action = AnalyticsConstants.Action.BUTTON_CLICK,
                        label = AnalyticsConstants.Label.ROOM_COMPONENT,
                    )

                    goToRoomDetail(roomId)
                }
                binding.vpRoom.adapter = adapter
                binding.dotsIndicator2.attachTo(binding.vpRoom)

                // 스와이프 GA 이벤트 로그 추가
                binding.vpRoom.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)

                        AnalyticsEventLogger.logEvent(
                            eventName = AnalyticsConstants.Event.GESTURE_ROOM_SWIPE,
                            category = AnalyticsConstants.Category.HOME_CONTENT,
                            action = AnalyticsConstants.Action.GESTURE,
                            label = AnalyticsConstants.Label.ROOM_SWIPE
                        )
                    }
                })
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
            // GA 이벤트 로그 추가
            AnalyticsEventLogger.logEvent(
                eventName = AnalyticsConstants.Event.BUTTON_CLICK_ROOM_MORE,
                category = AnalyticsConstants.Category.HOME_CONTENT,
                action = AnalyticsConstants.Action.BUTTON_CLICK,
                label = AnalyticsConstants.Label.ROOM_MORE,
            )

            val intent = Intent(requireContext(), CozyRoomDetailInfoActivity::class.java)
            startActivity(intent)
        }
    }

    fun refreshData() {
        setNickname()
        fetchRoommateList()
        fetchRoomList()
    }
}