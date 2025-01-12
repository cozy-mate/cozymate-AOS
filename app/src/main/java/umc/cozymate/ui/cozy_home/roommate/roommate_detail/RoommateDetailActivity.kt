package umc.cozymate.ui.cozy_home.roommate.roommate_detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.R
import umc.cozymate.data.model.response.member.stat.GetMemberDetailInfoResponse
import umc.cozymate.data.model.response.roommate.Detail
import umc.cozymate.data.model.response.roommate.Info
import umc.cozymate.databinding.ActivityRoommateDetailBinding
import umc.cozymate.databinding.ItemRoommateDetailListBinding
import umc.cozymate.databinding.ItemRoommateDetailTableBinding
import umc.cozymate.ui.message.WriteMessageActivity
import umc.cozymate.ui.roommate.RoommateOnboardingActivity
import umc.cozymate.ui.roommate.data_class.UserInfo
import umc.cozymate.ui.viewmodel.RoommateDetailViewModel

@AndroidEntryPoint
class RoommateDetailActivity : AppCompatActivity() {

    private val TAG = this.javaClass.simpleName
    private lateinit var binding: ActivityRoommateDetailBinding
    private val viewModel: RoommateDetailViewModel by viewModels()
    private var memberId: Int = -1
    private var otherUserDetail: GetMemberDetailInfoResponse.Result? = null
//    private var userDetail: GetMemberDetailInfoResponse.Result? = null

    private var isRoommateRequested: Boolean = false  // 버튼 상태를 관리할 변수
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoommateDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        memberId = intent.getIntExtra("member_id", -1)
//        Log.d(TAG, "멤버 상세 조회 아이디 : ${memberId}")

        // intent로 사용자 정보 전달
        otherUserDetail = intent.getParcelableExtra("other_user_detail")
        Log.d(TAG, "Received user detail: $otherUserDetail")

        val userDetail = getUserDetailFromPreferences()

        updateUI(otherUserDetail!!)
        selectListView(otherUserDetail!!)

        setUpListeners(userDetail!!)


        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun getUserDetailFromPreferences() : GetMemberDetailInfoResponse.Result? {
        val spf = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return try {
            val nickname = spf.getString("user_nickname", "")
            val birthday = spf.getString("user_birthday", "")
            if (nickname.isNullOrEmpty() || birthday.isNullOrEmpty()) {
                null
            } else {
                GetMemberDetailInfoResponse.Result(
                    memberDetail = GetMemberDetailInfoResponse.Result.MemberDetail(
                        nickname = nickname,
                        birthday = birthday,
                        universityName = spf.getString("user_university_name", "") ?: "",
                        majorName = spf.getString("user_major_name", "") ?: "",
                        gender = spf.getString("user_gender", "") ?: "",
                        memberId = spf.getInt("user_member_id", 0),
                        universityId = 0,
                        persona = 0
                    ),
                    memberStatDetail = GetMemberDetailInfoResponse.Result.MemberStatDetail(
                        admissionYear = spf.getString("user_admissionYear", "") ?: "",
                        numOfRoommate = spf.getInt("user_numOfRoommate", 0),
                        dormitoryName = spf.getString("user_dormitoryName", "") ?: "",
                        acceptance = spf.getString("user_acceptance", "") ?: "",
                        wakeUpMeridian = spf.getString("user_wakeUpMeridian", "") ?: "",
                        wakeUpTime = spf.getInt("user_wakeUpTime", 0),
                        sleepingMeridian = spf.getString("user_sleepingMeridian", "") ?: "",
                        sleepingTime = spf.getInt("user_sleepingTime", 0),
                        turnOffMeridian = spf.getString("user_turnOffMeridian", "") ?: "",
                        turnOffTime = spf.getInt("user_turnOffTime", 0),
                        smoking = spf.getString("user_smoking", "") ?: "",
                        sleepingHabit = spf.getStringSet("user_sleepingHabit", emptySet())?.toList()
                            ?: emptyList(),
                        airConditioningIntensity = spf.getInt("user_airConditioningIntensity", 3),
                        heatingIntensity = spf.getInt("user_heatingIntensity", 3),
                        lifePattern = spf.getString("user_lifePattern", "") ?: "",
                        intimacy = spf.getString("user_intimacy", "") ?: "",
                        canShare = spf.getString("user_canShare", "") ?: "",
                        isPlayGame = spf.getString("user_isPlayGame", "") ?: "",
                        isPhoneCall = spf.getString("user_isPhoneCall", "") ?: "",
                        studying = spf.getString("user_studying", "") ?: "",
                        intake = spf.getString("user_intake", "") ?: "",
                        cleanSensitivity = spf.getInt("user_cleanSensitivity", 3),
                        noiseSensitivity = spf.getInt("user_noiseSensitivity", 3),
                        cleaningFrequency = spf.getString("user_cleaningFrequency", "") ?: "",
                        drinkingFrequency = spf.getString("user_drinkingFrequency", "") ?: "",
                        personality = spf.getStringSet("user_personality", emptySet())?.toList()
                            ?: emptyList(),
                        mbti = spf.getString("user_mbti", "") ?: "",
                        selfIntroduction = spf.getString("user_selfIntroduction", "") ?: "",
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

    private fun updateUI(otherUserDetail: GetMemberDetailInfoResponse.Result) {
        with(binding) {
            Log.d(TAG, "updateUI 실행")
            // 프로필 이미지 업데이트
            setUserProfileImage(otherUserDetail.memberDetail.persona)

            // 이름 및 일치율
            tvOtherUserName.text = otherUserDetail.memberDetail.nickname
            tvUserMatchPercent.text = otherUserDetail.equality.toString()
        }
    }

    private fun setUpListeners(userDetail: GetMemberDetailInfoResponse.Result) {
        // 리스트 뷰 클릭 시
        binding.llListView.setOnClickListener {
            Log.d(TAG, "리스트 뷰 클릭")
            otherUserDetail?.let { detail ->
                selectListView(detail)
            }
        }

        // 테이블 뷰 클릭 시
        binding.llTableView.setOnClickListener {
            Log.d(TAG, "테이블 뷰 클릭")
            selectTableView(otherUserDetail!!, userDetail!!)
        }

        // 뒤로가기
        binding.btnBack.setOnClickListener {
            finish()
        }

        // 플로팅 버튼 처리
        binding.fabRequestRoommate.setOnClickListener {
            toggleRoommateRequestButton()
        }
        binding.btnChat.setOnClickListener {
            val memberId =  otherUserDetail!!.memberDetail.memberId
            val memberName = otherUserDetail!!.memberDetail.nickname
            val intent : Intent = Intent(this, WriteMessageActivity::class.java)
            intent.putExtra("recipientId",memberId)
            intent.putExtra("nickname",memberName)
            startActivity(intent)
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

        with(listBinding) {
            tvListName.text = it.memberDetail.nickname
            tvListBirth.text = it.memberDetail.birthday.substring(0, 4)  // 앞에 4자리만 받음
            tvListSchool.text = it.memberDetail.universityName
            tvListSchoolNumber.text = it.memberStatDetail.admissionYear
            tvListMajor.text = it.memberDetail.majorName
            tvListDormitoryNum.text = "${it.memberStatDetail.numOfRoommate}인 1실"
            tvListAcceptance.text = it.memberStatDetail.acceptance
            tvListWakeUpAmpm.text = it.memberStatDetail.wakeUpMeridian
            tvListWakeUpTime.text = it.memberStatDetail.wakeUpTime.toString()
            tvListSleepAmpm.text = it.memberStatDetail.sleepingMeridian
            tvListSleepTime.text = it.memberStatDetail.sleepingTime.toString()
            tvListLightOffAmpm.text = it.memberStatDetail.turnOffMeridian
            tvListLightOffTime.text = it.memberStatDetail.turnOffTime.toString()
            tvListSmokeCheck.text = it.memberStatDetail.smoking
            tvListSleepHabbit.text = it.memberStatDetail.sleepingHabit.joinToString(", ")
            tvListAc.text = when (it.memberStatDetail.airConditioningIntensity) {
                0 -> "안 틀어요"
                1 -> "약하게 틀어요"
                2 -> "적당하게 틀어요"
                3 -> "세게 틀어요"
                else -> "적당하게 틀어요"
            }
            tvListAcHeater.text = when (it.memberStatDetail.heatingIntensity) {
                0 -> "안 틀어요"
                1 -> "약하게 틀어요"
                2 -> "적당하게 틀어요"
                3 -> "세게 틀어요"
                else -> "적당하게 틀어"
            }
            tvListLivingPattern.text = it.memberStatDetail.lifePattern
            tvListFriendly.text = it.memberStatDetail.intimacy
            tvListStudy.text = it.memberStatDetail.studying
            tvListIntake.text = it.memberStatDetail.intake
            tvListGameCheck.text = it.memberStatDetail.isPlayGame
            tvListCallCheck.text = it.memberStatDetail.isPhoneCall
            tvListCleanCheck.text = when (it.memberStatDetail.cleanSensitivity) {
                1 -> "매우 예민하지 않아요"
                2 -> "예민하지 않아요"
                3 -> "보통이에요"
                4 -> "예민해요"
                5 -> "매우 예민해요"
                else -> "보통이에요"
            }
            tvListNoiseCheck.text = when (it.memberStatDetail.noiseSensitivity) {
                1 -> "매우 예민하지 않아요"
                2 -> "예민하지 않아요"
                3 -> "보통이에요"
                4 -> "예민해요"
                5 -> "매우 예민해요"
                else -> "보통이에요"
            }
            tvListCleanFrequency.text = it.memberStatDetail.cleaningFrequency
            tvListDrinkFrequency.text = it.memberStatDetail.drinkingFrequency
            tvListPersonalityCheck.text = it.memberStatDetail.personality.joinToString(", ")
            tvListMbti.text = it.memberStatDetail.mbti
            tvSelfIntroduction.text = it.memberStatDetail.selfIntroduction
        }

    }

    private fun selectTableView(other: GetMemberDetailInfoResponse.Result, user: GetMemberDetailInfoResponse.Result) {
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

            tvTableUserAcceptance.text = user.memberStatDetail.acceptance
            tvTableOtherAcceptance.text = other.memberStatDetail.acceptance

            tvTableUserWakeUpAmpm.text = user.memberStatDetail.wakeUpMeridian
            tvTableOtherWakeUpAmpm.text = other.memberStatDetail.wakeUpMeridian

            tvTableUserWakeUpTime.text = " ${user.memberStatDetail.wakeUpTime}시"
            tvTableOtherWakeUpTime.text = " ${other.memberStatDetail.wakeUpTime}시"

            tvTableUserSleepAmpm.text = user.memberStatDetail.sleepingMeridian
            tvTableOtherSleepAmpm.text = other.memberStatDetail.sleepingMeridian

            tvTableUserSleepTime.text = " ${user.memberStatDetail.sleepingTime}시"
            tvTableOtherSleepTime.text = " ${other.memberStatDetail.sleepingTime}시"

            tvTableUserLightOffAmpm.text = user.memberStatDetail.turnOffMeridian
            tvTableOtherLightOffAmpm.text = other.memberStatDetail.turnOffMeridian

            tvTableUserLightOffTime.text = " ${user.memberStatDetail.turnOffTime}시"
            tvTableOtherLightOffTime.text = " ${other.memberStatDetail.turnOffTime}시"

            tvTableUserSmoke.text = trimText(user.memberStatDetail.smoking)
            tvTableOtherSmoke.text = trimText(other.memberStatDetail.smoking)

            tvTableUserSleepHabbit.text = trimText(user.memberStatDetail.sleepingHabit.joinToString(", "))
            tvTableOtherSleepHabbit.text = trimText(other.memberStatDetail.sleepingHabit.joinToString(", "))

            tvTableUserAc.text = trimText(when (user.memberStatDetail.airConditioningIntensity) {
                1 -> "매우 예민하지 않아요"
                2 -> "예민하지 않아요"
                3 -> "보통이에요"
                4 -> "예민해요"
                5 -> "매우 예민해요"
                else -> "보통이에요"
            })
            tvTableOtherAc.text = trimText(when (other.memberStatDetail.airConditioningIntensity) {
                1 -> "매우 예민하지 않아요"
                2 -> "예민하지 않아요"
                3 -> "보통이에요"
                4 -> "예민해요"
                5 -> "매우 예민해요"
                else -> "보통이에요"
            })

            tvTableUserHeater.text = trimText(when (user.memberStatDetail.heatingIntensity) {
                1 -> "매우 예민하지 않아요"
                2 -> "예민하지 않아요"
                3 -> "보통이에요"
                4 -> "예민해요"
                5 -> "매우 예민해요"
                else -> "보통이에요"
            })
            tvTableOtherHeater.text = trimText(when (other.memberStatDetail.heatingIntensity) {
                1 -> "매우 예민하지 않아요"
                2 -> "예민하지 않아요"
                3 -> "보통이에요"
                4 -> "예민해요"
                5 -> "매우 예민해요"
                else -> "보통이에요"
            })

            tvTableUserLivingPattern.text = trimText(user.memberStatDetail.lifePattern)
            tvTableOtherLivingPattern.text = trimText(other.memberStatDetail.lifePattern)

            tvTableUserFriendly.text = trimText(user.memberStatDetail.intimacy)
            tvTableOtherFriendly.text = trimText(other.memberStatDetail.intimacy)

            tvTableUserShare.text = trimText(user.memberStatDetail.canShare)
            tvTableOtherShare.text = trimText(other.memberStatDetail.canShare)

            tvTableUserStudy.text = trimText(user.memberStatDetail.studying)
            tvTableOtherStudy.text = trimText(other.memberStatDetail.studying)

            tvTableUserIntake.text = trimText(user.memberStatDetail.intake)
            tvTableOtherIntake.text = trimText(other.memberStatDetail.intake)

            tvTableUserGame.text = trimText(user.memberStatDetail.isPlayGame)
            tvTableOtherGame.text = trimText(other.memberStatDetail.isPlayGame)

            tvTableUserCall.text = trimText(user.memberStatDetail.isPhoneCall)
            tvTableOtherCall.text = trimText(other.memberStatDetail.isPhoneCall)

            tvTableUserClean.text = trimText(when (user.memberStatDetail.cleanSensitivity) {
                1 -> "매우 예민하지 않아요"
                2 -> "예민하지 않아요"
                3 -> "보통이에요"
                4 -> "예민해요"
                5 -> "매우 예민해요"
                else -> "보통이에요"
            })
            tvTableOtherClean.text = trimText(when (other.memberStatDetail.cleanSensitivity) {
                1 -> "매우 예민하지 않아요"
                2 -> "예민하지 않아요"
                3 -> "보통이에요"
                4 -> "예민해요"
                5 -> "매우 예민해요"
                else -> "보통이에요"
            })

            tvTableUserNoise.text = trimText(when (user.memberStatDetail.noiseSensitivity) {
                1 -> "매우 예민하지 않아요"
                2 -> "예민하지 않아요"
                3 -> "보통이에요"
                4 -> "예민해요"
                5 -> "매우 예민해요"
                else -> "보통이에요"
            })
            tvTableOtherNoise.text = trimText(when (other.memberStatDetail.noiseSensitivity) {
                1 -> "매우 예민하지 않아요"
                2 -> "예민하지 않아요"
                3 -> "보통이에요"
                4 -> "예민해요"
                5 -> "매우 예민해요"
                else -> "보통이에요"
            })

            tvTableUserCleanFrequency.text = trimText(user.memberStatDetail.cleaningFrequency)
            tvTableOtherCleanFrequency.text = trimText(other.memberStatDetail.cleaningFrequency)

            tvTableUserDrinkFrequency.text = trimText(user.memberStatDetail.drinkingFrequency)
            tvTableOtherDrinkFrequency.text = trimText(other.memberStatDetail.drinkingFrequency)

            tvTableUserPersonality.text = trimText(user.memberStatDetail.personality.joinToString(", "))
            tvTableOtherPersonality.text = trimText(other.memberStatDetail.personality.joinToString(", "))

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

    private fun toggleRoommateRequestButton() {
        isRoommateRequested = !isRoommateRequested  // 버튼 상태 변경

        with(binding.fabRequestRoommate) {
            if (isRoommateRequested) {
                // 기본값 = true
                backgroundTintList = getColorStateList(R.color.main_blue)
            } else {
                backgroundTintList = getColorStateList(R.color.gray)
            }
        }
    }

    private fun setUserProfileImage(persona: Int) {
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
        binding.ivOtherUserProfile.setImageResource(profileImageResId)
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
        tableBinding.tvTableUserSleepHabbit.text = userInfo.sleepingHabit.joinToString(", ")
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
        tableBinding.tvTableUserPersonality.text = userInfo.personality.joinToString(", ")
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
        tableBinding.tvTableOtherAc.text = trimText(
            when (detail?.airConditioningIntensity) {
                0 -> "안 틀어요"
                1 -> "약하게 틀어요"
                2 -> "적당하게 틀어요"
                3 -> "세게 틀어요"
                else -> "적당하게 틀어요"
            }
        )
        tableBinding.tvTableOtherHeater.text = trimText(
            when (detail?.heatingIntensity) {
                0 -> "안 틀어요"
                1 -> "약하게 틀어요"
                2 -> "적당하게 틀어요"
                3 -> "세게 틀어요"
                else -> "적당하게 틀어요"
            }
        )
        tableBinding.tvTableOtherLivingPattern.text = detail?.lifePattern
        tableBinding.tvTableOtherFriendly.text = trimText(detail?.intimacy)
        tableBinding.tvTableOtherShare.text = detail?.canShare
        tableBinding.tvTableOtherStudy.text = trimText(detail?.studying)
        tableBinding.tvTableOtherIntake.text = trimText(detail?.intake)
        tableBinding.tvTableOtherGame.text = detail?.isPlayGame
        tableBinding.tvTableOtherCall.text = detail?.isPhoneCall
        tableBinding.tvTableOtherClean.text = trimText(
            when (detail?.cleanSensitivity) {
                1 -> "매우 예민하지 않아요"
                2 -> "예민하지 않아요"
                3 -> "보통이에요"
                4 -> "예민해요"
                5 -> "매우 예민해요"
                else -> "보통이에요"
            }
        )
        tableBinding.tvTableOtherNoise.text = trimText(
            when (detail?.noiseSensitivity) {
                1 -> "매우 예민하지 않아요"
                2 -> "예민하지 않아요"
                3 -> "보통이에요"
                4 -> "예민해요"
                5 -> "매우 예민해요"
                else -> "보통이에요"
            }
        )
        tableBinding.tvTableOtherCleanFrequency.text = trimText(detail?.cleaningFrequency)
        tableBinding.tvTableOtherPersonality.text = trimText(detail?.personality)
        tableBinding.tvTableOtherMbti.text = detail?.mbti
    }
}