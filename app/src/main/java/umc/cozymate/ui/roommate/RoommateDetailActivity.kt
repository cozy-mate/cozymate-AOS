package umc.cozymate.ui.roommate

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import umc.cozymate.R
import umc.cozymate.data.model.response.roommate.Detail
import umc.cozymate.data.model.response.roommate.Info
import umc.cozymate.databinding.ActivityRoommateDetailBinding
import umc.cozymate.databinding.ItemRoommateDetailListBinding
import umc.cozymate.databinding.ItemRoommateDetailTableBinding
import umc.cozymate.ui.roommate.data_class.UserInfo
import umc.cozymate.ui.viewmodel.RoommateViewModel

class RoommateDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRoommateDetailBinding
    private val viewModel: RoommateViewModel by viewModels()
    private lateinit var spfHelper: UserInfoSPFHelper
    private lateinit var behavior: BottomSheetBehavior<LinearLayout>

    private var isRoommateRequested: Boolean = false  // 버튼 상태를 관리할 변수
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoommateDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Info와 Detail 데이터를 받아오기
        val selectedInfo = intent.getParcelableExtra<Info>("selectInfo")
        val selectedDetail = intent.getParcelableExtra<Detail>("selectDetail")

//        viewModel.roommateRequestStatus.observe(this) { isSuccess ->
//            if (isSuccess) {
//                toggleRoommateRequestButton()
//            } else {
//                Toast.makeText(this, "코지메이트 요청 실패", Toast.LENGTH_SHORT).show()
//            }
//        }
        spfHelper = UserInfoSPFHelper(this)

        // SharedPreferences에서 유저 정보 로드
        val userInfo = spfHelper.loadUserInfo()

        selectedInfo?.let { info ->
            setUserProfileImage(info.memberPersona)  // 프로필 이미지 설정
            binding.tvOtherUserName.text = info.memberName  // 사용자 이름 설정
            binding.tvUserMatchPercent.text = "${info.equality}"
        }

        // 처음 실행 시 리스트 뷰를 기본 선택
        selectListView(selectedInfo, selectedDetail)

        // ListView 클릭 시
        binding.llListView.setOnClickListener {
            selectListView(selectedInfo, selectedDetail)
        }

        // TableView 클릭 시
        binding.llTableView.setOnClickListener {
            selectTableView(selectedInfo, selectedDetail, userInfo)
        }

        // 뒤로가기 버튼
        binding.btnBack.setOnClickListener {
            finish()
        }

//        binding.btnRequestRoommate.setOnClickListener {
//            sendRoommateRequest()
//        }

    }

    private fun toggleRoommateRequestButton() {
        isRoommateRequested = !isRoommateRequested  // 버튼 상태 변경

        if (isRoommateRequested) {
            binding.btnRequestRoommate.apply {
                setBackgroundColor(ContextCompat.getColor(this@RoommateDetailActivity, R.color.yellow))
                text = "요청 취소"
            }
        } else {
            binding.btnRequestRoommate.apply {
                setBackgroundColor(ContextCompat.getColor(this@RoommateDetailActivity, R.color.main_blue))
                text = "코지메이트 요청"
            }
        }
    }
//    private fun sendRoommateRequest() {
//        val accessToken = "Bearer ${getString(R.string.access_token_1)}"  // 액세스 토큰
//        val request = RoommateRequest(/* 요청에 필요한 데이터를 설정 */)
//        viewModel.sendRoommateRequest(accessToken, request)  // API 요청
//    }
    private fun setUserProfileImage(persona: Int) {
        val profileImageResId = when (persona) {
            1 -> R.drawable.character_0
            2 -> R.drawable.character_1
            3 -> R.drawable.character_2
            4 -> R.drawable.character_3
            5 -> R.drawable.character_4
            6 -> R.drawable.character_5
            7 -> R.drawable.character_6
            8 -> R.drawable.character_7
            9 -> R.drawable.character_8
            10 -> R.drawable.character_9
            11 -> R.drawable.character_10
            12 -> R.drawable.character_11
            13 -> R.drawable.character_12
            14 -> R.drawable.character_13
            15 -> R.drawable.character_14
            16 -> R.drawable.character_15
            else -> R.drawable.character_0
        }
        binding.ivOtherUserProfile.setImageResource(profileImageResId)
    }

    // 리스트 뷰를 선택했을 때 처리
    private fun selectListView(info: Info?, detail: Detail?) {
        // ListView 텍스트와 아이콘 색상 변경
        binding.tvListView.setTextColor(ContextCompat.getColor(this, R.color.main_blue))
        binding.ivListViewIcon.setColorFilter(ContextCompat.getColor(this, R.color.main_blue))

        // TableView 텍스트와 아이콘을 원래 색상으로
        binding.tvTableView.setTextColor(ContextCompat.getColor(this, R.color.unuse_font))
        binding.ivTableViewIcon.setColorFilter(ContextCompat.getColor(this, R.color.unuse_font))

        // list_table_layout에 itemRoommateDetailList 레이아웃을 동적으로 추가
        val listView = LayoutInflater.from(this).inflate(R.layout.item_roommate_detail_list, null)
        binding.listTableLayout.removeAllViews()
        binding.listTableLayout.addView(listView)

        // ListView의 Info와 Detail 데이터를 연결
        val listBinding = ItemRoommateDetailListBinding.bind(listView)  // ViewBinding 연결

        listBinding.tvListName.text = info?.memberName
        listBinding.tvListBirth.text = "${detail?.birthYear}"
        listBinding.tvListSchool.text = "인하대학교"
        listBinding.tvListSchoolNumber.text = detail?.admissionYear.toString()
        listBinding.tvListMajor.text = detail?.major
        listBinding.tvListDormitoryNum.text = "${info?.numOfRoommate}인 1실"
        listBinding.tvListAcceptance.text = detail?.acceptance
        listBinding.tvListWakeUpAmpm.text = detail?.wakeUpMeridian
        listBinding.tvListWakeUpTime.text = detail?.wakeUpTime.toString()
        listBinding.tvListSleepAmpm.text = detail?.sleepingMeridian
        listBinding.tvListSleepTime.text = detail?.sleepingTime.toString()
        listBinding.tvListLightOffAmpm.text = detail?.turnOffMeridian
        listBinding.tvListLightOffTime.text = detail?.turnOffTime.toString()
        listBinding.tvListSmokeCheck.text = detail?.smokingState
        listBinding.tvListSleepHabbit.text = detail?.sleepingHabit
        listBinding.tvListAc.text = when (detail?.airConditioningIntensity) {
            1 -> "약하게 틀어요"
            2 -> "적당하게 틀어요"
            3 -> "세게 틀어요"
            else -> "적당하게 틀어요"
        }
        listBinding.tvListAcHeater.text = when (detail?.airConditioningIntensity) {
            1 -> "약하게 틀어요"
            2 -> "적당하게 틀어요"
            3 -> "세게 틀어요"
            else -> "적당하게 틀어요"
        }
        listBinding.tvListLivingPattern.text = detail?.lifePattern
        listBinding.tvListFriendly.text = detail?.intimacy
        listBinding.tvListShare.text = if (detail?.canShare == true) {
            "O"
        } else {
            "X"
        }
        listBinding.tvListStudy.text = detail?.studying
        listBinding.tvListGameCheck.text = if (detail?.isPlayGame == true) {
            "O"
        } else {
            "X"
        }
        listBinding.tvListCallCheck.text = if (detail?.isPhoneCall == true) {
            "O"
        } else {
            "X"
        }
        listBinding.tvListIntake.text = detail?.intake
        listBinding.tvListCleanCheck.text = when (detail?.cleanSensitivity) {
            1 -> "매우 예민하지 않아요"
            2 -> "예민하지 않아요"
            3 -> "보통이에요"
            4 -> "예민해요"
            5 -> "매우 예민해요"
            else -> "보통이에요"
        }
        listBinding.tvListNoiseCheck.text = when (detail?.noiseSensitivity) {
            1 -> "매우 예민하지 않아요"
            2 -> "예민하지 않아요"
            3 -> "보통이에요"
            4 -> "예민해요"
            5 -> "매우 예민해요"
            else -> "보통이에요"
        }
        listBinding.tvListCleanFrequency.text = detail?.cleaningFrequency
        listBinding.tvListPersonalityCheck.text = detail?.personality
        listBinding.tvListMbti.text = detail?.mbti

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
        tableBinding.tvTableUserAcceptance.text = trimText(userInfo.acceptance)
        tableBinding.tvTableUserWakeUpAmpm.text = userInfo.wakeAmPm
        tableBinding.tvTableUserWakeUpTime.text = " ${userInfo.wakeUpTime}시"
        tableBinding.tvTableUserSleepAmpm.text = userInfo.sleepAmPm
        tableBinding.tvTableUserSleepTime.text = " ${userInfo.sleepTime}시"
        tableBinding.tvTableUserLightOffAmpm.text = userInfo.lightOffAmPm
        tableBinding.tvTableUserLightOffTime.text = " ${userInfo.lightOffTime}시"
        tableBinding.tvTableUserSmoke.text = userInfo.smokingState
        tableBinding.tvTableUserSleepHabbit.text = userInfo.sleepingHabit
        tableBinding.tvTableUserAc.text = trimText(userInfo.airConditioningIntensity)
        tableBinding.tvTableUserHeater.text = trimText(userInfo.heatingIntensity)
        tableBinding.tvTableUserLivingPattern.text = userInfo.lifePattern
        tableBinding.tvTableUserFriendly.text = trimText(userInfo.intimacy)
        tableBinding.tvTableUserShare.text = trimText(userInfo.canShare)
        tableBinding.tvTableUserStudy.text = trimText(userInfo.studying)
        tableBinding.tvTableUserIntake.text = trimText(userInfo.intake)
        tableBinding.tvTableUserGame.text = trimText(userInfo.isPlayGame)
        tableBinding.tvTableUserCall.text = trimText(userInfo.isPhoneCall)
        tableBinding.tvTableUserClean.text = trimText(userInfo.cleanSensitivity)
        tableBinding.tvTableUserNoise.text = trimText(userInfo.noiseSensitivity)
        tableBinding.tvTableUserCleanFrequency.text = trimText(userInfo.cleaningFrequency)
        tableBinding.tvTableUserPersonality.text = trimText(userInfo.personality)
        tableBinding.tvTableUserMbti.text = userInfo.mbti




        tableBinding.tvTableOtherName.text = info?.memberName
        tableBinding.tvTableOtherBirth.text = "${detail?.birthYear}년"
        tableBinding.tvTableOtherSchoolNum.text = "${detail?.admissionYear}학번"
        tableBinding.tvTableOtherSchool.text = "인하대학교"
        tableBinding.tvTableOtherMajor.text = trimText(detail?.major)
        tableBinding.tvTableOtherDormitoryNum.text = "${detail?.numOfRoommate}인 1실"
        tableBinding.tvTableOtherAcceptance.text = trimText(detail?.acceptance)
        tableBinding.tvTableOtherWakeUpAmpm.text = detail?.wakeUpMeridian
        tableBinding.tvTableOtherWakeUpTime.text = " ${detail?.wakeUpTime}시"
        tableBinding.tvTableOtherSleepAmpm.text = detail?.sleepingMeridian
        tableBinding.tvTableOtherSleepTime.text = " ${detail?.sleepingTime}시"
        tableBinding.tvTableOtherLightOffAmpm.text = detail?.turnOffMeridian
        tableBinding.tvTableOtherLightOffTime.text = " ${detail?.turnOffTime}시"
        tableBinding.tvTableOtherSmoke.text = detail?.smokingState
        tableBinding.tvTableOtherSleepHabbit.text = trimText(detail?.sleepingHabit)
        tableBinding.tvTableOtherAc.text = when (detail?.airConditioningIntensity) {
            1 -> "약하게 틀어요"
            2 -> "적당하게 틀어요"
            3 -> "세게 틀어요"
            else -> "적당하게 틀어요"
        }
        tableBinding.tvTableOtherHeater.text = when (detail?.heatingIntensity) {
            1 -> "약하게 틀어요"
            2 -> "적당하게 틀어요"
            3 -> "세게 틀어요"
            else -> "적당하게 틀어요"
        }
        tableBinding.tvTableOtherLivingPattern.text = detail?.lifePattern
        tableBinding.tvTableOtherFriendly.text = trimText(detail?.intimacy)
        tableBinding.tvTableOtherShare.text = if (detail?.canShare == true) {
            "O"
        } else {
            "X"
        }
        tableBinding.tvTableOtherStudy.text = trimText(detail?.studying)
        tableBinding.tvTableOtherIntake.text = trimText(detail?.intake)
        tableBinding.tvTableOtherGame.text = if (detail?.isPlayGame == true) {
            "O"
        } else {
            "X"
        }
        tableBinding.tvTableOtherCall.text = if (detail?.isPhoneCall == true) {
            "O"
        } else {
            "X"
        }
        tableBinding.tvTableOtherClean.text = when (detail?.cleanSensitivity) {
            1 -> "매우 예민하지.."
            2 -> "예민하지 않아.."
            3 -> "보통이에요"
            4 -> "예민해요"
            5 -> "매우 예민해요"
            else -> "보통이에요"
        }
        tableBinding.tvTableOtherNoise.text = when (detail?.noiseSensitivity) {
            1 -> "매우 예민하지.."
            2 -> "예민하지 않아.."
            3 -> "보통이에요"
            4 -> "예민해요"
            5 -> "매우 예민해요"
            else -> "보통이에요"
        }
        tableBinding.tvTableOtherCleanFrequency.text = trimText(detail?.cleaningFrequency)
        tableBinding.tvTableOtherPersonality.text = trimText(detail?.personality)
        tableBinding.tvTableOtherMbti.text = detail?.mbti


        if (tableBinding.tvTableUserWakeUpAmpm.text.toString() != tableBinding.tvTableOtherWakeUpAmpm.text.toString() ||
            tableBinding.tvTableUserWakeUpTime.text.toString() != tableBinding.tvTableOtherWakeUpTime.text.toString()) {
            tableBinding.tvTableUserWakeUpAmpm.setTextColor(ContextCompat.getColor(this, R.color.red))
            tableBinding.tvTableOtherWakeUpAmpm.setTextColor(ContextCompat.getColor(this, R.color.red))
            tableBinding.tvTableUserWakeUpTime.setTextColor(ContextCompat.getColor(this, R.color.red))
            tableBinding.tvTableOtherWakeUpTime.setTextColor(ContextCompat.getColor(this, R.color.red))
        }

        if (tableBinding.tvTableUserBirth.text != tableBinding.tvTableOtherBirth) {
            tableBinding.tvTableUserBirth.setTextColor(ContextCompat.getColor(this, R.color.red))
            tableBinding.tvTableOtherBirth.setTextColor(ContextCompat.getColor(this, R.color.red))
        }

        if (tableBinding.tvTableUserSchoolNum.text != tableBinding.tvTableOtherSchoolNum.text) {
            tableBinding.tvTableUserSchoolNum.setTextColor(ContextCompat.getColor(this, R.color.red))
            tableBinding.tvTableOtherSchoolNum.setTextColor(ContextCompat.getColor(this, R.color.red))
        }

        if (tableBinding.tvTableUserMajor.text != tableBinding.tvTableOtherMajor.text) {
            tableBinding.tvTableUserMajor.setTextColor(ContextCompat.getColor(this, R.color.red))
            tableBinding.tvTableOtherMajor.setTextColor(ContextCompat.getColor(this, R.color.red))
        }

        if (tableBinding.tvTableUserDormitoryNum.text != tableBinding.tvTableOtherDormitoryNum.text) {
            tableBinding.tvTableUserDormitoryNum.setTextColor(ContextCompat.getColor(this, R.color.red))
            tableBinding.tvTableOtherDormitoryNum.setTextColor(ContextCompat.getColor(this, R.color.red))
        }

        if (tableBinding.tvTableUserAcceptance.text != tableBinding.tvTableOtherAcceptance.text) {
            tableBinding.tvTableUserAcceptance.setTextColor(ContextCompat.getColor(this, R.color.red))
            tableBinding.tvTableOtherAcceptance.setTextColor(ContextCompat.getColor(this, R.color.red))
        }

        if (tableBinding.tvTableUserSleepAmpm.text != tableBinding.tvTableOtherSleepAmpm.text ||
            tableBinding.tvTableUserSleepTime.text != tableBinding.tvTableOtherSleepTime.text) {
            tableBinding.tvTableUserSleepAmpm.setTextColor(ContextCompat.getColor(this, R.color.red))
            tableBinding.tvTableOtherSleepAmpm.setTextColor(ContextCompat.getColor(this, R.color.red))
            tableBinding.tvTableUserSleepTime.setTextColor(ContextCompat.getColor(this, R.color.red))
            tableBinding.tvTableOtherSleepTime.setTextColor(ContextCompat.getColor(this, R.color.red))
        }

        if (tableBinding.tvTableUserLightOffAmpm.text != tableBinding.tvTableOtherLightOffAmpm.text ||
            tableBinding.tvTableUserLightOffTime.text != tableBinding.tvTableOtherLightOffTime.text) {
            tableBinding.tvTableUserLightOffAmpm.setTextColor(ContextCompat.getColor(this, R.color.red))
            tableBinding.tvTableOtherLightOffAmpm.setTextColor(ContextCompat.getColor(this, R.color.red))
            tableBinding.tvTableUserLightOffTime.setTextColor(ContextCompat.getColor(this, R.color.red))
            tableBinding.tvTableOtherLightOffTime.setTextColor(ContextCompat.getColor(this, R.color.red))
        }

        if (tableBinding.tvTableUserSmoke.text != tableBinding.tvTableOtherSmoke.text) {
            tableBinding.tvTableUserSmoke.setTextColor(ContextCompat.getColor(this, R.color.red))
            tableBinding.tvTableOtherSmoke.setTextColor(ContextCompat.getColor(this, R.color.red))
        }

        if (tableBinding.tvTableUserSleepHabbit.text != tableBinding.tvTableOtherSleepHabbit.text) {
            tableBinding.tvTableUserSleepHabbit.setTextColor(ContextCompat.getColor(this, R.color.red))
            tableBinding.tvTableOtherSleepHabbit.setTextColor(ContextCompat.getColor(this, R.color.red))
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
            tableBinding.tvTableUserLivingPattern.setTextColor(ContextCompat.getColor(this, R.color.red))
            tableBinding.tvTableOtherLivingPattern.setTextColor(ContextCompat.getColor(this, R.color.red))
        }

        if (tableBinding.tvTableUserFriendly.text != tableBinding.tvTableOtherFriendly.text) {
            tableBinding.tvTableUserFriendly.setTextColor(ContextCompat.getColor(this, R.color.red))
            tableBinding.tvTableOtherFriendly.setTextColor(ContextCompat.getColor(this, R.color.red))
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
            tableBinding.tvTableUserCleanFrequency.setTextColor(ContextCompat.getColor(this, R.color.red))
            tableBinding.tvTableOtherCleanFrequency.setTextColor(ContextCompat.getColor(this, R.color.red))
        }

        if (tableBinding.tvTableUserPersonality.text != tableBinding.tvTableOtherPersonality.text) {
            tableBinding.tvTableUserPersonality.setTextColor(ContextCompat.getColor(this, R.color.red))
            tableBinding.tvTableOtherPersonality.setTextColor(ContextCompat.getColor(this, R.color.red))
        }

        if (tableBinding.tvTableUserMbti.text != tableBinding.tvTableOtherMbti.text) {
            tableBinding.tvTableUserMbti.setTextColor(ContextCompat.getColor(this, R.color.red))
            tableBinding.tvTableOtherMbti.setTextColor(ContextCompat.getColor(this, R.color.red))
        }
        // SharedPreferences에서 불러온 값 적용

        // 추가로 필요한 값이 있다면 동일하게 설정
    }
}
