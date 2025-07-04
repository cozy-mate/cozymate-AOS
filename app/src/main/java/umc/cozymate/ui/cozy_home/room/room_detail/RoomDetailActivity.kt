package umc.cozymate.ui.cozy_home.room_detail

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.logEvent
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import umc.cozymate.R
import umc.cozymate.data.model.response.room.GetRoomInfoResponse
import umc.cozymate.databinding.ActivityRoomDetailBinding
import umc.cozymate.databinding.DialogMemberStatBinding
import umc.cozymate.ui.MainActivity
import umc.cozymate.ui.viewmodel.JoinRoomViewModel
import umc.cozymate.util.CustomDividerItemDecoration
import umc.cozymate.ui.viewmodel.RoomDetailViewModel
import umc.cozymate.ui.cozy_home.room.room_detail.RoomInvitedListRVA
import umc.cozymate.ui.cozy_home.room.room_detail.RoomMemberListRVA
import umc.cozymate.ui.cozy_home.room.room_detail.RoomMemberStatRVA
import umc.cozymate.ui.cozy_home.roommate.roommate_detail.RoommateDetailActivity
import umc.cozymate.ui.viewmodel.RoommateDetailViewModel
import umc.cozymate.ui.message.WriteMessageActivity
import umc.cozymate.ui.pop_up.PopupClick
import umc.cozymate.ui.pop_up.TwoButtonPopup
import umc.cozymate.ui.roommate.RoommateOnboardingActivity
import umc.cozymate.ui.viewmodel.CozyHomeViewModel
import umc.cozymate.ui.viewmodel.FavoriteViewModel
import umc.cozymate.ui.viewmodel.MakingRoomViewModel
import umc.cozymate.util.AnalyticsChipMapper
import umc.cozymate.util.AnalyticsConstants
import umc.cozymate.util.AnalyticsEventLogger
import umc.cozymate.util.CharacterUtil
import umc.cozymate.util.PreferencesUtil
import umc.cozymate.util.SnackbarUtil
import umc.cozymate.util.StatusBarUtil
import umc.cozymate.util.navigationHeight
import umc.cozymate.util.setStatusBarTransparent

// 방 생성 후, 내방 컴포넌트 클릭 후 화면 전환할 때 room_id를 받아오도록 구현해놨습니다. 이해 안되는거 있음 얘기해주세요
@AndroidEntryPoint
class RoomDetailActivity : AppCompatActivity() {
    private val TAG = this.javaClass.simpleName
    private lateinit var binding: ActivityRoomDetailBinding
    private val viewModel: RoomDetailViewModel by viewModels()
    private val cozyHomeViewModel: CozyHomeViewModel by viewModels()
    private val roommateDetailViewModel: RoommateDetailViewModel by viewModels()
    private val favoriteViewModel: FavoriteViewModel by viewModels()
    private val roomViewModel: MakingRoomViewModel by viewModels()
    private val joinRoomViewModel: JoinRoomViewModel by viewModels()
    private var screenEnterTime: Long = 0


    private var roomId: Int = 0
    private var favoriteId: Int = 0
    private var managerMemberId: Int? = 0
    private var activeDialog: AlertDialog? = null // 현재 활성화된 다이얼로그 추적
    private var isFavorite: Boolean = false // 찜 상태를 추적하는 변수 추가
    private var managerNickname : String? = null

    // 방 id는  Intent를 통해 불러옵니다
    companion object {
        const val ARG_ROOM_ID = "room_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoomDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.setStatusBarTransparent()
        StatusBarUtil.updateStatusBarColor(this@RoomDetailActivity, Color.WHITE)
        binding.main.setPadding(0, 0, 0, this.navigationHeight())

        getRoomId()

        val spf = getSharedPreferences(PreferencesUtil.PREFS_NAME, MODE_PRIVATE)
        val savedRoomId = spf.getInt("room_id", -2)

        observeRoomInfo()
        setupFavoriteButton()

        if (savedRoomId == roomId) {
            updateUserRoomInfo()
        } else {
            getRoomDetailInfo()
        }

        updateFloatingButton()

        setupBackButton()

        // 룸메 상세정보창과 연결
        observeOtherUserInfo()

        binding.ivChat.setOnClickListener {
            // GA 이벤트 로그 추가
            AnalyticsEventLogger.logEvent(
                eventName = AnalyticsConstants.Event.BUTTON_CLICK_ROOM_MESSAGE,
                category = AnalyticsConstants.Category.ROOM_DETAIL,
                action = AnalyticsConstants.Action.BUTTON_CLICK,
                label = AnalyticsConstants.Label.ROOM_MESSAGE,
            )

            val intent: Intent = Intent(this, WriteMessageActivity::class.java)
            Log.d(TAG,"managerNickname ${managerNickname}")
            intent.putExtra("recipientId", managerMemberId)
            intent.putExtra("nickname",  managerNickname)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        screenEnterTime = System.currentTimeMillis()

        // GA 이벤트 로그 추가
        AnalyticsEventLogger.logEvent(
            eventName = AnalyticsConstants.Event.PAGE_VIEW_ROOM_DETAIL,
            category = AnalyticsConstants.Category.ROOM_DETAIL,
            action = AnalyticsConstants.Action.PAGE_VIEW,
            label = AnalyticsConstants.Label.ROOM_DETAIL,
        )
    }

    override fun onPause() {
        super.onPause()
        val screenLeaveTime = System.currentTimeMillis()
        val sessionDuration = screenLeaveTime - screenEnterTime // 밀리초 단위

        // GA 이벤트 로그 추가
        AnalyticsEventLogger.logEvent(
            eventName = AnalyticsConstants.Event.SESSION_TIME_ROOM_DETAIL,
            category = AnalyticsConstants.Category.ROOM_DETAIL,
            action = AnalyticsConstants.Action.SESSION_TIME,
            label = AnalyticsConstants.Label.ROOM_DETAIL,
            duration = sessionDuration
        )
    }

    private fun getRoomId() {
        // 방 id 불러오기
        roomId = intent.getIntExtra(ARG_ROOM_ID, -1)
        if (roomId == -1) {
            Log.e(TAG, "Invalid room ID received!")
        } else {
            lifecycleScope.launch {
                viewModel.getOtherRoomInfo(roomId!!)
                viewModel.fetchInvitedMembers(roomId!!)
            }
            Log.d(TAG, "Received room ID: $roomId")
        }
    }

    private fun updateRoomInfo() {
        lifecycleScope.launch {
            viewModel.getOtherRoomInfo(roomId)
        }
    }

    private fun updateUserRoomInfo() {
        lifecycleScope.launch {
            Log.d(TAG, "updateUserRoomInfo 진입")
            viewModel.otherRoomDetailInfo.collectLatest { roomInfo ->
                with(binding) {
                    tvRoomName.text = roomInfo.name
                    CharacterUtil.setImg(roomInfo.persona, ivRoomCharacter)
                    updateRoomManager(roomInfo.isRoomManager)
                    when (roomInfo.equality) {
                        0 -> tvRoomMatch.text = "방 평균 일치율 - %"
                        else -> tvRoomMatch.text = "방 평균 일치율 ${roomInfo.equality}%"
                    }
                    ivLike.visibility = View.INVISIBLE
                    ivExit.visibility = View.VISIBLE
                    fabBnt.visibility = View.GONE
                    tvRoomInfoCurrentNum.text = roomInfo.arrivalMateNum.toString()
                    tvRoomInfoTotalNum.text = " / ${roomInfo.maxMateNum}"
                    tvDormitoryName.text = roomInfo.dormitoryName
                    tvDormitoryRoomNum.text = "${roomInfo.maxMateNum}인실"
                    updateDifference(roomInfo.difference)
                    managerMemberId = roomInfo.managerMemberId
                    managerNickname = roomInfo.managerNickname
                    exitButton(roomInfo.roomId)
                    // 리사이클러 뷰 연결
                    rvRoomMemberList.apply {
                        layoutManager = LinearLayoutManager(this@RoomDetailActivity)
                        adapter = RoomMemberListRVA(
                            roomInfo.mateDetailList,
                            roomInfo.managerNickname
                        ) { memberId ->

                            // GA 이벤트 로그 추가
                            AnalyticsEventLogger.logEvent(
                                eventName = AnalyticsConstants.Event.BUTTON_CLICK_ROOM_MATE_COMPONENT,
                                category = AnalyticsConstants.Category.ROOM_DETAIL,
                                action = AnalyticsConstants.Action.BUTTON_CLICK,
                                label = AnalyticsConstants.Label.ROOM_MATE_COMPONENT,
                            )

                            navigatorToRoommateDetail(memberId)
                        }
                    }
                    ivSetting.visibility = View.GONE // 기능 구현 후 visible로 수정
                }
            }
        }
        lifecycleScope.launch {
            Log.d(TAG, "invitedMembers LifecycleScope 실행")
            viewModel.invitedMembers.collectLatest { invitedInfo ->
                Log.d(TAG, "invitedMembers.collectLatest 호출")
                if (invitedInfo.isEmpty()) {
                    binding.clInvitedMember.visibility = View.GONE
                    Log.d(TAG, "InvitedMember Empty")
                } else {
                    Log.d(TAG, "InvitedMember Not Empty")
                    binding.clInvitedMember.visibility = View.VISIBLE
                    binding.rvInvitedMember.apply {
                        layoutManager = LinearLayoutManager(this@RoomDetailActivity)
                        adapter = RoomInvitedListRVA(
                            invitedInfo
                        ) { memberId ->
                            navigatorToRoommateDetail(memberId)
                        }
                    }
                }
            }
        }
    }

    private fun exitButton(roomId: Int) {
        val spf = getSharedPreferences("app_prefs", MODE_PRIVATE)

        with(binding) {
            ivExit.setOnClickListener {

                // GA 이벤트 로그 추가
                AnalyticsEventLogger.logEvent(
                    eventName = AnalyticsConstants.Event.BUTTON_CLICK_ROOM_OUT,
                    category = AnalyticsConstants.Category.ROOM_DETAIL,
                    action = AnalyticsConstants.Action.BUTTON_CLICK,
                    label = AnalyticsConstants.Label.ROOM_OUT,
                )

                val text = listOf("방을 나가시겠습니까?", "", "취소", "확인")
                val dialog = TwoButtonPopup(text, object : PopupClick {
                    override fun rightClickFunction() {
                        // 방 나가기 로직 실행
                        roomViewModel.quitRoom(roomId)

                        roomViewModel.roomQuitResult.observe(this@RoomDetailActivity) { result ->
                            if (result.isSuccess) {
                                SnackbarUtil.showCustomSnackbar(
                                    context = this@RoomDetailActivity,
                                    message = "방을 성공적으로 나갔습니다.",
                                    iconType = SnackbarUtil.IconType.YES,
                                    anchorView = binding.fabBnt,
                                    extraYOffset = 20
                                )
                                spf.edit().putInt("room_id", 0).apply()
                                val intent =
                                    Intent(this@RoomDetailActivity, MainActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                startActivity(intent)
                                finish() // 방 나가기 성공 시 메인액티비티로 이동하고 액티비티 종료
                            } else {
                                SnackbarUtil.showCustomSnackbar(
                                    context = this@RoomDetailActivity,
                                    message = "방 나가기에 실패했습니다. 다시 시도해주세요",
                                    iconType = SnackbarUtil.IconType.NO,
                                    anchorView = binding.fabBnt,
                                    extraYOffset = 20
                                )
                            }
                        }
                    }
                }, true) // true로 설정하면 다이얼로그 외부 클릭 시 닫히지 않음
                dialog.show(supportFragmentManager, "QuitRoomPopup")
            }
        }
    }

    private fun observeRoomInfo() {
        lifecycleScope.launch {
            viewModel.otherRoomDetailInfo.collectLatest { roomInfo ->
                roomId = roomInfo.roomId
                favoriteId = roomInfo.favoriteId
                updateFavoriteButton() // 방 찜 상태 UI 업데이트
            }
        }
    }

    private fun setupFavoriteButton() {
        binding.ivLike.setOnClickListener {

            // GA 이벤트 로그 추가
            AnalyticsEventLogger.logEvent(
                eventName = AnalyticsConstants.Event.BUTTON_CLICK_ROOM_LIKE,
                category = AnalyticsConstants.Category.ROOM_DETAIL,
                action = AnalyticsConstants.Action.BUTTON_CLICK,
                label = AnalyticsConstants.Label.ROOM_LIKE,
            )

            lifecycleScope.launch {
                favoriteViewModel.toggleRoomFavorite(
                    roomId = roomId,
                    favoriteId = favoriteId,
                    onUpdate = {
                        lifecycleScope.launch {
                            viewModel.getOtherRoomInfo(roomId) // 방 정보 업데이트
                            viewModel.otherRoomDetailInfo.collectLatest { updatedRoomInfo ->
                                favoriteId = updatedRoomInfo.favoriteId // 갱신된 favoriteId 적용
                                updateFavoriteButton()
                            }
                        }
                    },
                    onError = { errorMessage ->
                        SnackbarUtil.showCustomSnackbar(
                            context = this@RoomDetailActivity,
                            message = "문제가 발생했어요.",
                            iconType = SnackbarUtil.IconType.NO,
                        )
                    }
                )
            }
        }
    }

    private fun updateFavoriteButton() {
        binding.ivLike.setImageResource(
            if (favoriteId == 0) R.drawable.ic_heart else R.drawable.ic_heartfull
        )
        binding.ivLike.setColorFilter(
            ContextCompat.getColor(
                this,
                if (favoriteId == 0) R.color.unuse_font else R.color.red
            )
        )
    }

    private fun getRoomDetailInfo() {
        viewModel.roomName.observe(this) { name ->
            binding.tvRoomName.text = name
        }

        lifecycleScope.launch {
            viewModel.otherRoomDetailInfo.collectLatest { roomInfo ->
                updateUI(roomInfo)
            }
        }
    }


    private fun updateUI(roomInfo: GetRoomInfoResponse.Result) {
        CharacterUtil.setImg(roomInfo.persona, binding.ivRoomCharacter)
        updateHashtags(roomInfo.hashtagList)
        with(binding) {
            tvRoomName.text = roomInfo.name
            tvRoomMatch.text = "방 평균 일치율 ${roomInfo.equality}%"
            tvRoomInfoCurrentNum.text = roomInfo.arrivalMateNum.toString()
            tvRoomInfoTotalNum.text = " / ${roomInfo.maxMateNum}"
            tvDormitoryName.text = roomInfo.dormitoryName
            tvDormitoryRoomNum.text = "${roomInfo.maxMateNum}인실"
            updateDifference(roomInfo.difference)
            managerMemberId = roomInfo.managerMemberId
            managerNickname = roomInfo.managerNickname
            updateOtherRoomFab(roomInfo.roomId)
            // 초기 찜 상태 설정
            isFavorite = roomInfo.favoriteId != 0
//            updateFavoriteIcon(isFavorite)

            // 리사이클러 뷰 연결
            rvRoomMemberList.apply {
                layoutManager = LinearLayoutManager(this@RoomDetailActivity)
                adapter = RoomMemberListRVA(
                    roomInfo.mateDetailList,
                    roomInfo.managerNickname
                ) { memberId ->

                    // GA 이벤트 로그 추가
                    AnalyticsEventLogger.logEvent(
                        eventName = AnalyticsConstants.Event.BUTTON_CLICK_ROOM_MATE_COMPONENT,
                        category = AnalyticsConstants.Category.ROOM_DETAIL,
                        action = AnalyticsConstants.Action.BUTTON_CLICK,
                        label = AnalyticsConstants.Label.ROOM_MATE_COMPONENT,
                    )

                    navigatorToRoommateDetail(memberId)
                }
            }
        }
    }

    private fun updateOtherRoomFab(roomId: Int) {
        val spf = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val mbti = spf.getString("user_mbti", null)
        val savedRoomId = spf.getInt("room_id", -2)

        if (mbti.isNullOrEmpty()) {
            // 라이프스타일 입력을 하지 않은 경우
            inputLifeStyle()
            return
        }

        if (savedRoomId != 0 && savedRoomId != -2) {
            // 내 방이 있는 경우
            with(binding) {
                fabBnt.text = "방 참여요청"
                fabBnt.backgroundTintList = getColorStateList(R.color.gray)
                fabBnt.setTextColor(getColor(R.color.white))
                fabBnt.isEnabled = true

                fabBnt.setOnClickListener {
                    // GA 이벤트 로그 추가
                    AnalyticsEventLogger.logEvent(
                        eventName = AnalyticsConstants.Event.BUTTON_CLICK_INVITE_ROOM,
                        category = AnalyticsConstants.Category.ROOM_DETAIL,
                        action = AnalyticsConstants.Action.BUTTON_CLICK,
                        label = AnalyticsConstants.Label.INVITE_ROOM,
                    )

                    SnackbarUtil.showCustomSnackbar(
                        context = this@RoomDetailActivity,
                        message = "이미 방에 참여 중 입니다.",
                        iconType = SnackbarUtil.IconType.NO,
                        anchorView = binding.fabBnt,
                        extraYOffset = 20
                    )
                }
            }
            return
        }

        // 내 방이 없는 경우
        lifecycleScope.launch {
            // 방 초대 상태 확인
            viewModel.getInvitedRoomStatus(roomId)
            // 방 참여 요청 상태 확인
            viewModel.getPendingRoomStatus(roomId)

            // 초대 상태 및 참여 요청 상태를 동시에 관찰
            viewModel.isInvitedToRoom.observe(this@RoomDetailActivity) { isInvited ->
                if (isInvited) {
                    // 초대를 받은 경우
                    with(binding) {
                        clAcceptBtn.visibility = View.VISIBLE
                        fabBnt.visibility = View.GONE

                        fabAcceptRefuse.setOnClickListener {
                            // GA 이벤트 로그 추가
                            AnalyticsEventLogger.logEvent(
                                eventName = AnalyticsConstants.Event.BUTTON_CLICK_ROOM_REJECT,
                                category = AnalyticsConstants.Category.ROOM_DETAIL,
                                action = AnalyticsConstants.Action.BUTTON_CLICK,
                                label = AnalyticsConstants.Label.ROOM_REJECT,
                            )

                            // 초대 거절 처리
                            viewModel.acceptRoomEnter(roomId, accept = false)
                            clAcceptBtn.visibility = View.GONE
                            fabBnt.visibility = View.VISIBLE
                            updateRoomInfo()
                        }

                        fabAcceptAccept.setOnClickListener {
                            // GA 이벤트 로그 추가
                            AnalyticsEventLogger.logEvent(
                                eventName = AnalyticsConstants.Event.BUTTON_CLICK_ROOM_ACCEPT,
                                category = AnalyticsConstants.Category.ROOM_DETAIL,
                                action = AnalyticsConstants.Action.BUTTON_CLICK,
                                label = AnalyticsConstants.Label.ROOM_ACCEPT,
                            )

                            // 초대 수락 처리
                            lifecycleScope.launch {
                                viewModel.acceptRoomEnter(roomId, accept = true)
                                clAcceptBtn.visibility = View.GONE
                                fabBnt.visibility = View.VISIBLE
                                delay(1000)
                                updateRoomInfo()
                            }
                        }
                    }
                } else {
                    // 초대를 받지 않은 경우
                    viewModel.isPendingRoom.observe(this@RoomDetailActivity) { isPending ->
                        with(binding) {
                            clAcceptBtn.visibility = View.GONE
                            fabBnt.visibility = View.VISIBLE

                            if (isPending) {
                                // 이미 방 참여 요청을 한 경우
                                fabBnt.text = "방 참여요청 취소"
                                fabBnt.setBackgroundTintList(getColorStateList(R.color.white))
                                fabBnt.setTextColor(getColor(R.color.main_blue))
                                fabBnt.setStrokeColorResource(R.color.main_blue) // 테두리 설정
                                fabBnt.strokeWidth = 2 // 테두리 두께 설정

                                fabBnt.setOnClickListener {
                                    // GA 이벤트 로그 추가
                                    AnalyticsEventLogger.logEvent(
                                        eventName = AnalyticsConstants.Event.BUTTON_CLICK_INVITE_ROOM,
                                        category = AnalyticsConstants.Category.ROOM_DETAIL,
                                        action = AnalyticsConstants.Action.BUTTON_CLICK,
                                        label = AnalyticsConstants.Label.INVITE_ROOM,
                                    )

                                    // 방 참여 요청 취소 처리
                                    viewModel.cancelJoinRequest(roomId)
                                    updateRoomInfo()
                                }
                            } else {
                                // 방 참여 요청을 하지 않은 경우
                                fabBnt.text = "방 참여요청"
                                fabBnt.setBackgroundTintList(getColorStateList(R.color.main_blue))
                                fabBnt.setTextColor(getColor(R.color.white))
                                fabBnt.setOnClickListener {
                                    // GA 이벤트 로그 추가
                                    AnalyticsEventLogger.logEvent(
                                        eventName = AnalyticsConstants.Event.BUTTON_CLICK_INVITE_ROOM_CANCLE,
                                        category = AnalyticsConstants.Category.ROOM_DETAIL,
                                        action = AnalyticsConstants.Action.BUTTON_CLICK,
                                        label = AnalyticsConstants.Label.INVITE_ROOM_CANCLE,
                                    )

                                    // 방 참여 요청 처리
                                    lifecycleScope.launch {
                                        joinRoomViewModel.requestJoinRoom(roomId)
                                        delay(500)
                                        viewModel.getPendingRoomStatus(roomId) // 상태 갱신
                                        updateRoomInfo()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    private fun inputLifeStyle() {
        with(binding) {
            fabBnt.text = "라이프스타일 입력하고 방 참여하기"
            fabBnt.setBackgroundTintList(
                getColorStateList(R.color.main_blue)
            )
            fabBnt.setTextColor(getColor(R.color.white))
            fabBnt.setOnClickListener {
                // GA 이벤트 로그 추가
                AnalyticsEventLogger.logEvent(
                    eventName = AnalyticsConstants.Event.BUTTON_CLICK_LIFE_STYLE_COMPONENT,
                    category = AnalyticsConstants.Category.ROOM_DETAIL,
                    action = AnalyticsConstants.Action.BUTTON_CLICK,
                    label = AnalyticsConstants.Label.LIFE_STYLE_COMPONENT,
                )

                val intent = Intent(
                    this@RoomDetailActivity,
                    RoommateOnboardingActivity::class.java
                )
                startActivity(intent)
            }
        }
    }

    private fun navigatorToRoommateDetail(memberId: Int) {
        roommateDetailViewModel.getOtherUserDetailInfo(memberId)
    }

    // 룸메 상세정보 받아오기
    private fun observeOtherUserInfo() {
        roommateDetailViewModel.otherUserDetailInfo.observe(this) { otherUserDetail ->
            if (otherUserDetail == null) return@observe
            else {
                val intent = Intent(this, RoommateDetailActivity::class.java)
                intent.putExtra("other_user_detail", otherUserDetail)
                startActivity(intent)
            }
        }
    }

    // 해시태그 업데이트
    private fun updateHashtags(hashtags: List<String>) {
        with(binding) {
            Log.d(TAG, "해시태그 : $hashtags")
            when (hashtags.size) {
                0 -> {
                    tvHashtag1.visibility = View.GONE
                    tvHashtag2.visibility = View.GONE
                    tvHashtag3.visibility = View.GONE
                }

                1 -> {
                    tvHashtag1.text = "#${hashtags[0]}"
                    tvHashtag1.visibility = View.VISIBLE
                    tvHashtag2.visibility = View.GONE
                    tvHashtag3.visibility = View.GONE
                }

                2 -> {
                    tvHashtag1.text = "#${hashtags[0]}"
                    tvHashtag1.visibility = View.VISIBLE
                    tvHashtag2.text = "#${hashtags[1]}"
                    tvHashtag2.visibility = View.VISIBLE
                    tvHashtag3.visibility = View.GONE
                }

                3 -> {
                    tvHashtag1.text = "#${hashtags[0]}"
                    tvHashtag1.visibility = View.VISIBLE
                    tvHashtag2.text = "#${hashtags[1]}"
                    tvHashtag2.visibility = View.VISIBLE
                    tvHashtag3.text = "#${hashtags[2]}"
                    tvHashtag3.visibility = View.VISIBLE
                }
            }
            Log.d(
                TAG,
                "tvHashtag1: ${tvHashtag1.text}, visibility: ${tvHashtag1.visibility}"
            )
            Log.d(
                TAG,
                "tvHashtag2: ${tvHashtag2.text}, visibility: ${tvHashtag2.visibility}"
            )
            Log.d(
                TAG,
                "tvHashtag3: ${tvHashtag3.text}, visibility: ${tvHashtag3.visibility}"
            )
        }
    }

    private fun updateRoomManager(isRoomManager: Boolean) {
        if (isRoomManager) {
            binding.ivSetting.visibility = View.VISIBLE
        } else {
            binding.ivSetting.visibility = View.GONE
        }
    }

    private fun updateDifference(difference: GetRoomInfoResponse.Result.Difference) {
        val viewMap = mapOf(
            "coolingIntensity" to binding.selectAc,
            "callingStatus" to binding.selectCall,
            "sleepingTime" to binding.selectSleep,
            "noiseSensitivity" to binding.selectNoise,
            "wakeUpTime" to binding.selectWake,
            "turnOffTime" to binding.selectLightOff,
            "admissionYear" to binding.selectNumber,
            "mbti" to binding.selectMbti,
            "heatingIntensity" to binding.selectHeater,
            "drinkingFrequency" to binding.selectDrinkFrequency,
            "studyingStatus" to binding.selectStudy,
            "sharingStatus" to binding.selectShare,
            "sleepingHabits" to binding.selectSleepHabit,
            "intimacy" to binding.selectFriendly,
            "lifePattern" to binding.selectLivingPattern,
            "dormJoiningStatus" to binding.selectAcceptance,
            "cleannessSensitivity" to binding.selectClean,
            "personalities" to binding.selectPersonality,
            "birthYear" to binding.selectBirth,
            "cleaningFrequency" to binding.selectCleanFrequency,
            "smokingStatus" to binding.selectSmoke,
            "majorName" to binding.selectMajor,
            "gamingStatus" to binding.selectGame,
            "eatingStatus" to binding.selectIntake
        )

        val flexboxLayout = binding.chips1
        val spf = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val savedRoomId = spf.getInt("room_id", -2)

        // 내 방인지 확인
        val isMyRoom = savedRoomId == roomId
        val isAlone = (difference.blue.size + difference.red.size + difference.white.size == 1)

        // 모든 칩 초기화
        viewMap.values.forEach { view ->
            view.setBackgroundResource(R.drawable.custom_select_chip_default)
            view.setTextColor(getColor(R.color.unuse_font))
            view.setOnClickListener(null) // 기존 리스너 제거
        }

        // 칩 추가를 위한 리스트
        val blueViews = mutableListOf<View>()
        val redViews = mutableListOf<View>()
        val whiteViews = mutableListOf<View>()

        val isChipClickable = !(isMyRoom &&isAlone)
        // 파란색 칩 업데이트 및 추가
        difference.blue.forEach { key ->
            viewMap[key]?.let { view ->
                view.setBackgroundResource(R.drawable.custom_select_chip_blue)
                view.setTextColor(getColor(R.color.main_blue))
                if (isChipClickable) {
                    view.setOnClickListener {

                        // GA 이벤트 로그 추가
                        AnalyticsChipMapper.chipTextMap[view.text.toString()]?.let { eventInfo ->
                            AnalyticsEventLogger.logEvent(
                                eventName = eventInfo.eventName,
                                category = AnalyticsConstants.Category.ROOM_DETAIL,
                                action = eventInfo.action,
                                label = eventInfo.label ?: ""
                            )
                        }

                        showMemberStatDialog(roomId!!, key, getColor(R.color.main_blue))
                    }
                }
                blueViews.add(view)
            }
        }

        // 빨간색 칩 업데이트 및 추가
        difference.red.forEach { key ->
            viewMap[key]?.let { view ->
                view.setBackgroundResource(R.drawable.custom_select_chip_red)
                view.setTextColor(getColor(R.color.red))
                if (isChipClickable) {
                    view.setOnClickListener {

                        // GA 이벤트 로그 추가
                        AnalyticsChipMapper.chipTextMap[view.text.toString()]?.let { eventInfo ->
                            AnalyticsEventLogger.logEvent(
                                eventName = eventInfo.eventName,
                                category = AnalyticsConstants.Category.ROOM_DETAIL,
                                action = eventInfo.action,
                                label = eventInfo.label ?: ""
                            )
                        }

                        showMemberStatDialog(roomId!!, key, getColor(R.color.red))
                    }
                }
                redViews.add(view)
            }
        }

        // 하얀색 칩 업데이트 및 추가
        difference.white.forEach { key ->
            viewMap[key]?.let { view ->
                view.setBackgroundResource(R.drawable.custom_select_chip_default)
                view.setTextColor(getColor(R.color.unuse_font))
                if (isChipClickable) {
                    view.setOnClickListener {

                        // GA 이벤트 로그 추가
                        AnalyticsChipMapper.chipTextMap[view.text.toString()]?.let { eventInfo ->
                            AnalyticsEventLogger.logEvent(
                                eventName = eventInfo.eventName,
                                category = AnalyticsConstants.Category.ROOM_DETAIL,
                                action = eventInfo.action,
                                label = eventInfo.label ?: ""
                            )
                        }

                        showMemberStatDialog(roomId!!, key, getColor(R.color.unuse_font))
                    }
                }
                whiteViews.add(view)
            }
        }

        // FlexboxLayout에서 모든 칩 제거
        flexboxLayout.removeAllViews()

        // 칩을 파란색, 빨간색, 하얀색 순으로 추가
        blueViews.forEach { flexboxLayout.addView(it) }
        redViews.forEach { flexboxLayout.addView(it) }
        whiteViews.forEach { flexboxLayout.addView(it) }
    }

    private fun showMemberStatDialog(
        roomId: Int,
        memberStatKey: String,
        chipColor: Int
    ) {
        // 기존 다이얼로그 닫기
        activeDialog?.dismiss()
        activeDialog = null

        // 데이터 요청
        viewModel.getRoomMemberStats(roomId, memberStatKey)

        lifecycleScope.launch {
            delay(50) // 데이터 로딩 대기

            viewModel.isLoading.observe(this@RoomDetailActivity) { isLoading ->
                if (isLoading) return@observe

                val memberList = viewModel.roomMemberStats.value ?: emptyList()

                val dialogBinding = DialogMemberStatBinding.inflate(layoutInflater)

                dialogBinding.tvStatTitle.text = translateMemberStatKey(memberStatKey)
                dialogBinding.tvStatTitle.setTextColor(chipColor)

                dialogBinding.rvMemberStat.apply {
                    layoutManager = LinearLayoutManager(this@RoomDetailActivity)
                    adapter = RoomMemberStatRVA(
                        context = this@RoomDetailActivity,
                        members = memberList,
                        memberStatKey = memberStatKey,
                        color = chipColor
                    )
                    addItemDecoration(
                        CustomDividerItemDecoration(
                            context = this@RoomDetailActivity,
                            heightDp = 1f,
                            marginStartDp = 16f,
                            marginEndDp = 16f
                        )
                    )
                }

                dialogBinding.tvClose.setOnClickListener {
                    activeDialog?.dismiss()
                    activeDialog = null
                }

                val dialog = AlertDialog.Builder(this@RoomDetailActivity)
                    .setView(dialogBinding.root)
                    .create()

                dialog.window?.let { window ->
                    window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    val layoutParams = window.attributes
                    layoutParams.dimAmount = 0.7f
                    window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                    window.attributes = layoutParams
                }

                dialog.setOnDismissListener {
                    viewModel.roomMemberStats.removeObservers(this@RoomDetailActivity)
                    viewModel.isLoading.removeObservers(this@RoomDetailActivity)
                    activeDialog = null
                }

                dialog.setCancelable(true)
                dialog.show()
                activeDialog = dialog
            }
        }
    }

    private fun translateMemberStatKey(key: String): String {
        return when (key) {
            "coolingIntensity" -> "에어컨"
            "callingStatus" -> "전화여부"
            "sleepingTime" -> "취침시간"
            "noiseSensitivity" -> "소음예민도"
            "wakeUpTime" -> "기상시간"
            "turnOffTime" -> "소등시간"
            "admissionYear" -> "학번"
            "mbti" -> "MBTI"
            "heatingIntensity" -> "히터"
            "drinkingFrequency" -> "음주빈도"
            "studyingStatus" -> "공부여부"
            "sharingStatus" -> "물건공유"
            "sleepingHabits" -> "잠버릇"
            "intimacy" -> "친밀도"
            "lifePattern" -> "생활패턴"
            "dormJoiningStatus" -> "합격여부"
            "cleannessSensitivity" -> "청결예민도"
            "personalities" -> "성격"
            "birthYear" -> "출생년도"
            "cleaningFrequency" -> "청소빈도"
            "smokingStatus" -> "흡연여부"
            "majorName" -> "학과"
            "gamingStatus" -> "게임여부"
            "eatingStatus" -> "섭취여부"
            else -> "알 수 없음"
        }
    }

    private fun updateFloatingButton() {
        val savedRoomId = cozyHomeViewModel.getSavedRoomId()

        with(binding.fabBnt) {
            if (savedRoomId == -1) {
                // 기본값인 경우 = 방이 없는 경우
                text = "방 참여요청"
                backgroundTintList = getColorStateList(R.color.main_blue)
                setTextColor(getColor(R.color.white))
            } else {
                // 방이 있는 경우
                text = "방 참여요청"
                backgroundTintList = getColorStateList(R.color.gray)
                setTextColor(getColor(R.color.white))
            }

            setOnClickListener {
                if (savedRoomId == -1) {
                    // 방이 없는 경우, 방 신청
                    applyForRoom()
                } else {
                    // 방이 있는 경우
                }
            }
        }
    }

    private fun applyForRoom() {
        // 방 신청 로직
        Log.d(TAG, " 방 신청 버튼 누름")
    }

    private fun setupBackButton() {
        binding.ivBack.setOnClickListener {
            finish()
        }
    }
}