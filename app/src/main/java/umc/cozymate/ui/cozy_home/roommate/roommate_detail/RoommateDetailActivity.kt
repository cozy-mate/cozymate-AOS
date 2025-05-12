package umc.cozymate.ui.cozy_home.roommate.roommate_detail

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.google.firebase.analytics.logEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import umc.cozymate.R
import umc.cozymate.data.model.response.member.stat.GetMemberDetailInfoResponse
import umc.cozymate.data.model.response.roommate.Detail
import umc.cozymate.data.model.response.roommate.Info
import umc.cozymate.databinding.ActivityRoommateDetailBinding
import umc.cozymate.databinding.ItemRoommateDetailListBinding
import umc.cozymate.databinding.ItemRoommateDetailTableBinding
import umc.cozymate.ui.message.WriteMessageActivity
import umc.cozymate.ui.pop_up.PopupClick
import umc.cozymate.ui.pop_up.ReportPopup
import umc.cozymate.ui.roommate.RoommateOnboardingActivity
import umc.cozymate.ui.roommate.data_class.UserInfo
import umc.cozymate.ui.viewmodel.FavoriteViewModel
import umc.cozymate.ui.viewmodel.MakingRoomViewModel
import umc.cozymate.ui.viewmodel.ReportViewModel
import umc.cozymate.ui.viewmodel.RoomDetailViewModel
import umc.cozymate.ui.viewmodel.RoommateDetailViewModel
import umc.cozymate.util.CharacterUtil
import umc.cozymate.util.PreferencesUtil
import umc.cozymate.util.SnackbarUtil
import umc.cozymate.util.StatusBarUtil
import umc.cozymate.util.navigationHeight
import umc.cozymate.util.setStatusBarTransparent

@AndroidEntryPoint
class RoommateDetailActivity : AppCompatActivity() {

    private val TAG = this.javaClass.simpleName
    private lateinit var binding: ActivityRoommateDetailBinding
    private val viewModel: RoommateDetailViewModel by viewModels()
    private var memberId: Int = -1
    private var favoriteId: Int = 0
    private var otherUserDetail: GetMemberDetailInfoResponse.Result? = null
    private val makingRoomViewModel: MakingRoomViewModel by viewModels()
    private val reportViewModel : ReportViewModel by viewModels()
    private var isFavorite: Boolean = false // 찜 상태
    private val favoriteViewModel: FavoriteViewModel by viewModels()
    private val roommateDetailViewModel: RoommateDetailViewModel by viewModels()
    private val roomDetailViewModel : RoomDetailViewModel by viewModels()
    val firebaseAnalytics = Firebase.analytics

    private var isRoommateRequested: Boolean = false  // 버튼 상태를 관리할 변수
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoommateDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.setStatusBarTransparent()
        window.navigationBarColor = Color.WHITE
        StatusBarUtil.updateStatusBarColor(this@RoommateDetailActivity, Color.WHITE)
        binding.main.setPadding(0, 0, 0, this.navigationHeight())

        // intent로 사용자 정보 전달
//        otherUserDetail = intent.getParcelableExtra("other_user_detail")
//        Log.d(TAG, "Received user detail: $otherUserDetail")
        otherUserDetail = intent.getParcelableExtra("other_user_detail")
        if (otherUserDetail == null) {
            Log.e(TAG, "intent에서 other_user_detail이 null입니다.")
            finish()
            return
        }
        Log.d(TAG, "Received user detail: $otherUserDetail")

        val spf = getSharedPreferences(PreferencesUtil.PREFS_NAME, Context.MODE_PRIVATE)
        val savedMemberId = spf.getInt("user_member_id", -1)
        if (otherUserDetail!!.memberDetail.memberId == savedMemberId) {
            val intent = Intent(this, RoommateMyDetailActivity::class.java)
            startActivity(intent)
            finish() // 현재 액티비티 종료
            return
        }

        val userDetail = getUserDetailFromPreferences()

        memberId = otherUserDetail?.memberDetail?.memberId!!
        favoriteId = otherUserDetail?.favoriteId ?: 0

        otherUserDetail.let {
            updateUI(it!!)
            setupFAB(it)
        }

        selectListView(otherUserDetail!!)
//        setUpListeners(userDetail!!)
        if (userDetail != null) {
            setUpListeners(userDetail)
        } else {
            Log.e(TAG, "userDetail이 null입니다.")
            SnackbarUtil.showCustomSnackbar(
                context = this,
                message = "사용자 정보가 부족하여 일부 기능이 제한됩니다.",
                iconType = SnackbarUtil.IconType.NO,
                anchorView = binding.root,
                extraYOffset = 20
            )
        }

        observeMemberInfo()
        setupFavoriteButton()

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }


    }

    private fun getUserDetailFromPreferences(): GetMemberDetailInfoResponse.Result? {
        return try {
//            val nickname = PreferencesUtil.getString(this, PreferencesUtil.KEY_USER_NICKNAME, "")
//            val birthday = PreferencesUtil.getString(this, PreferencesUtil.KEY_USER_BIRTHDAY, "")
//            val majorName = PreferencesUtil.getString(this, PreferencesUtil.KEY_USER_MAJOR_NAME, "")
//            val admissionYear = PreferencesUtil.getString(this, PreferencesUtil.KEY_USER_ADMISSION_YEAR, "")

//            if (nickname.isNullOrEmpty() || birthday.isNullOrEmpty() || majorName.isNullOrEmpty() || admissionYear.isNullOrEmpty()) {
//                return null
//            }
            val nickname = PreferencesUtil.getString(this, PreferencesUtil.KEY_USER_NICKNAME, "") ?: "샘플닉네임"
            val birthday = PreferencesUtil.getString(this, PreferencesUtil.KEY_USER_BIRTHDAY, "") ?: "2000-01-01"
            val majorName = PreferencesUtil.getString(this, PreferencesUtil.KEY_USER_MAJOR_NAME, "") ?: "컴퓨터공학과"
            val admissionYear = PreferencesUtil.getString(this, PreferencesUtil.KEY_USER_ADMISSION_YEAR, "") ?: "21"


            val spf = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            val wakeUpTime = spf.getInt("user_wakeUpTime", -1)

            val sampleDetail = GetMemberDetailInfoResponse.Result.MemberStatDetail(
                admissionYear = admissionYear.ifEmpty { "21" },
                numOfRoommate = 2,
                dormName = "기숙사A동",
                dormJoiningStatus = "합격",
                wakeUpTime = if(wakeUpTime == -1) 8 else wakeUpTime,
                sleepingTime = 1,
                turnOffTime = 13,
                smokingStatus = "비흡연",
                sleepingHabits = listOf("잠꼬대", "코골이"),
                coolingIntensity = "약하게 틀어요",
                heatingIntensity = "세게 틀어요",
                lifePattern = "아침형 인간",
                intimacy = "완전 친하게 지내요",
                sharingStatus = "칫솔만 아니면 돼요",
                gamingStatus = "키보드 채팅정도만 쳐요",
                callingStatus = "급한 전화만 해요",
                studyingStatus = "시험기간 때만 해요",
                eatingStatus = "간단한 간식정도만 먹어요",
                cleannessSensitivity = "예민해요",
                noiseSensitivity = "예민해요",
                cleaningFrequency = "일주일에 한 번 해요",
                drinkingFrequency = "일주일에 한 두번 마셔요",
                personalities = listOf("깔끔해요", "느긋해요"),
                mbti = "ISTJ",
                selfIntroduction = "안녕하세요! 샘플 데이터에요!."
            )

            return if (wakeUpTime == -1) {
                binding.lyGoToLifestyle.visibility = View.VISIBLE
                GetMemberDetailInfoResponse.Result(
                    memberDetail = GetMemberDetailInfoResponse.Result.MemberDetail(
                        nickname = nickname,
                        birthday = birthday,
                        universityName = PreferencesUtil.getString(this, PreferencesUtil.KEY_USER_UNIVERSITY_NAME, "") ?: "",
                        majorName = PreferencesUtil.getString(this, PreferencesUtil.KEY_USER_MAJOR_NAME, "") ?: "",
                        gender = PreferencesUtil.getString(this, "user_gender", "") ?: "",
                        memberId = getSharedPreferences(PreferencesUtil.PREFS_NAME, Context.MODE_PRIVATE).getInt("user_member_id", 0),
                        universityId = 1,
                        persona = 1
                    ),
                    memberStatDetail = sampleDetail,
                    equality = 0,
                    roomId = 0,
                    favoriteId = 0,
                    hasRequestedRoomEntry = false
                )
            }
            else {
                GetMemberDetailInfoResponse.Result(
                    memberDetail = GetMemberDetailInfoResponse.Result.MemberDetail(
                        nickname = nickname,
                        birthday = birthday,
                        universityName = PreferencesUtil.getString(this, PreferencesUtil.KEY_USER_UNIVERSITY_NAME, "") ?: "",
                        majorName = PreferencesUtil.getString(this, PreferencesUtil.KEY_USER_MAJOR_NAME, "") ?: "",
                        gender = PreferencesUtil.getString(this, "user_gender", "") ?: "",
                        memberId = getSharedPreferences(PreferencesUtil.PREFS_NAME, Context.MODE_PRIVATE).getInt("user_member_id", 0),
                        universityId = 1,
                        persona = 1
                    ),
                    memberStatDetail = GetMemberDetailInfoResponse.Result.MemberStatDetail(
                        admissionYear = PreferencesUtil.getString(this, PreferencesUtil.KEY_USER_ADMISSION_YEAR, "") ?: "",
                        numOfRoommate = getSharedPreferences(PreferencesUtil.PREFS_NAME, Context.MODE_PRIVATE).getInt("user_numOfRoommate", 0),
                        dormName = PreferencesUtil.getString(this, "user_dormName", "") ?: "",
                        dormJoiningStatus = PreferencesUtil.getString(this, PreferencesUtil.KEY_USER_DORM_JOINING_STATUS, "") ?: "",
                        wakeUpTime = getSharedPreferences(PreferencesUtil.PREFS_NAME, Context.MODE_PRIVATE).getInt("user_wakeUpTime", 0),
                        sleepingTime = getSharedPreferences(PreferencesUtil.PREFS_NAME, Context.MODE_PRIVATE).getInt("user_sleepingTime", 0),
                        turnOffTime = getSharedPreferences(PreferencesUtil.PREFS_NAME, Context.MODE_PRIVATE).getInt("user_turnOffTime", 0),
                        smokingStatus = PreferencesUtil.getString(this, PreferencesUtil.KEY_USER_SMOKING_STATUS, "") ?: "",
                        sleepingHabits = getSharedPreferences(PreferencesUtil.PREFS_NAME, Context.MODE_PRIVATE).getStringSet("user_sleepingHabits", emptySet())?.toList() ?: emptyList(),
                        coolingIntensity = getSharedPreferences(PreferencesUtil.PREFS_NAME, Context.MODE_PRIVATE).getString("user_coolingIntensity", "") ?: "",
                        heatingIntensity = getSharedPreferences(PreferencesUtil.PREFS_NAME, Context.MODE_PRIVATE).getString("user_heatingIntensity", "") ?: "",
                        lifePattern = PreferencesUtil.getString(this, PreferencesUtil.KEY_USER_LIFE_PATTERN, "") ?: "",
                        intimacy = PreferencesUtil.getString(this, PreferencesUtil.KEY_USER_INTIMACY, "") ?: "",
                        sharingStatus = PreferencesUtil.getString(this, PreferencesUtil.KEY_USER_SHARING_STATUS, "") ?: "",
                        gamingStatus = PreferencesUtil.getString(this, PreferencesUtil.KEY_USER_GAMING_STATUS, "") ?: "",
                        callingStatus = PreferencesUtil.getString(this, PreferencesUtil.KEY_USER_CALLING_STATUS, "") ?: "",
                        studyingStatus = PreferencesUtil.getString(this, PreferencesUtil.KEY_USER_STUDYING_STATUS, "") ?: "",
                        eatingStatus = PreferencesUtil.getString(this, PreferencesUtil.KEY_USER_EATING_STATUS, "") ?: "",
                        cleannessSensitivity = getSharedPreferences(PreferencesUtil.PREFS_NAME, Context.MODE_PRIVATE).getString("user_cleanSensitivity", "") ?: "",
                        noiseSensitivity = getSharedPreferences(PreferencesUtil.PREFS_NAME, Context.MODE_PRIVATE).getString("user_noiseSensitivity", "") ?: "",
                        cleaningFrequency = PreferencesUtil.getString(this, PreferencesUtil.KEY_USER_CLEANING_FREQUENCY, "") ?: "",
                        drinkingFrequency = PreferencesUtil.getString(this, PreferencesUtil.KEY_USER_DRINKING_FREQUENCY, "") ?: "",
                        personalities = getSharedPreferences(PreferencesUtil.PREFS_NAME, Context.MODE_PRIVATE).getStringSet("user_personalities", emptySet())?.toList() ?: emptyList(),
                        mbti = PreferencesUtil.getString(this, PreferencesUtil.KEY_USER_MBTI, "") ?: "",
                        selfIntroduction = PreferencesUtil.getString(this, PreferencesUtil.KEY_USER_SELF_INTRODUCTION, "") ?: ""
                    ),
                    equality = 0,
                    roomId = 0,
                    favoriteId = 0,
                    hasRequestedRoomEntry = false
                )
            }
        } catch (e: Exception) {
            Log.d(TAG, "사용자 라이프스타일 정보 불러오는 중 오류 발생 ${e.message}")
            null
        }
    }

    private fun setupFAB(userDetail: GetMemberDetailInfoResponse.Result) {
        updateFAB()
    }

    private fun updateUI(otherUserDetail: GetMemberDetailInfoResponse.Result) {
        with(binding) {
            Log.d(TAG, "updateUI 실행")
            // 프로필 이미지 업데이트
            setUserProfileImage(otherUserDetail.memberDetail.persona)
            // 이름 및 일치율
            tvOtherUserName.text = otherUserDetail.memberDetail.nickname
            tvUserMatchPercent.text = otherUserDetail.equality.toString()

            favoriteId = otherUserDetail.favoriteId
            updateFavoriteButton()
        }
    }

    private fun setUpListeners(userDetail: GetMemberDetailInfoResponse.Result) {
        // 리스트 뷰 클릭 시
        binding.llListView.setOnClickListener {
            firebaseAnalytics.logEvent("list_view_button_click") {
                param("리스트로 보기", "list_view_button")
                param("룸메이트 상세 화면", "roommate_detail_screen")
            }
            Log.d(TAG, "리스트 뷰 클릭")
            otherUserDetail?.let { detail ->
                selectListView(detail)
            }
        }

        // 테이블 뷰 클릭 시
        binding.llTableView.setOnClickListener {
            firebaseAnalytics.logEvent("table_view_button_click") {
                param("표로 보기", "table_view_button")
                param("룸메이트 상세 화면", "roommate_detail_screen")
            }

            Log.d(TAG, "테이블 뷰 클릭")
            selectTableView(otherUserDetail!!, userDetail!!)
        }

        // 뒤로가기
        binding.btnBack.setOnClickListener {
            finish()
        }

        // 플로팅 버튼 처리
//        updateFAB(userDetail)

        binding.btnChat.setOnClickListener {
            val memberId = otherUserDetail!!.memberDetail.memberId
            val memberName = otherUserDetail!!.memberDetail.nickname
            val intent: Intent = Intent(this, WriteMessageActivity::class.java)
            intent.putExtra("recipientId", memberId)
            intent.putExtra("nickname", memberName)
            startActivity(intent)
        }
    }

    private fun updateRoommateInfo() {
        roommateDetailViewModel.getOtherUserDetailInfo(memberId)
    }

    private fun updateFAB() {
        val spf = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val userMemberId = spf.getInt("user_member_id", -1) // 현재 사용자 ID
        val savedRoomId = spf.getInt("room_id", -1) // 현재 사용자의 방 ID

        val otherRoomId = otherUserDetail?.roomId ?: -1 // 상대방의 방 ID
        val otherMemberId = otherUserDetail?.memberDetail?.memberId ?: -1 // 상대방의 ID

        // 1. 내 방이 없거나 상대방이 이미 방이 있는 경우
        if (savedRoomId <= 0 || otherRoomId > 0) {
            binding.fabRequestRoommate.apply {
                text = "내 방으로 초대하기"
                setBackgroundColor(ContextCompat.getColor(this@RoommateDetailActivity, R.color.gray))
                setTextColor(ContextCompat.getColor(this@RoommateDetailActivity, R.color.white))
                isClickable = true
                setOnClickListener {
                    SnackbarUtil.showCustomSnackbar(
                        context = this@RoommateDetailActivity,
                        message = "초대할 수 없습니다",
                        iconType = SnackbarUtil.IconType.NO,
                        anchorView = binding.fabRequestRoommate,
                        extraYOffset = 20
                    )
                }
            }
            return
        }

        // 2. 현재 사용자가 방장인지 확인
        lifecycleScope.launch {
            roomDetailViewModel.getOtherRoomInfo(savedRoomId)
        }
        roomDetailViewModel.managerMemberId.observe(this) { managerId ->
            if (managerId != userMemberId) {
                // 사용자가 방장이 아닌 경우 버튼 숨김
                binding.fabRequestRoommate.visibility = View.GONE
                return@observe
            }

            // 3. 상대방이 방에 입장 요청한 상태인지 확인
            roomDetailViewModel.getPendingMember(otherMemberId)
            roomDetailViewModel.isPendingMember.observe(this) { isPending ->
                if (isPending) {
                    // 상대방이 방에 입장 요청한 경우
                    binding.fabRequestRoommate.visibility = View.GONE
                    binding.clAcceptBtn.visibility = View.VISIBLE

                    binding.fabAcceptAccept.setOnClickListener {
                        roomDetailViewModel.acceptMemberRequest(otherMemberId, true)
                        SnackbarUtil.showCustomSnackbar(
                            context = this@RoommateDetailActivity,
                            message = "입장 요청을 수락했습니다.",
                            iconType = SnackbarUtil.IconType.YES,
                            anchorView = binding.fabAcceptAccept,
                            extraYOffset = 20
                        )
                        updateRoommateInfo()
                    }

                    binding.fabAcceptRefuse.setOnClickListener {
                        roomDetailViewModel.acceptMemberRequest(otherMemberId, false)
                        SnackbarUtil.showCustomSnackbar(
                            context = this@RoommateDetailActivity,
                            message = "입장 요청을 거절했습니다.",
                            iconType = SnackbarUtil.IconType.YES,
                            anchorView = binding.fabAcceptRefuse,
                            extraYOffset = 20
                        )
                        updateRoommateInfo()
                    }
                    return@observe
                }

                // 4. 상대방이 이미 초대된 상태인지 확인
                roomDetailViewModel.getInvitedStatus(otherMemberId)
                roomDetailViewModel.isInvitedStatus.observe(this@RoommateDetailActivity) { isInvited ->
                    binding.clAcceptBtn.visibility = View.GONE
                    binding.fabRequestRoommate.visibility = View.VISIBLE

                    if (isInvited) {
                        // 이미 초대된 경우
                        binding.fabRequestRoommate.apply {
                            text = "초대 취소하기"
                            setBackgroundColor(ContextCompat.getColor(this@RoommateDetailActivity, R.color.color_box))
                            setTextColor(ContextCompat.getColor(this@RoommateDetailActivity, R.color.main_blue))
                            isClickable = true
                            setOnClickListener {
                                roomDetailViewModel.cancelInvitation(otherMemberId)
                                lifecycleScope.launch {
                                    delay(500)
                                }
                                SnackbarUtil.showCustomSnackbar(
                                    context = this@RoommateDetailActivity,
                                    message = "초대를 취소했습니다.",
                                    iconType = SnackbarUtil.IconType.YES,
                                    anchorView = binding.fabRequestRoommate,
                                    extraYOffset = 20
                                )
                                updateRoommateInfo()
                            }
                        }
                    } else {
                        // 초대 가능 상태
                        binding.fabRequestRoommate.apply {
                            text = "내 방으로 초대하기"
                            setBackgroundColor(ContextCompat.getColor(this@RoommateDetailActivity, R.color.main_blue))
                            setTextColor(ContextCompat.getColor(this@RoommateDetailActivity, R.color.white))
                            isClickable = true
                            setOnClickListener {
                                Log.d("updateFAB", "버튼 클릭됨")
                                roomDetailViewModel.inviteMember(otherMemberId)
                                lifecycleScope.launch {
                                    delay(500)
                                }
                                SnackbarUtil.showCustomSnackbar(
                                    context = this@RoommateDetailActivity,
                                    message = "초대 요청을 보냈습니다.",
                                    iconType = SnackbarUtil.IconType.YES,
                                    anchorView = binding.fabRequestRoommate,
                                    extraYOffset = 20
                                )
                                updateRoommateInfo()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun observeMemberInfo() {
        roommateDetailViewModel.otherUserDetailInfo.observe(this) {memberInfo ->
            if(otherUserDetail == null) return@observe
            else{
                memberId = memberInfo.memberDetail.memberId
                favoriteId = memberInfo.favoriteId
                updateFavoriteButton()
            }
        }
    }

    private fun setupFavoriteButton() {
        binding.ivLike.setOnClickListener {
            lifecycleScope.launch {
                favoriteViewModel.toggleRoommateFavorite(
                    memberId = memberId,
                    favoriteId = favoriteId,
                    onUpdate = {
                        roommateDetailViewModel.getOtherUserDetailInfo(memberId)
                    },
                    onError = { errorMessage ->
                        Toast.makeText(
                            this@RoommateDetailActivity,
                            errorMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                        SnackbarUtil.showCustomSnackbar(
                            context = this@RoommateDetailActivity,
                            message = errorMessage,
                            iconType = SnackbarUtil.IconType.NO,
                            anchorView = binding.fabRequestRoommate,
                            extraYOffset = 20
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

    private fun getMeridianFrom24Hour(hour: Int): String {
        return when (hour) {
            in 0..11 -> "오전"
            in 12..23 -> "오후"
            else -> ""
        }
    }

    private fun convertTo12Hour(hour: Int): Int {
        return when (hour) {
            0 -> 12
            in 1..12 -> hour
            else -> hour - 12
        }
    }

    private fun selectListView(it: GetMemberDetailInfoResponse.Result) {
        // 리스트로 보기 선택 시 텍스트와 아이콘 활성화
        binding.tvListView.setTextColor(ContextCompat.getColor(this, R.color.main_blue))
        binding.ivListViewIcon.setColorFilter(ContextCompat.getColor(this, R.color.main_blue))

        // 표로 보기 텍스트와 아이콘 비활성화
        binding.tvTableView.setTextColor(ContextCompat.getColor(this, R.color.unuse_font))
        binding.ivTableViewIcon.setColorFilter(ContextCompat.getColor(this, R.color.unuse_font))

        // 리스트 레이아웃 초기화 후 동적 뷰 추가
        val listView = LayoutInflater.from(this).inflate(R.layout.item_roommate_detail_list, null)
        binding.listTableLayout.removeAllViews()
        binding.listTableLayout.addView(listView)

        val listBinding = ItemRoommateDetailListBinding.bind(listView)

        listBinding.tvReportTitle.setOnClickListener {
                reportPopup()
        }
        with(listBinding) {
            tvListName.text = it.memberDetail.nickname
            tvListBirth.text = it.memberDetail.birthday.substring(0, 4)  // 앞에 4자리만 받음
            tvListSchool.text = it.memberDetail.universityName
            tvListSchoolNumber.text = it.memberStatDetail.admissionYear
            tvListMajor.text = it.memberDetail.majorName
            tvListDormitoryNum.text = "${it.memberStatDetail.numOfRoommate}인 1실"
            tvListAcceptance.text = it.memberStatDetail.dormJoiningStatus
            tvListWakeUpAmpm.text = getMeridianFrom24Hour(it.memberStatDetail.wakeUpTime)
            tvListWakeUpTime.text = convertTo12Hour(it.memberStatDetail.wakeUpTime).toString()
            tvListSleepAmpm.text = getMeridianFrom24Hour(it.memberStatDetail.sleepingTime)
            tvListSleepTime.text = convertTo12Hour(it.memberStatDetail.sleepingTime).toString()
            tvListLightOffAmpm.text = getMeridianFrom24Hour(it.memberStatDetail.turnOffTime)
            tvListLightOffTime.text = convertTo12Hour(it.memberStatDetail.turnOffTime).toString()
            tvListSmokeCheck.text = it.memberStatDetail.smokingStatus
            tvListSleepHabbit.text = it.memberStatDetail.sleepingHabits.joinToString(", ")
            tvListAc.text = it.memberStatDetail.coolingIntensity
            tvListAcHeater.text = it.memberStatDetail.heatingIntensity
            tvListLivingPattern.text = it.memberStatDetail.lifePattern
            tvListFriendly.text = it.memberStatDetail.intimacy
            tvListShare.text = it.memberStatDetail.sharingStatus
            tvListStudy.text = it.memberStatDetail.studyingStatus
            tvListIntake.text = it.memberStatDetail.eatingStatus
            tvListGameCheck.text = it.memberStatDetail.gamingStatus
            tvListCallCheck.text = it.memberStatDetail.callingStatus
            tvListCleanCheck.text = it.memberStatDetail.cleannessSensitivity
            tvListNoiseCheck.text = it.memberStatDetail.noiseSensitivity
            tvListCleanFrequency.text = it.memberStatDetail.cleaningFrequency
            tvListDrinkFrequency.text = it.memberStatDetail.drinkingFrequency
            tvListPersonalityCheck.text = it.memberStatDetail.personalities.joinToString(", ")
            tvListMbti.text = it.memberStatDetail.mbti
            tvSelfIntroduction.text = it.memberStatDetail.selfIntroduction
        }

    }

    private fun selectTableView(
        other: GetMemberDetailInfoResponse.Result,
        user: GetMemberDetailInfoResponse.Result
    ) {
        // TableView 텍스트와 아이콘 색상 변경
        binding.tvTableView.setTextColor(ContextCompat.getColor(this, R.color.main_blue))
        binding.ivTableViewIcon.setColorFilter(ContextCompat.getColor(this, R.color.main_blue))

        // ListView 텍스트와 아이콘을 원래 색상으로
        binding.tvListView.setTextColor(ContextCompat.getColor(this, R.color.unuse_font))
        binding.ivListViewIcon.setColorFilter(ContextCompat.getColor(this, R.color.unuse_font))

        // list_table_layout에 itemRoommateDetailTable 레이아웃을 동적으로 추가
        val tableView = LayoutInflater.from(this).inflate(R.layout.item_roommate_detail_table, null)
        binding.listTableLayout.removeAllViews()
        binding.listTableLayout.addView(tableView)

        // TableView의 Info와 Detail 데이터를 연결
        val tableBinding = ItemRoommateDetailTableBinding.bind(tableView)  // ViewBinding 연결

        if (user == null) {
            tableBinding.clGoLifecstyle.visibility = View.VISIBLE
        } else {
            tableBinding.clGoLifecstyle.visibility = View.GONE
        }

        tableBinding.lyGoLifestyle.setOnClickListener {
            val intent = Intent(this, RoommateOnboardingActivity::class.java)
            startActivity(intent)
        }

        fun trimText(text: String?): String {
            return if (text != null && text.length > 6) {
                text.substring(0, 7) + ".."
            } else {
                text ?: ""
            }
        }

        tableBinding.tvReportTitle.setOnClickListener {
            reportPopup()
        }

        with(tableBinding) {
            tvTableUserName.text = user.memberDetail.nickname
            tvTableOtherName.text = other.memberDetail.nickname

            tvTableUserBirth.text = user.memberDetail.birthday.substring(0, 4)
            tvTableOtherBirth.text = other.memberDetail.birthday.substring(0, 4)

            tvTableUserSchoolNum.text = "${user.memberStatDetail.admissionYear}학번"
            tvTableOtherSchoolNum.text = "${other.memberStatDetail.admissionYear}학번"

            tvTableUserSchool.text = trimText(user.memberDetail.universityName)
            tvTableOtherSchool.text = trimText(other.memberDetail.universityName)

            tvTableUserMajor.text = trimText(user.memberDetail.majorName)
            tvTableOtherMajor.text = trimText(other.memberDetail.majorName)

            tvTableUserDormitoryNum.text = "${user.memberStatDetail.numOfRoommate}인1실"
            tvTableOtherDormitoryNum.text = "${other.memberStatDetail.numOfRoommate}인1실"

            tvTableUserAcceptance.text = user.memberStatDetail.dormJoiningStatus
            tvTableOtherAcceptance.text = other.memberStatDetail.dormJoiningStatus

            tvTableUserWakeUpAmpm.text = getMeridianFrom24Hour(user.memberStatDetail.wakeUpTime)
            tvTableOtherWakeUpAmpm.text = getMeridianFrom24Hour(other.memberStatDetail.wakeUpTime)

            tvTableUserWakeUpTime.text = " ${user.memberStatDetail.wakeUpTime}시"
            tvTableOtherWakeUpTime.text = " ${other.memberStatDetail.wakeUpTime}시"

            tvTableUserSleepAmpm.text = getMeridianFrom24Hour(user.memberStatDetail.sleepingTime)
            tvTableOtherSleepAmpm.text = getMeridianFrom24Hour(other.memberStatDetail.sleepingTime)

            tvTableUserSleepTime.text = " ${user.memberStatDetail.sleepingTime}시"
            tvTableOtherSleepTime.text = " ${other.memberStatDetail.sleepingTime}시"

            tvTableUserLightOffAmpm.text = getMeridianFrom24Hour(user.memberStatDetail.turnOffTime)
            tvTableOtherLightOffAmpm.text = getMeridianFrom24Hour(other.memberStatDetail.turnOffTime)

            tvTableUserLightOffTime.text = " ${user.memberStatDetail.turnOffTime}시"
            tvTableOtherLightOffTime.text = " ${other.memberStatDetail.turnOffTime}시"

            tvTableUserSmoke.text = trimText(user.memberStatDetail.smokingStatus)
            tvTableOtherSmoke.text = trimText(other.memberStatDetail.smokingStatus)

            tvTableUserSleepHabbit.text =
                trimText(user.memberStatDetail.sleepingHabits.joinToString(", "))
            tvTableOtherSleepHabbit.text =
                trimText(other.memberStatDetail.sleepingHabits.joinToString(", "))

            tvTableUserAc.text = user.memberStatDetail.coolingIntensity
            tvTableOtherAc.text = other.memberStatDetail.coolingIntensity

            tvTableUserHeater.text = user.memberStatDetail.heatingIntensity
            tvTableOtherHeater.text = other.memberStatDetail.heatingIntensity

            tvTableUserLivingPattern.text = trimText(user.memberStatDetail.lifePattern)
            tvTableOtherLivingPattern.text = trimText(other.memberStatDetail.lifePattern)

            tvTableUserFriendly.text = trimText(user.memberStatDetail.intimacy)
            tvTableOtherFriendly.text = trimText(other.memberStatDetail.intimacy)

            tvTableUserShare.text = trimText(user.memberStatDetail.sharingStatus)
            tvTableOtherShare.text = trimText(other.memberStatDetail.sharingStatus)

            tvTableUserStudy.text = trimText(user.memberStatDetail.studyingStatus)
            tvTableOtherStudy.text = trimText(other.memberStatDetail.studyingStatus)

            tvTableUserIntake.text = trimText(user.memberStatDetail.eatingStatus)
            tvTableOtherIntake.text = trimText(other.memberStatDetail.eatingStatus)

            tvTableUserGame.text = trimText(user.memberStatDetail.gamingStatus)
            tvTableOtherGame.text = trimText(other.memberStatDetail.gamingStatus)

            tvTableUserCall.text = trimText(user.memberStatDetail.callingStatus)
            tvTableOtherCall.text = trimText(other.memberStatDetail.callingStatus)

            tvTableUserClean.text = user.memberStatDetail.cleannessSensitivity
            tvTableOtherClean.text = other.memberStatDetail.cleannessSensitivity

            tvTableUserNoise.text = user.memberStatDetail.noiseSensitivity
            tvTableOtherNoise.text = other.memberStatDetail.noiseSensitivity

            tvTableUserCleanFrequency.text = trimText(user.memberStatDetail.cleaningFrequency)
            tvTableOtherCleanFrequency.text = trimText(other.memberStatDetail.cleaningFrequency)

            tvTableUserDrinkFrequency.text = trimText(user.memberStatDetail.drinkingFrequency)
            tvTableOtherDrinkFrequency.text = trimText(other.memberStatDetail.drinkingFrequency)

            tvTableUserPersonality.text =
                trimText(user.memberStatDetail.personalities.joinToString(", "))
            tvTableOtherPersonality.text =
                trimText(other.memberStatDetail.personalities.joinToString(", "))

            tvTableUserMbti.text = user.memberStatDetail.mbti
            tvTableOtherMbti.text = other.memberStatDetail.mbti

            tvSelfIntroduction.text = other.memberStatDetail.selfIntroduction
        }

        if (tableBinding.tvTableUserWakeUpAmpm.text.toString() != tableBinding.tvTableOtherWakeUpAmpm.text.toString() ||
            tableBinding.tvTableUserWakeUpTime.text.toString() != tableBinding.tvTableOtherWakeUpTime.text.toString()
        ) {
            tableBinding.tvTableUserWakeUpAmpm.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.red
                )
            )
            tableBinding.tvTableOtherWakeUpAmpm.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.red
                )
            )
            tableBinding.tvTableUserWakeUpTime.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.red
                )
            )
            tableBinding.tvTableOtherWakeUpTime.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.red
                )
            )
        }

        if (tableBinding.tvTableUserBirth.text != tableBinding.tvTableOtherBirth.text) {
            tableBinding.tvTableUserBirth.setTextColor(ContextCompat.getColor(this, R.color.red))
            tableBinding.tvTableOtherBirth.setTextColor(ContextCompat.getColor(this, R.color.red))
        }

        if (tableBinding.tvTableUserSchoolNum.text != tableBinding.tvTableOtherSchoolNum.text) {
            tableBinding.tvTableUserSchoolNum.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.red
                )
            )
            tableBinding.tvTableOtherSchoolNum.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.red
                )
            )
        }

        if (tableBinding.tvTableUserSchool.text != tableBinding.tvTableOtherSchool.text) {
            tableBinding.tvTableUserSchool.setTextColor(ContextCompat.getColor(this, R.color.red))
            tableBinding.tvTableOtherSchool.setTextColor(ContextCompat.getColor(this, R.color.red))
        }

        if (tableBinding.tvTableUserMajor.text != tableBinding.tvTableOtherMajor.text) {
            tableBinding.tvTableUserMajor.setTextColor(ContextCompat.getColor(this, R.color.red))
            tableBinding.tvTableOtherMajor.setTextColor(ContextCompat.getColor(this, R.color.red))
        }

        if (tableBinding.tvTableUserDormitoryNum.text != tableBinding.tvTableOtherDormitoryNum.text) {
            tableBinding.tvTableUserDormitoryNum.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.red
                )
            )
            tableBinding.tvTableOtherDormitoryNum.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.red
                )
            )
        }

        if (tableBinding.tvTableUserAcceptance.text != tableBinding.tvTableOtherAcceptance.text) {
            tableBinding.tvTableUserAcceptance.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.red
                )
            )
            tableBinding.tvTableOtherAcceptance.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.red
                )
            )
        }

        if (tableBinding.tvTableUserSleepAmpm.text != tableBinding.tvTableOtherSleepAmpm.text ||
            tableBinding.tvTableUserSleepTime.text != tableBinding.tvTableOtherSleepTime.text
        ) {
            tableBinding.tvTableUserSleepAmpm.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.red
                )
            )
            tableBinding.tvTableOtherSleepAmpm.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.red
                )
            )
            tableBinding.tvTableUserSleepTime.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.red
                )
            )
            tableBinding.tvTableOtherSleepTime.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.red
                )
            )
        }

        if (tableBinding.tvTableUserLightOffAmpm.text != tableBinding.tvTableOtherLightOffAmpm.text ||
            tableBinding.tvTableUserLightOffTime.text != tableBinding.tvTableOtherLightOffTime.text
        ) {
            tableBinding.tvTableUserLightOffAmpm.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.red
                )
            )
            tableBinding.tvTableOtherLightOffAmpm.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.red
                )
            )
            tableBinding.tvTableUserLightOffTime.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.red
                )
            )
            tableBinding.tvTableOtherLightOffTime.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.red
                )
            )
        }

        if (tableBinding.tvTableUserSmoke.text != tableBinding.tvTableOtherSmoke.text) {
            tableBinding.tvTableUserSmoke.setTextColor(ContextCompat.getColor(this, R.color.red))
            tableBinding.tvTableOtherSmoke.setTextColor(ContextCompat.getColor(this, R.color.red))
        }

        if (tableBinding.tvTableUserSleepHabbit.text != tableBinding.tvTableOtherSleepHabbit.text) {
            tableBinding.tvTableUserSleepHabbit.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.red
                )
            )
            tableBinding.tvTableOtherSleepHabbit.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.red
                )
            )
        }

        if (tableBinding.tvTableUserAc.text != tableBinding.tvTableOtherAc.text) {
            tableBinding.tvTableUserAc.setTextColor(ContextCompat.getColor(this, R.color.red))
            tableBinding.tvTableOtherAc.setTextColor(ContextCompat.getColor(this, R.color.red))
        }

        if (tableBinding.tvTableUserHeater.text != tableBinding.tvTableOtherHeater.text) {
            tableBinding.tvTableUserHeater.setTextColor(ContextCompat.getColor(this, R.color.red))
            tableBinding.tvTableOtherHeater.setTextColor(ContextCompat.getColor(this, R.color.red))
        }

        if (tableBinding.tvTableUserLivingPattern.text != tableBinding.tvTableOtherLivingPattern.text) {
            tableBinding.tvTableUserLivingPattern.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.red
                )
            )
            tableBinding.tvTableOtherLivingPattern.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.red
                )
            )
        }

        if (tableBinding.tvTableUserFriendly.text != tableBinding.tvTableOtherFriendly.text) {
            tableBinding.tvTableUserFriendly.setTextColor(ContextCompat.getColor(this, R.color.red))
            tableBinding.tvTableOtherFriendly.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.red
                )
            )
        }

        if (tableBinding.tvTableUserShare.text != tableBinding.tvTableOtherShare.text) {
            tableBinding.tvTableUserShare.setTextColor(ContextCompat.getColor(this, R.color.red))
            tableBinding.tvTableOtherShare.setTextColor(ContextCompat.getColor(this, R.color.red))
        }

        if (tableBinding.tvTableUserStudy.text != tableBinding.tvTableOtherStudy.text) {
            tableBinding.tvTableUserStudy.setTextColor(ContextCompat.getColor(this, R.color.red))
            tableBinding.tvTableOtherStudy.setTextColor(ContextCompat.getColor(this, R.color.red))
        }

        if (tableBinding.tvTableUserIntake.text != tableBinding.tvTableOtherIntake.text) {
            tableBinding.tvTableUserIntake.setTextColor(ContextCompat.getColor(this, R.color.red))
            tableBinding.tvTableOtherIntake.setTextColor(ContextCompat.getColor(this, R.color.red))
        }

        if (tableBinding.tvTableUserGame.text != tableBinding.tvTableOtherGame.text) {
            tableBinding.tvTableUserGame.setTextColor(ContextCompat.getColor(this, R.color.red))
            tableBinding.tvTableOtherGame.setTextColor(ContextCompat.getColor(this, R.color.red))
        }

        if (tableBinding.tvTableUserCall.text != tableBinding.tvTableOtherCall.text) {
            tableBinding.tvTableUserCall.setTextColor(ContextCompat.getColor(this, R.color.red))
            tableBinding.tvTableOtherCall.setTextColor(ContextCompat.getColor(this, R.color.red))
        }

        if (tableBinding.tvTableUserClean.text != tableBinding.tvTableOtherClean.text) {
            tableBinding.tvTableUserClean.setTextColor(ContextCompat.getColor(this, R.color.red))
            tableBinding.tvTableOtherClean.setTextColor(ContextCompat.getColor(this, R.color.red))
        }

        if (tableBinding.tvTableUserNoise.text != tableBinding.tvTableOtherNoise.text) {
            tableBinding.tvTableUserNoise.setTextColor(ContextCompat.getColor(this, R.color.red))
            tableBinding.tvTableOtherNoise.setTextColor(ContextCompat.getColor(this, R.color.red))
        }

        if (tableBinding.tvTableUserCleanFrequency.text != tableBinding.tvTableOtherCleanFrequency.text) {
            tableBinding.tvTableUserCleanFrequency.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.red
                )
            )
            tableBinding.tvTableOtherCleanFrequency.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.red
                )
            )
        }

        if (tableBinding.tvTableUserDrinkFrequency.text != tableBinding.tvTableOtherDrinkFrequency.text) {
            tableBinding.tvTableUserDrinkFrequency.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.red
                )
            )
            tableBinding.tvTableOtherDrinkFrequency.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.red
                )
            )
        }

        if (tableBinding.tvTableUserPersonality.text != tableBinding.tvTableOtherPersonality.text) {
            tableBinding.tvTableUserPersonality.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.red
                )
            )
            tableBinding.tvTableOtherPersonality.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.red
                )
            )
        }

        if (tableBinding.tvTableUserMbti.text != tableBinding.tvTableOtherMbti.text) {
            tableBinding.tvTableUserMbti.setTextColor(ContextCompat.getColor(this, R.color.red))
            tableBinding.tvTableOtherMbti.setTextColor(ContextCompat.getColor(this, R.color.red))
        }
    }

    private fun setUserProfileImage(persona: Int) {
        CharacterUtil.setImg(persona, binding.ivOtherUserProfile)
    }

    // 테이블 뷰를 선택했을 때
    private fun selectTableView(info: Info?, detail: Detail?, userInfo: UserInfo) {
        // TableView 텍스트와 아이콘 색상 변경
        binding.tvTableView.setTextColor(ContextCompat.getColor(this, R.color.main_blue))
        binding.ivTableViewIcon.setColorFilter(ContextCompat.getColor(this, R.color.main_blue))

        // ListView 텍스트와 아이콘을 원래 색상으로
        binding.tvListView.setTextColor(ContextCompat.getColor(this, R.color.unuse_font))
        binding.ivListViewIcon.setColorFilter(ContextCompat.getColor(this, R.color.unuse_font))

        // list_table_layout에 itemRoommateDetailTable 레이아웃을 동적으로 추가
        val tableView = LayoutInflater.from(this).inflate(R.layout.item_roommate_detail_table, null)
        binding.listTableLayout.removeAllViews()
        binding.listTableLayout.addView(tableView)

        // TableView의 Info와 Detail 데이터를 연결
        val tableBinding = ItemRoommateDetailTableBinding.bind(tableView)  // ViewBinding 연결

        fun trimText(text: String?): String {
            return if (text != null && text.length > 6) {
                text.substring(0, 7) + ".."
            } else {
                text ?: ""
            }
        }

        val spf = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val name = spf.getString("user_name", "")
        val _birth = spf.getString("user_birthday", "")
        val birthYear = _birth?.substring(0, 4) ?: ""


        tableBinding.tvTableUserName.text = name
        tableBinding.tvTableUserBirth.text = "${birthYear}년"
        tableBinding.tvTableUserSchoolNum.text = "${userInfo.admissionYear}학번"
        tableBinding.tvTableUserSchool.text = "인하대학교"
        tableBinding.tvTableUserMajor.text = trimText(userInfo.major)
        tableBinding.tvTableUserDormitoryNum.text = "${userInfo.numOfRoommate}인 1실"
        tableBinding.tvTableUserAcceptance.text = trimText(userInfo.dormJoiningStatus)
        tableBinding.tvTableUserWakeUpAmpm.text = getMeridianFrom24Hour(userInfo.wakeUpTime)
        tableBinding.tvTableUserWakeUpTime.text = " ${convertTo12Hour(userInfo.wakeUpTime)}시"
        tableBinding.tvTableUserSleepAmpm.text = getMeridianFrom24Hour(userInfo.sleepTime)
        tableBinding.tvTableUserSleepTime.text = " ${convertTo12Hour(userInfo.sleepTime)}시"
        tableBinding.tvTableUserLightOffAmpm.text = getMeridianFrom24Hour(userInfo.turnOffTime)
        tableBinding.tvTableUserLightOffTime.text = " ${convertTo12Hour(userInfo.turnOffTime)}시"
        tableBinding.tvTableUserSmoke.text = userInfo.smokingStatus
        tableBinding.tvTableUserSleepHabbit.text = userInfo.sleepingHabits.joinToString(", ")
        tableBinding.tvTableUserAc.text = trimText(userInfo.coolingIntensity)
        tableBinding.tvTableUserHeater.text = trimText(userInfo.heatingIntensity)
        tableBinding.tvTableUserLivingPattern.text = userInfo.lifePattern
        tableBinding.tvTableUserFriendly.text = trimText(userInfo.intimacy)
        tableBinding.tvTableUserShare.text = trimText(userInfo.sharingStatus)
        tableBinding.tvTableUserStudy.text = trimText(userInfo.studyingStatus)
        tableBinding.tvTableUserIntake.text = trimText(userInfo.eatingStatus)
        tableBinding.tvTableUserGame.text = trimText(userInfo.gamingStatus)
        tableBinding.tvTableUserCall.text = trimText(userInfo.callingStatus)
        tableBinding.tvTableUserClean.text = trimText(userInfo.cleannessSensitivity)
        tableBinding.tvTableUserNoise.text = trimText(userInfo.noiseSensitivity)
        tableBinding.tvTableUserCleanFrequency.text = trimText(userInfo.cleaningFrequency)
        tableBinding.tvTableUserPersonality.text = userInfo.personalities.joinToString(", ")
        tableBinding.tvTableUserMbti.text = userInfo.mbti




        tableBinding.tvTableOtherName.text = info?.memberName
        tableBinding.tvTableOtherBirth.text = "${detail?.birthYear}년"
        tableBinding.tvTableOtherSchoolNum.text = "${detail?.admissionYear}학번"
        tableBinding.tvTableOtherSchool.text = "인하대학교"
        tableBinding.tvTableOtherMajor.text = trimText(detail?.major)
        tableBinding.tvTableOtherDormitoryNum.text = "${detail?.numOfRoommate}인 1실"
        tableBinding.tvTableOtherAcceptance.text = trimText(detail?.dormJoiningStatus)
        tableBinding.tvTableOtherWakeUpAmpm.text = getMeridianFrom24Hour(detail!!.wakeUpTime)
        tableBinding.tvTableOtherWakeUpTime.text = " ${convertTo12Hour(detail!!.wakeUpTime)}시"
        tableBinding.tvTableOtherSleepAmpm.text = getMeridianFrom24Hour(detail!!.sleepingTime)
        tableBinding.tvTableOtherSleepTime.text = " ${convertTo12Hour(detail!!.sleepingTime)}시"
        tableBinding.tvTableOtherLightOffAmpm.text = getMeridianFrom24Hour(detail!!.turnOffTime)
        tableBinding.tvTableOtherLightOffTime.text = " ${convertTo12Hour(detail!!.turnOffTime)}시"
        tableBinding.tvTableOtherSmoke.text = detail?.smokingStatus
        tableBinding.tvTableOtherSleepHabbit.text = trimText(detail?.sleepingHabits)
        tableBinding.tvTableOtherAc.text = detail?.coolingIntensity
        tableBinding.tvTableOtherHeater.text = detail?.heatingIntensity
        tableBinding.tvTableOtherLivingPattern.text = detail?.lifePattern
        tableBinding.tvTableOtherFriendly.text = trimText(detail?.intimacy)
        tableBinding.tvTableOtherShare.text = detail?.sharingStatus
        tableBinding.tvTableOtherStudy.text = trimText(detail?.studyingStatus)
        tableBinding.tvTableOtherIntake.text = trimText(detail?.eatingStatus)
        tableBinding.tvTableOtherGame.text = detail?.gamingStatus
        tableBinding.tvTableOtherCall.text = detail?.callingStatus
        tableBinding.tvTableOtherClean.text = detail?.cleannessSensitivity
        tableBinding.tvTableOtherNoise.text = trimText(detail?.noiseSensitivity)
        tableBinding.tvTableOtherCleanFrequency.text = trimText(detail?.cleaningFrequency)
        tableBinding.tvTableOtherPersonality.text = trimText(detail?.personalities)
        tableBinding.tvTableOtherMbti.text = detail?.mbti
    }

    private fun reportPopup(){
        val dialog = ReportPopup(object : PopupClick {
            override fun reportFunction(reason: Int, content : String) {
                reportViewModel.postReport(memberId, 0, reason, content)
            }
        })
        dialog.show(this.supportFragmentManager!!, "reportPopup")
    }

}