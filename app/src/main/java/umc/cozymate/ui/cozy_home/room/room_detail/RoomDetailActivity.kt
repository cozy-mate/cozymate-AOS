package umc.cozymate.ui.cozy_home.room_detail

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import umc.cozymate.R
import umc.cozymate.data.model.response.room.GetRoomInfoResponse
import umc.cozymate.databinding.ActivityCozyRoomDetailInfoBinding
import umc.cozymate.databinding.DialogMemberStatBinding
import umc.cozymate.ui.cozy_home.room.room_detail.RoomDetailViewModel
import umc.cozymate.ui.cozy_home.room.room_detail.RoomMemberListRVA
import umc.cozymate.ui.cozy_home.room.room_detail.RoomMemberStatRVA
import umc.cozymate.ui.cozy_home.roommate.roommate_detail.RoommateDetailActivity
import umc.cozymate.ui.cozy_home.roommate.roommate_detail.RoommateDetailViewModel
import umc.cozymate.ui.message.WriteMessageActivity
import umc.cozymate.ui.viewmodel.CozyHomeViewModel
import umc.cozymate.ui.viewmodel.FavoriteViewModel
import umc.cozymate.util.StatusBarUtil

// 방 생성 후, 내방 컴포넌트 클릭 후 화면 전환할 때 room_id를 받아오도록 구현해놨습니다. 이해 안되는거 있음 얘기해주세요
@AndroidEntryPoint
class RoomDetailActivity : AppCompatActivity() {
    private val TAG = this.javaClass.simpleName
    private lateinit var binding: ActivityCozyRoomDetailInfoBinding
    private val viewModel: RoomDetailViewModel by viewModels()
    private val cozyHomeViewModel: CozyHomeViewModel by viewModels()
    private val roommateDetailViewModel: RoommateDetailViewModel by viewModels()
    private val favoriteViewModel: FavoriteViewModel by viewModels()
    private var roomId: Int? = 0
    private var managerMemberId: Int? = 0

    // 방 id는  Intent를 통해 불러옵니다
    companion object {
        const val ARG_ROOM_ID = "room_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCozyRoomDetailInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        StatusBarUtil.updateStatusBarColor(this@RoomDetailActivity, Color.WHITE)

        getRoomId()

        val spf = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val savedRoomId = spf.getInt("room_id", -2)

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
            }
            Log.d(TAG, "Received room ID: $roomId")
        }
    }

    private fun updateUserRoomInfo() {
        lifecycleScope.launch {
            viewModel.otherRoomDetailInfo.collectLatest { roomInfo ->
                with(binding) {
                    tvRoomName.text = roomInfo.name
                    updateProfileImage(roomInfo.persona)
                    updateRoomStatus(roomInfo.roomType)
                    updateRoomManager(roomInfo.isRoomManager)
                    tvRoomMatch.text = "방 평균 일치율 - %"
                    ivChat.visibility = View.GONE
                    ivLike.visibility = View.GONE
                    tvRoomInfoCurrentNum.text =
                        "${roomInfo.arrivalMateNum}  /  ${roomInfo.maxMateNum}"
                    tvDormitoryName.text = roomInfo.dormitoryName

                    tvDormitoryRoomNum.text = "${roomInfo.maxMateNum}인실"
                    updateDifference(roomInfo.difference)
                    managerMemberId = roomInfo.managerMemberId
                    fabRequestEnterRoom.visibility = View.GONE
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
        with(binding) {
            tvRoomName.text = roomInfo.name
            updateProfileImage(roomInfo.persona)
            updateHashtags(roomInfo.hashtagList)
            tvRoomMatch.text = "방 평균 일치율 ${roomInfo.equality}%"
            tvRoomInfoCurrentNum.text = "${roomInfo.arrivalMateNum}  /  ${roomInfo.maxMateNum}"
            tvDormitoryName.text = roomInfo.dormitoryName
            tvDormitoryRoomNum.text = "${roomInfo.maxMateNum}인실"
            updateDifference(roomInfo.difference)
            managerMemberId = roomInfo.managerMemberId

            if (roomInfo.favoriteId == 0) {
                ivLike.setImageResource(R.drawable.ic_heart)
            } else {
                ivLike.setImageResource(R.drawable.ic_heartfull)
            }

            ivLike.setOnClickListener {
                handleLikeClick(roomInfo)
            }

            fabRequestEnterRoom.visibility = View.GONE
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
            when (hashtags.size) {
                0 -> {
                    tvHashtag1.visibility = View.GONE
                    tvHashtag2.visibility = View.GONE
                    tvHashtag3.visibility = View.GONE
                }

                1 -> {
                    tvHashtag1.text = "#${hashtags[0]}"
                    tvHashtag2.visibility = View.GONE
                    tvHashtag3.visibility = View.GONE
                }

                2 -> {
                    tvHashtag1.text = "#${hashtags[0]}"
                    tvHashtag2.text = "#${hashtags[1]}"
                    tvHashtag3.visibility = View.GONE
                }

                3 -> {
                    tvHashtag1.text = "#${hashtags[0]}"
                    tvHashtag2.text = "#${hashtags[1]}"
                    tvHashtag3.text = "#${hashtags[2]}"
                }
            }
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

    private fun handleLikeClick(roomInfo: GetRoomInfoResponse.Result) {
        if (roomInfo.favoriteId == 0) {
            binding.ivLike.setImageResource(R.drawable.ic_heartfull)
            lifecycleScope.launch {
                favoriteViewModel.sendFavoriteRoom(roomInfo.roomId)
                Log.d(TAG, "방 찜하기 요청, 방번호 : ${roomInfo.roomId}")
            }
        } else {
            binding.ivLike.setImageResource(R.drawable.ic_heart)
            lifecycleScope.launch {
                favoriteViewModel.deleteFavoriteRoomMember(roomInfo.roomId)
                Log.d(TAG, "방 찜하기 삭제, 방번호 : ${roomInfo.roomId}")
            }
        }
    }

    //    // difference 업데이트
//    private fun updateDifference(difference: GetRoomInfoResponse.Result.Difference) {
//        val viewMap = mapOf(
//            "airConditioningIntensity" to binding.selectAc,
//            "isPhoneCall" to binding.selectCall,
//            "sleepingTime" to binding.selectSleep,
//            "noiseSensitivity" to binding.selectNoise,
//            "wakeUpTime" to binding.selectWake,
//            "turnOffTime" to binding.selectLightOff,
//            "admissionYear" to binding.selectNumber,
//            "mbti" to binding.selectMbti,
//            "heatingIntensity" to binding.selectHeater,
//            "drinkingFrequency" to binding.selectDrinkFrequency,
//            "studying" to binding.selectStudy,
//            "canShare" to binding.selectShare,
//            "sleepingHabit" to binding.selectSleepHabit,
//            "intimacy" to binding.selectFriendly,
//            "lifePattern" to binding.selectLivingPattern,
//            "acceptance" to binding.selectAcceptance,
//            "cleanSensitivity" to binding.selectClean,
//            "personality" to binding.selectPersonality,
//            "birthYear" to binding.selectBirth,
//            "cleaningFrequency" to binding.selectCleanFrequency,
//            "smoking" to binding.selectSmoke,
//            "majorName" to binding.selectMajor,
//            "isPlayGame" to binding.selectGame,
//            "intake" to binding.selectIntake
//        )
//
//        val flexboxLayout = binding.chips1
//
//        // Step 1: 모든 뷰 초기화 (기본 색상)
//        viewMap.values.forEach { view ->
//            view.setBackgroundResource(R.drawable.custom_select_chip_default)
//            view.setTextColor(getColor(R.color.unuse_font))
//        }
//
//        // Step 2: 파란색, 빨간색, 하얀색으로 재정렬
//        val blueViews = mutableListOf<View>()
//        val redViews = mutableListOf<View>()
//        val whiteViews = mutableListOf<View>()
//
//        difference.blue.forEach { key ->
//            viewMap[key]?.let { view ->
//                view.setBackgroundResource(R.drawable.custom_select_chip_blue)
//                view.setTextColor(getColor(R.color.main_blue))
//                blueViews.add(view)
//            }
//        }
//
//        difference.red.forEach { key ->
//            viewMap[key]?.let { view ->
//                view.setBackgroundResource(R.drawable.custom_select_chip_red)
//                view.setTextColor(getColor(R.color.red))
//                redViews.add(view)
//            }
//        }
//
//        difference.white.forEach { key ->
//            viewMap[key]?.let { view ->
//                view.setBackgroundResource(R.drawable.custom_select_chip_default)
//                view.setTextColor(getColor(R.color.unuse_font))
//                whiteViews.add(view)
//            }
//        }
//
//        // Step 3: FlexboxLayout에서 모든 뷰 제거 후 다시 추가
//        flexboxLayout.removeAllViews()
//        blueViews.forEach { flexboxLayout.addView(it) }
//        redViews.forEach { flexboxLayout.addView(it) }
//        whiteViews.forEach { flexboxLayout.addView(it) }
//    }

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
        }

        // 파란색 칩 업데이트 및 클릭 리스너
        difference.blue.forEach { key ->
            viewMap[key]?.let { view ->
                view.setBackgroundResource(R.drawable.custom_select_chip_blue)
                view.setTextColor(getColor(R.color.main_blue))
                view.setOnClickListener {
                    showMemberStatDialog(roomId!!, key, getColor(R.color.main_blue))
                }
            }
        }

        // 빨간색 칩 업데이트 및 클릭 리스너
        difference.red.forEach { key ->
            viewMap[key]?.let { view ->
                view.setBackgroundResource(R.drawable.custom_select_chip_red)
                view.setTextColor(getColor(R.color.red))
                view.setOnClickListener {
                    showMemberStatDialog(roomId!!, key, getColor(R.color.red))
                }
            }
        }
    }

    private fun showMemberStatDialog(roomId: Int, memberStatKey: String, chipColor: Int) {
        // ViewModel에서 데이터 호출
        viewModel.getRoomMemberStats(roomId, memberStatKey)

        // ViewModel 데이터 관찰
        viewModel.roomMemberStats.observe(this) { memberList ->
            if (memberList != null && memberList.isNotEmpty()) {
                val dialogBinding = DialogMemberStatBinding.inflate(layoutInflater)
                val dialog = AlertDialog.Builder(this)
                    .setView(dialogBinding.root)
                    .create()

                // 다이얼로그 제목 설정
                dialogBinding.tvStatTitle.text = memberStatKey
                dialogBinding.tvStatTitle.setTextColor(chipColor) // 칩 색상 그대로 반영

                // RecyclerView 설정
                dialogBinding.rvMemberStat.apply {
                    layoutManager = LinearLayoutManager(this@RoomDetailActivity)
                    adapter = RoomMemberStatRVA(
                        context = this@RoomDetailActivity,
                        members = memberList,
                        color = chipColor
                    )
                }

                // 닫기 버튼 설정
                dialogBinding.tvClose.setOnClickListener { dialog.dismiss() }

                // 다이얼로그 외부 터치로 닫기 가능
                dialog.setCancelable(true)
                dialog.show()
            } else {
                Log.e(TAG, "No data available for key: $memberStatKey")
            }
        }
    }

    private fun updateFloatingButton() {
        val savedRoomId = cozyHomeViewModel.getSavedRoomId()

        with(binding.fabRequestEnterRoom) {
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