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
import umc.cozymate.util.CharacterUtil
import umc.cozymate.util.StatusBarUtil
import umc.cozymate.util.navigationHeight
import umc.cozymate.util.setStatusBarTransparent
import umc.cozymate.util.PreferencesUtil


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
        binding.btnBack.setOnClickListener {
            finish()
        }
        binding.btnFetchLifestyle.setOnClickListener {
            if (PreferencesUtil.getString(this, PreferencesUtil.KEY_USER_MBTI, "").isNullOrEmpty()) {
                val intent = Intent(this, RoommateOnboardingActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, FetchLifestyleActivity::class.java)
                startActivity(intent)
            }
        }
        updateUI()
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    private fun updateUI() {
        with(binding) {
            val persona = PreferencesUtil.getInt(this@RoommateMyDetailActivity, PreferencesUtil.KEY_USER_PERSONA, 0)
            CharacterUtil.setImg(persona, binding.ivOtherUserProfile)
            tvOtherUserName.text = PreferencesUtil.getString(this@RoommateMyDetailActivity, PreferencesUtil.KEY_USER_NICKNAME, "")
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
            tvListName.text = PreferencesUtil.getString(this@RoommateMyDetailActivity, PreferencesUtil.KEY_USER_NICKNAME, "")
            tvListBirth.text = PreferencesUtil.getString(this@RoommateMyDetailActivity, PreferencesUtil.KEY_USER_BIRTHDAY, "")?.substring(0, 4)
            tvListSchool.text = PreferencesUtil.getString(this@RoommateMyDetailActivity, PreferencesUtil.KEY_USER_UNIVERSITY_NAME, "")
            tvListSchoolNumber.text = PreferencesUtil.getString(this@RoommateMyDetailActivity, PreferencesUtil.KEY_USER_ADMISSION_YEAR, "")
            tvListMajor.text = PreferencesUtil.getString(this@RoommateMyDetailActivity, PreferencesUtil.KEY_USER_MAJOR_NAME, "")
            tvListDormitoryNum.text = "${PreferencesUtil.getInt(this@RoommateMyDetailActivity, PreferencesUtil.KEY_USER_NUM_OF_ROOMMATE, 0)}인 1실"
            tvListAcceptance.text = PreferencesUtil.getString(this@RoommateMyDetailActivity, PreferencesUtil.KEY_USER_ACCEPTANCE, "")
            tvListWakeUpAmpm.text = PreferencesUtil.getString(this@RoommateMyDetailActivity, PreferencesUtil.KEY_USER_WAKE_UP_MERIDIAN, "")
            tvListWakeUpTime.text = PreferencesUtil.getInt(this@RoommateMyDetailActivity, PreferencesUtil.KEY_USER_WAKE_UP_TIME, 0).toString()
            tvListSleepAmpm.text = PreferencesUtil.getString(this@RoommateMyDetailActivity, PreferencesUtil.KEY_USER_SLEEPING_MERIDIAN, "")
            tvListSleepTime.text = PreferencesUtil.getInt(this@RoommateMyDetailActivity, PreferencesUtil.KEY_USER_SLEEPING_TIME, 0).toString()
            tvListLightOffAmpm.text = PreferencesUtil.getString(this@RoommateMyDetailActivity, PreferencesUtil.KEY_USER_TURN_OFF_MERIDIAN, "")
            tvListLightOffTime.text = PreferencesUtil.getInt(this@RoommateMyDetailActivity, PreferencesUtil.KEY_USER_TURN_OFF_TIME, 0).toString()
            tvListSmokeCheck.text = PreferencesUtil.getString(this@RoommateMyDetailActivity, PreferencesUtil.KEY_USER_SMOKING, "")
            tvListSleepHabbit.text = trimText(PreferencesUtil.getString(this@RoommateMyDetailActivity, PreferencesUtil.KEY_USER_SLEEPING_HABIT, ""))
            tvListAc.text = when (PreferencesUtil.getInt(this@RoommateMyDetailActivity, PreferencesUtil.KEY_USER_AIR_CONDITIONING_INTENSITY, 3)) {
                0 -> "안 틀어요"
                1 -> "약하게 틀어요"
                2 -> "적당하게 틀어요"
                3 -> "세게 틀어요"
                else -> "적당하게 틀어요"
            }
            tvListAcHeater.text = when (PreferencesUtil.getInt(this@RoommateMyDetailActivity, PreferencesUtil.KEY_USER_HEATING_INTENSITY, 3)) {
                0 -> "안 틀어요"
                1 -> "약하게 틀어요"
                2 -> "적당하게 틀어요"
                3 -> "세게 틀어요"
                else -> "적당하게 틀어"
            }
            tvListLivingPattern.text = PreferencesUtil.getString(this@RoommateMyDetailActivity, PreferencesUtil.KEY_USER_LIFE_PATTERN, "")
            tvListFriendly.text = PreferencesUtil.getString(this@RoommateMyDetailActivity, PreferencesUtil.KEY_USER_INTIMACY, "")
            tvListShare.text = PreferencesUtil.getString(this@RoommateMyDetailActivity, PreferencesUtil.KEY_USER_CAN_SHARE, "")
            tvListStudy.text = PreferencesUtil.getString(this@RoommateMyDetailActivity, PreferencesUtil.KEY_USER_STUDYING, "")
            tvListIntake.text = PreferencesUtil.getString(this@RoommateMyDetailActivity, PreferencesUtil.KEY_USER_INTAKE, "")
            tvListGameCheck.text = PreferencesUtil.getString(this@RoommateMyDetailActivity, PreferencesUtil.KEY_USER_IS_PLAY_GAME, "")
            tvListCallCheck.text = PreferencesUtil.getString(this@RoommateMyDetailActivity, PreferencesUtil.KEY_USER_IS_PHONE_CALL, "")
            tvListCleanCheck.text = when (PreferencesUtil.getInt(this@RoommateMyDetailActivity, PreferencesUtil.KEY_USER_CLEAN_SENSITIVITY, 3)) {
                1 -> "매우 예민하지 않아요"
                2 -> "예민하지 않아요"
                3 -> "보통이에요"
                4 -> "예민해요"
                5 -> "매우 예민해요"
                else -> "보통이에요"
            }
            tvListNoiseCheck.text = when (PreferencesUtil.getInt(this@RoommateMyDetailActivity, PreferencesUtil.KEY_USER_NOISE_SENSITIVITY, 3)) {
                1 -> "매우 예민하지 않아요"
                2 -> "예민하지 않아요"
                3 -> "보통이에요"
                4 -> "예민해요"
                5 -> "매우 예민해요"
                else -> "보통이에요"
            }
            tvListCleanFrequency.text = PreferencesUtil.getString(this@RoommateMyDetailActivity, PreferencesUtil.KEY_USER_CLEANING_FREQUENCY, "")
            tvListDrinkFrequency.text = PreferencesUtil.getString(this@RoommateMyDetailActivity, PreferencesUtil.KEY_USER_DRINKING_FREQUENCY, "")
            tvListPersonalityCheck.text = trimText(
                PreferencesUtil.getString(this@RoommateMyDetailActivity, PreferencesUtil.KEY_USER_PERSONALITY, "")
            )
            tvListMbti.text = PreferencesUtil.getString(this@RoommateMyDetailActivity, PreferencesUtil.KEY_USER_MBTI, "")
            tvSelfIntroduction.text = PreferencesUtil.getString(this@RoommateMyDetailActivity, PreferencesUtil.KEY_USER_SELF_INTRODUCTION, "")
        }
    }
}