package umc.cozymate.ui.cozy_home.room_detail

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import umc.cozymate.R
import umc.cozymate.data.model.response.room.GetRoomInfoResponse
import umc.cozymate.databinding.ActivityRoomDetailBinding
import umc.cozymate.databinding.DialogMemberStatBinding
import umc.cozymate.ui.viewmodel.JoinRoomViewModel
import umc.cozymate.ui.cozy_home.room.room_detail.CustomDividerItemDecoration
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
import umc.cozymate.util.StatusBarUtil

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
    private var roomId: Int? = 0
    private var managerMemberId: Int? = 0
    private var activeDialog: AlertDialog? = null // 현재 활성화된 다이얼로그 추적
    private var isFavorite: Boolean = false // 찜 상태를 추적하는 변수 추가

    // 방 id는  Intent를 통해 불러옵니다
    companion object {
        const val ARG_ROOM_ID = "room_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoomDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        StatusBarUtil.updateStatusBarColor(this@RoomDetailActivity, Color.WHITE)

        getRoomId()
        observeFavoriteState()

        val spf = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val savedRoomId = spf.getInt("room_id", -2)

        lifecycleScope.launch {
            viewModel.otherRoomDetailInfo.collectLatest { roomInfo ->
                updateFavoriteButton(roomInfo.roomId, roomInfo.favoriteId)
            }
        }

        if (savedRoomId == roomId) {
            updateUserRoomInfo()
        } else {
            getRoomDetailInfo()
        }

        updateFloatingButton()

        setupBackButton()

        binding.ivChat.setOnClickListener {
            val intent: Intent = Intent(this, WriteMessageActivity::class.java)
            intent.putExtra("recipientId", managerMemberId)
            startActivity(intent)
        }
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

    private fun updateUserRoomInfo() {
        lifecycleScope.launch {
            Log.d(TAG, "updateUserRoomInfo 진입")
            viewModel.otherRoomDetailInfo.collectLatest { roomInfo ->
                with(binding) {
                    tvRoomName.text = roomInfo.name
                    updateProfileImage(roomInfo.persona)
                    updateRoomStatus(roomInfo.roomType)
                    updateRoomManager(roomInfo.isRoomManager)
                    tvRoomMatch.text = "방 평균 일치율 - %"
                    ivLike.visibility = View.INVISIBLE
                    ivExit.visibility = View.VISIBLE
                    fabBnt.visibility = View.GONE
                    tvRoomInfoCurrentNum.text =
                        "${roomInfo.arrivalMateNum}  /  ${roomInfo.maxMateNum}"
                    tvDormitoryName.text = roomInfo.dormitoryName

                    tvDormitoryRoomNum.text = "${roomInfo.maxMateNum}인실"
                    updateDifference(roomInfo.difference)
                    managerMemberId = roomInfo.managerMemberId
                    exitButton(roomInfo.roomId)
                    // 리사이클러 뷰 연결
                    rvRoomMemberList.apply {
                        layoutManager = LinearLayoutManager(this@RoomDetailActivity)
                        adapter = RoomMemberListRVA(
                            roomInfo.mateDetailList,
                            roomInfo.managerNickname
                        ) { memberId ->
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
                val text = listOf("방을 나가시겠습니까?", "", "취소", "확인")
                val dialog = TwoButtonPopup(text, object : PopupClick {
                    override fun rightClickFunction() {
                        // 방 나가기 로직 실행
                        roomViewModel.quitRoom(roomId)

                        roomViewModel.roomQuitResult.observe(this@RoomDetailActivity) { result ->
                            if (result.isSuccess) {
                                Toast.makeText(
                                    this@RoomDetailActivity,
                                    "방을 성공적으로 나갔습니다.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                spf.edit().putInt("room_id", 0).apply()
                                finish() // 방 나가기 성공 시 액티비티 종료
                            } else {
                                Toast.makeText(
                                    this@RoomDetailActivity,
                                    "방 나가기에 실패했습니다. 다시 시도해주세요.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }, true) // true로 설정하면 다이얼로그 외부 클릭 시 닫히지 않음
                dialog.show(supportFragmentManager, "QuitRoomPopup")
            }
        }
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
        updateProfileImage(roomInfo.persona)
        updateHashtags(roomInfo.hashtagList)
        with(binding) {
            tvRoomName.text = roomInfo.name
            tvRoomMatch.text = "방 평균 일치율 ${roomInfo.equality}%"
            tvRoomInfoCurrentNum.text = "${roomInfo.arrivalMateNum}  /  ${roomInfo.maxMateNum}"
            tvDormitoryName.text = roomInfo.dormitoryName
            tvDormitoryRoomNum.text = "${roomInfo.maxMateNum}인실"
            updateDifference(roomInfo.difference)
            managerMemberId = roomInfo.managerMemberId

            updateOtherRoomFab(roomInfo.roomId)
            // 초기 찜 상태 설정
            isFavorite = roomInfo.favoriteId != 0
            updateFavoriteIcon(isFavorite)

            // 리사이클러 뷰 연결
            rvRoomMemberList.apply {
                layoutManager = LinearLayoutManager(this@RoomDetailActivity)
                adapter = RoomMemberListRVA(
                    roomInfo.mateDetailList,
                    roomInfo.managerNickname
                ) { memberId ->
                    navigatorToRoommateDetail(memberId)
                }
            }
        }
    }

    private fun updateFavoriteButton(roomId: Int, favoriteId: Int) {
        binding.ivLike.setOnClickListener {
            lifecycleScope.launch {
                // 현재 찜 상태 확인
                val isCurrentlyFavorite = favoriteId != 0

                if (isCurrentlyFavorite) {
                    // 찜 해제 요청: favoriteId 사용
                    favoriteViewModel.toggleRoomFavorite(favoriteId, isCurrentlyFavorite)
                } else {
                    // 찜 요청: roomId 사용
                    favoriteViewModel.toggleRoomFavorite(roomId, isCurrentlyFavorite)
                }

                // UI 변경을 약간 늦추기 위해 딜레이 추가
                delay(200)

                // 방 정보를 새로고침하여 정확한 상태 반영
                viewModel.getOtherRoomInfo(roomId)

            }
        }
    }

    private fun observeFavoriteState() {
        favoriteViewModel.roomFavoriteState.observe(this) { isFavorite ->
            updateFavoriteIcon(isFavorite)
        }
    }

    private fun updateFavoriteIcon(favorite: Boolean) {
        binding.ivLike.setImageResource(
            if (favorite) R.drawable.ic_heartfull else R.drawable.ic_heart
        )
        binding.ivLike.setColorFilter(
            if (favorite) getColor(R.color.red) else getColor(R.color.unuse_font)
        )
    }

    private fun updateOtherRoomFab(roomId: Int) {
        val spf = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val mbti = spf.getString("user_mbti", null)
        val savedRoomId = spf.getInt("room_id", -2)
        if (mbti!!.isNotEmpty()) {
            // 라이프스타일 입력을 한 경우
            if (savedRoomId == 0 || savedRoomId == -2) {
                // 내 방이 없는 경우
                roomViewModel.getPendingRoom(roomId)
                roomViewModel.pendingRoom.observe(this) { isPending ->
                    if (isPending) {
                        // 참여 요청한 방인 경우 (아직 승인 대기 중)
                        with(binding) {
                            fabBnt.text = "방 참여요청 취소"
                            fabBnt.setBackgroundTintList(getColorStateList(R.color.color_box))
                            fabBnt.setTextColor(getColor(R.color.main_blue))
                            fabBnt.setOnClickListener {
                                lifecycleScope.launch {
                                    roomViewModel.deleteRoomJoin(roomId)
                                    delay(300)
                                    roomViewModel.getPendingRoom(roomId)
                                    recreate()
                                }
                            }
                        }
                    } else {
                        // 참여 요청한 방이 아닌 경우 (참여 요청하기)
                        with(binding) {
                            fabBnt.text = "방 참여요청"
                            fabBnt.setBackgroundTintList(getColorStateList(R.color.main_blue))
                            fabBnt.setTextColor(getColor(R.color.white))
                            fabBnt.setOnClickListener {
                                lifecycleScope.launch {
                                    joinRoomViewModel.joinRoom(roomId)
                                    delay(300)
                                    roomViewModel.getPendingRoom(roomId)
                                    recreate()
                                }
                            }
                        }
                    }
                }
            } else {
                // 내 방이 있는 경우
                with(binding) {
                    fabBnt.text = "방 참여 요청"
                    fabBnt.backgroundTintList =
                        android.content.res.ColorStateList.valueOf(Color.parseColor("#C4C4C4"))
                    fabBnt.setTextColor(getColor(R.color.white))
                    fabBnt.isEnabled = false
                }
            }
        } else {
            // 라이프스타일 입력을 안한 경우
            inputLifeStyle()
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
                val intent = Intent(this@RoomDetailActivity, RoommateOnboardingActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun navigatorToRoommateDetail(memberId: Int) {
        lifecycleScope.launch {
            roommateDetailViewModel.getOtherUserDetailInfo(memberId)
            roommateDetailViewModel.otherUserDetailInfo.collectLatest { otherUserDetail ->
                val intent =
                    Intent(this@RoomDetailActivity, RoommateDetailActivity::class.java).apply {
                        putExtra("other_user_detail", otherUserDetail)
                    }
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
            Log.d(TAG, "tvHashtag1: ${tvHashtag1.text}, visibility: ${tvHashtag1.visibility}")
            Log.d(TAG, "tvHashtag2: ${tvHashtag2.text}, visibility: ${tvHashtag2.visibility}")
            Log.d(TAG, "tvHashtag3: ${tvHashtag3.text}, visibility: ${tvHashtag3.visibility}")
        }
    }

    // 방 프로필 이미지
    private fun updateProfileImage(persona: Int) {
        val profileImageResId = when (persona) {
            1 -> R.drawable.character_id_1
            2 -> R.drawable.character_id_2
            3 -> R.drawable.character_id_3
            4 -> R.drawable.character_id_4
            5 -> R.drawable.character_id_5
            6 -> R.drawable.character_id_6
            7 -> R.drawable.character_id_7
            8 -> R.drawable.character_id_8
            9 -> R.drawable.character_id_9
            10 -> R.drawable.character_id_10
            11 -> R.drawable.character_id_11
            12 -> R.drawable.character_id_12
            13 -> R.drawable.character_id_13
            14 -> R.drawable.character_id_14
            15 -> R.drawable.character_id_15
            16 -> R.drawable.character_id_16
            else -> R.drawable.character_id_1
        }
        binding.ivRoomCharacter.setImageResource(profileImageResId)
    }

    private fun updateRoomStatus(type: String) {
        val roomStatusText = if (type == "PUBLIC") {
            "공개방이에요"
        } else {
            "비공개방이에요"
        }
        binding.tvRoomStatus.visibility = View.VISIBLE
        binding.tvRoomStatus.text = roomStatusText
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
            "airConditioningIntensity" to binding.selectAc,
            "isPhoneCall" to binding.selectCall,
            "sleepingTime" to binding.selectSleep,
            "noiseSensitivity" to binding.selectNoise,
            "wakeUpTime" to binding.selectWake,
            "turnOffTime" to binding.selectLightOff,
            "admissionYear" to binding.selectNumber,
            "mbti" to binding.selectMbti,
            "heatingIntensity" to binding.selectHeater,
            "drinkingFrequency" to binding.selectDrinkFrequency,
            "studying" to binding.selectStudy,
            "canShare" to binding.selectShare,
            "sleepingHabit" to binding.selectSleepHabit,
            "intimacy" to binding.selectFriendly,
            "lifePattern" to binding.selectLivingPattern,
            "acceptance" to binding.selectAcceptance,
            "cleanSensitivity" to binding.selectClean,
            "personality" to binding.selectPersonality,
            "birthYear" to binding.selectBirth,
            "cleaningFrequency" to binding.selectCleanFrequency,
            "smoking" to binding.selectSmoke,
            "majorName" to binding.selectMajor,
            "isPlayGame" to binding.selectGame,
            "intake" to binding.selectIntake
        )

        val flexboxLayout = binding.chips1

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

        // 파란색 칩 업데이트 및 추가
        difference.blue.forEach { key ->
            viewMap[key]?.let { view ->
                view.setBackgroundResource(R.drawable.custom_select_chip_blue)
                view.setTextColor(getColor(R.color.main_blue))
                view.setOnClickListener {
                    showMemberStatDialog(roomId!!, key, getColor(R.color.main_blue))
                }
                blueViews.add(view)
            }
        }

        // 빨간색 칩 업데이트 및 추가
        difference.red.forEach { key ->
            viewMap[key]?.let { view ->
                view.setBackgroundResource(R.drawable.custom_select_chip_red)
                view.setTextColor(getColor(R.color.red))
                view.setOnClickListener {
                    showMemberStatDialog(roomId!!, key, getColor(R.color.red))
                }
                redViews.add(view)
            }
        }

        // 하얀색 칩 업데이트 및 추가
        difference.white.forEach { key ->
            viewMap[key]?.let { view ->
                view.setBackgroundResource(R.drawable.custom_select_chip_default)
                view.setTextColor(getColor(R.color.unuse_font))
                view.setOnClickListener {
                    showMemberStatDialog(roomId!!, key, getColor(R.color.unuse_font))
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

    private fun showMemberStatDialog(roomId: Int, memberStatKey: String, chipColor: Int) {
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
            "airConditioningIntensity" -> "에어컨"
            "isPhoneCall" -> "전화여부"
            "sleepingTime" -> "취침시간"
            "noiseSensitivity" -> "소음예민도"
            "wakeUpTime" -> "기상시간"
            "turnOffTime" -> "소등시간"
            "admissionYear" -> "학번"
            "mbti" -> "MBTI"
            "heatingIntensity" -> "히터"
            "drinkingFrequency" -> "음주빈도"
            "studying" -> "공부여부"
            "canShare" -> "물건공유"
            "sleepingHabit" -> "잠버릇"
            "intimacy" -> "친밀도"
            "lifePattern" -> "생활패턴"
            "acceptance" -> "합격여부"
            "cleanSensitivity" -> "청결예민도"
            "personality" -> "성격"
            "birthYear" -> "출생년도"
            "cleaningFrequency" -> "청소빈도"
            "smoking" -> "흡연여부"
            "majorName" -> "학과"
            "isPlayGame" -> "게임여부"
            "intake" -> "섭취여부"
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