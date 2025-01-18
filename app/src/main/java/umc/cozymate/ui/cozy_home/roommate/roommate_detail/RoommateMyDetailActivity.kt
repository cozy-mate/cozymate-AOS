package umc.cozymate.ui.cozy_home.roommate.roommate_detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import umc.cozymate.R
import umc.cozymate.databinding.ActivityRoommateMyDetailBinding
import umc.cozymate.databinding.ItemRoommateDetailListBinding
import umc.cozymate.ui.roommate.RoommateOnboardingActivity

class RoommateMyDetailActivity : AppCompatActivity() {

    private val TAG = this.javaClass.simpleName
    private lateinit var binding: ActivityRoommateMyDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoommateMyDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateUI()
        binding.btnFetchLifestyle.setOnClickListener {
            val intent = Intent(this, RoommateOnboardingActivity::class.java)
            startActivity(intent)
        }
    }

    private fun updateUI() {
        val spf = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        with(binding) {
            setUserProfileImage(spf.getInt("user_persona", 0))
            tvOtherUserName.text = spf.getString("user_nickname", "")
        }

        fun trimText(text: String?): String {
            return if (text != null && text.length > 6) {
                text.substring(0, 7) + ".."
            } else {
                text ?: ""
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
            tvListSleepHabbit.text = trimText(spf.getStringSet("user_sleepingHabit", emptySet())?.toList()?.joinToString(", "))
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