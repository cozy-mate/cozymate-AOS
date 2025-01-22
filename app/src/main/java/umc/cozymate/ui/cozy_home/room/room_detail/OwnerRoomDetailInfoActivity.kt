package umc.cozymate.ui.cozy_home.room.room_detail

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import umc.cozymate.R
import umc.cozymate.data.model.response.room.GetRoomInfoResponse
import umc.cozymate.databinding.ActivityOwnerRoomDetailInfoBinding
import umc.cozymate.databinding.DialogMemberStatBinding
import umc.cozymate.ui.MainActivity
import umc.cozymate.ui.cozy_home.roommate.roommate_detail.RoommateDetailActivity
import umc.cozymate.ui.my_page.update_room.UpdateRoomInfoActivity
import umc.cozymate.ui.pop_up.PopupClick
import umc.cozymate.ui.pop_up.TwoButtonPopup
import umc.cozymate.ui.viewmodel.CozyHomeViewModel
import umc.cozymate.ui.viewmodel.FavoriteViewModel
import umc.cozymate.ui.viewmodel.JoinRoomViewModel
import umc.cozymate.ui.viewmodel.MakingRoomViewModel
import umc.cozymate.ui.viewmodel.RoommateDetailViewModel
import umc.cozymate.util.StatusBarUtil
import umc.cozymate.util.navigationHeight
import umc.cozymate.util.setStatusBarTransparent

// TODO: 방 수정, 방 나가기, 방 전환은 나중에(공개방/비공개방)
@AndroidEntryPoint
class OwnerRoomDetailInfoActivity : AppCompatActivity() {
    private val TAG = this.javaClass.simpleName
    private lateinit var binding: ActivityOwnerRoomDetailInfoBinding
    private val viewModel: RoomDetailViewModel by viewModels()
    private val cozyHomeViewModel: CozyHomeViewModel by viewModels()
    private val roommateDetailViewModel: RoommateDetailViewModel by viewModels()
    private val favoriteViewModel: FavoriteViewModel by viewModels()
    private val roomViewModel: MakingRoomViewModel by viewModels()
    private val joinRoomViewModel: JoinRoomViewModel by viewModels()
    private var roomId: Int? = 0
    private var managerMemberId: Int? = 0
    private var roomType: String = ""
    private var activeDialog: AlertDialog? = null // 현재 활성화된 다이얼로그 추적

    // 방 id는  Intent를 통해 불러옵니다
    companion object {
        const val ARG_ROOM_ID = "room_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOwnerRoomDetailInfoBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        this.setStatusBarTransparent()
        StatusBarUtil.updateStatusBarColor(this@OwnerRoomDetailInfoActivity, Color.WHITE)
        binding.main.setPadding(0, 0, 0, this.navigationHeight())

        // 더보기 버튼 설정
        var moreFlag = false
        binding.llMore.visibility = View.GONE
        binding.ivMore.setOnClickListener {
            if (!moreFlag) {
                binding.llMore.visibility = View.VISIBLE
                binding.llMore.bringToFront() // 우선순위 조정
                binding.clMid.requestDisallowInterceptTouchEvent(true) // RecyclerView의 터치 차단
                moreFlag = true
            } else {
                binding.llMore.visibility = View.GONE
                binding.clMid.requestDisallowInterceptTouchEvent(false)
                moreFlag = false
            }
        }
        binding.tvUpdateInfo.setOnClickListener {
            val intent = Intent(this@OwnerRoomDetailInfoActivity, UpdateRoomInfoActivity::class.java)
            intent.putExtra(UpdateRoomInfoActivity.ROOM_STATE, roomType)
            startActivity(intent)
        }
        // 뒤로가기 버튼
        binding.ivBack.setOnClickListener {
            finish()
        }
        // 방 id 불러오기
        roomId = intent.getIntExtra(ARG_ROOM_ID, -1)
        lifecycleScope.launch {
            viewModel.getOtherRoomInfo(roomId!!)
        }
        updateUserRoomInfo()
        // 방 나가기
        setQuitRoom(roomId!!)
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
                    tvRoomInfoCurrentNum.text =
                        "${roomInfo.arrivalMateNum}  /  ${roomInfo.maxMateNum}"
                    tvDormitoryName.text = roomInfo.dormitoryName

                    tvDormitoryRoomNum.text = "${roomInfo.maxMateNum}인실"
                    updateDifference(roomInfo.difference)
                    managerMemberId = roomInfo.managerMemberId
                    // 리사이클러 뷰 연결
                    rvRoomMemberList.apply {
                        layoutManager = LinearLayoutManager(this@OwnerRoomDetailInfoActivity)
                        adapter = RoomMemberListRVA(
                            roomInfo.mateDetailList,
                            roomInfo.managerNickname
                        ) { memberId ->
                            navigatorToRoommateDetail(memberId)
                        }
                    }
                }
            }
        }
    }

    // 방 나가기
    fun setQuitRoom(roomId: Int) {
        with(binding) {
            tvQuit.setOnClickListener {
                showQuitRoomPopup()
            }
        }
        // 방 삭제 옵저빙
        roomViewModel.roomQuitResult.observe(this) { result ->
            if (result.isSuccess) {
                loadMainActivity()
            } else {
                Toast.makeText(this, "방 나가기를 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 삭제 확인 팝업 띄우기
    fun showQuitRoomPopup() {
        val text = listOf("정말 방을 나가시나요?", "삭제하면 우리의 추억을 복구할 수 없어요!", "취소", "확인")
        val dialog = TwoButtonPopup(text, object : PopupClick {
            override fun rightClickFunction() {
                roomViewModel.quitRoom(roomId)
                val spf = getSharedPreferences("app_prefs", MODE_PRIVATE)
                spf.edit().putInt("room_id", 0)
            }
        }, true)
        dialog.show(supportFragmentManager, "roomDeletionPopup")
    }

    // 코지홈으로 화면 전환
    fun loadMainActivity() {
        val intent = Intent(this@OwnerRoomDetailInfoActivity, MainActivity::class.java)
        // 방 나가기 후에 상태변수를 설정해줍니다.
        intent.putExtra("isRoomExist", false)
        intent.putExtra("isRoomManager", false)
        startActivity(intent)
        this.finish()
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

            // 리사이클러 뷰 연결
            rvRoomMemberList.apply {
                layoutManager = LinearLayoutManager(this@OwnerRoomDetailInfoActivity)
                adapter = RoomMemberListRVA(
                    roomInfo.mateDetailList,
                    roomInfo.managerNickname
                ) { memberId ->
                    navigatorToRoommateDetail(memberId)
                }
            }
        }
    }

    // 룸메이트 상세 창
    private fun navigatorToRoommateDetail(memberId: Int) {
        lifecycleScope.launch {
            roommateDetailViewModel.getOtherUserDetailInfo(memberId)
            roommateDetailViewModel.otherUserDetailInfo.collectLatest { otherUserDetail ->
                val intent =
                    Intent(
                        this@OwnerRoomDetailInfoActivity,
                        RoommateDetailActivity::class.java
                    ).apply {
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
            /*when (hashtags.size) {
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
            Log.d(TAG, "tvHashtag3: ${tvHashtag3.text}, visibility: ${tvHashtag3.visibility}")*/
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
        var roomStatusText = ""
        if (type == "PUBLIC") {
            roomStatusText = "공개방이에요"
            roomType = "PUBLIC"
        } else {
            roomStatusText = "비공개방이에요"
            roomType = "PRIVATE"
        }
        //binding.tvRoomStatus.visibility = View.VISIBLE
        //binding.tvRoomStatus.text = roomStatusText
    }

    private fun updateRoomManager(isRoomManager: Boolean) {
        if (isRoomManager) {
            //binding.ivSetting.visibility = View.VISIBLE
        } else {
            //binding.ivSetting.visibility = View.GONE
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
        // 기존 다이얼로그 닫기 및 초기화
        activeDialog?.dismiss()
        activeDialog = null
        viewModel.roomMemberStats.removeObservers(this)
        viewModel.isLoading.removeObservers(this)

        // 로딩 로그
        Log.d(TAG, "Loading data for key: $memberStatKey")

        // 데이터 요청
        viewModel.getRoomMemberStats(roomId, memberStatKey)

        // 0.5초 딜레이 후 다이얼로그 표시
        lifecycleScope.launch {
            delay(50)

            viewModel.isLoading.observe(this@OwnerRoomDetailInfoActivity) { isLoading ->
                if (isLoading) {
                    Log.d(TAG, "Still loading for key: $memberStatKey")
                    return@observe
                }

                // 로딩이 끝난 후 데이터 확인
                val memberList = viewModel.roomMemberStats.value
                if (memberList.isNullOrEmpty()) {
                    Log.e(TAG, "No data available for key: $memberStatKey")
                    viewModel.roomMemberStats.removeObservers(this@OwnerRoomDetailInfoActivity)
                    viewModel.isLoading.removeObservers(this@OwnerRoomDetailInfoActivity)
                    return@observe
                }

                // 다이얼로그 생성
                if (activeDialog == null) {
                    Log.d(TAG, "Creating dialog for key: $memberStatKey")
                    val dialogBinding = DialogMemberStatBinding.inflate(layoutInflater)

                    // 다이얼로그 생성 시 스타일 적용 없이 기존 방식 유지
                    val dialog = AlertDialog.Builder(this@OwnerRoomDetailInfoActivity)
                        .setView(dialogBinding.root)
                        .create()

                    dialogBinding.tvStatTitle.text = translateMemberStatKey(memberStatKey)
                    dialogBinding.tvStatTitle.setTextColor(chipColor)

                    dialogBinding.rvMemberStat.apply {
                        layoutManager = LinearLayoutManager(this@OwnerRoomDetailInfoActivity)
                        adapter = RoomMemberStatRVA(
                            context = this@OwnerRoomDetailInfoActivity,
                            members = memberList,
                            memberStatKey = memberStatKey,
                            color = chipColor
                        )
                        // 디바이더 추가
                        addItemDecoration(
                            CustomDividerItemDecoration(
                                context = this@OwnerRoomDetailInfoActivity,
                                heightDp = 1f, // 1dp
                                marginStartDp = 16f,
                                marginEndDp = 16f
                            )
                        )
                    }

                    dialogBinding.tvClose.setOnClickListener {
                        dialog.dismiss()
                        activeDialog = null
                    }

                    dialog.setOnDismissListener {
                        // 다이얼로그 닫힐 때 관찰자 제거
                        viewModel.roomMemberStats.removeObservers(this@OwnerRoomDetailInfoActivity)
                        viewModel.isLoading.removeObservers(this@OwnerRoomDetailInfoActivity)
                        activeDialog = null
                    }

                    dialog.setCancelable(true)
                    dialog.show()
                    activeDialog = dialog
                }
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
}