package umc.cozymate.ui.cozy_home.roommate.roommate_detail

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import umc.cozymate.R
import umc.cozymate.databinding.ActivityRoommateMyDetailBinding
import umc.cozymate.databinding.ItemRoommateDetailListBinding
import umc.cozymate.ui.my_page.lifestyle.FetchLifestyleActivity
import umc.cozymate.ui.roommate.RoommateOnboardingActivity
import umc.cozymate.util.StatusBarUtil
import umc.cozymate.util.navigationHeight
import umc.cozymate.util.setStatusBarTransparent

class RoommateMyDetailActivity : AppCompatActivity() {

    private val TAG = this.javaClass.simpleName
    private lateinit var binding: ActivityRoommateMyDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoommateMyDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.setStatusBarTransparent()
        window.navigationBarColor = Color.WHITE
        StatusBarUtil.updateStatusBarColor(this@RoommateMyDetailActivity, Color.WHITE)
        binding.main.setPadding(0, 0, 0, this.navigationHeight())
        updateUI()
    }

    private fun updateUI() {
        val spf = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnFetchLifestyle.setOnClickListener {
            if (spf.getString("user_mbti", "").isNullOrEmpty()) {
                val intent = Intent(this, RoommateOnboardingActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, FetchLifestyleActivity::class.java)
                startActivity(intent)
            }
        }

        with(binding) {
            setUserProfileImage(spf.getInt("user_persona", 0))
            tvOtherUserName.text = spf.getString("user_nickname", "")
        }

        fun trimText(text: String?): String {
            return when {
                text == null -> ""
                text.length > 12 -> "${text.substring(0, 12)}.."
                else -> text
            }
        }

        val listView = LayoutInflater.from(this).inflate(R.layout.item_roommate_detail_list, null)
        val listBinding = ItemRoommateDetailListBinding.bind(listView)

        binding.listTableLayout.removeAllViews()
        binding.listTableLayout.addView(listView)

        with(listBinding) {
            guideView2.visibility = View.GONE
            tvListName.text = spf.getString("user_nickname", "")
            tvListBirth.text = spf.getString("user_birthday", "")?.substring(0, 4)
            tvListSchool.text = spf.getString("user_university_name", "")
            tvListSchoolNumber.text = spf.getString("user_admissionYear", "")
            tvListMajor.text = spf.getString("major_name", "")
            tvListDormitoryNum.text = "${spf.getInt("user_numOfRoommate", 0)}인 1실"
            tvListAcceptance.text = spf.getString("user_acceptance", "")
            tvListWakeUpAmpm.text = spf.getString("user_wakeUpMeridian", "")
            tvListWakeUpTime.text = "${spf.getInt("user_wakeUpTime", 0)}"
            tvListSleepAmpm.text = spf.getString("user_sleepingMeridian", "")
            tvListSleepTime.text = "${spf.getInt("user_sleepingTime", 0)}"
            tvListLightOffAmpm.text = spf.getString("user_turnOffMeridian", "")
            tvListLightOffTime.text = "${spf.getInt("user_turnOffTime", 0)}"
            tvListSmokeCheck.text = spf.getString("user_smoking", "")
            tvListSleepHabbit.text = trimText(
                spf.getStringSet("user_sleepingHabit", emptySet())?.toList()?.joinToString(", ")
            )
            tvListAc.text = when (spf.getInt("user_airConditioningIntensity", 3)) {
                0 -> "안 틀어요"
                1 -> "약하게 틀어요"
                2 -> "적당하게 틀어요"
                3 -> "세게 틀어요"
                else -> "적당하게 틀어요"
            }
            tvListAcHeater.text = when (spf.getInt("user_heatingIntensity", 3)) {
                0 -> "안 틀어요"
                1 -> "약하게 틀어요"
                2 -> "적당하게 틀어요"
                3 -> "세게 틀어요"
                else -> "적당하게 틀어"
            }
            tvListLivingPattern.text = spf.getString("user_lifePattern", "")
            tvListFriendly.text = spf.getString("user_intimacy", "")
            tvListShare.text = spf.getString("user_canShare", "")
            tvListStudy.text = spf.getString("user_studying", "")
            tvListIntake.text = spf.getString("user_intake", "")
            tvListGameCheck.text = spf.getString("user_isPlayGame", "")
            tvListCallCheck.text = spf.getString("user_isPhoneCall", "")
            tvListCleanCheck.text = when (spf.getInt("user_cleanSensitivity", 3)) {
                1 -> "매우 예민하지 않아요"
                2 -> "예민하지 않아요"
                3 -> "보통이에요"
                4 -> "예민해요"
                5 -> "매우 예민해요"
                else -> "보통이에요"
            }
            tvListNoiseCheck.text = when (spf.getInt("user_noiseSensitivity", 3)) {
                1 -> "매우 예민하지 않아요"
                2 -> "예민하지 않아요"
                3 -> "보통이에요"
                4 -> "예민해요"
                5 -> "매우 예민해요"
                else -> "보통이에요"
            }
            tvListCleanFrequency.text = spf.getString("user_cleaningFrequency", "")
            tvListDrinkFrequency.text = spf.getString("user_drinkingFrequency", "")
            tvListPersonalityCheck.text = trimText(
                spf.getStringSet("user_personality", emptySet())?.toList()?.joinToString(", ")
            )
            tvListMbti.text = spf.getString("user_mbti", "")
            tvSelfIntroduction.text = spf.getString("user_selfIntroduction", "")
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
}