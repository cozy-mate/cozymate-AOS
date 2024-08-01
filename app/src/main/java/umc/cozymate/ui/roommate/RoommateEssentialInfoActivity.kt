package umc.cozymate.ui.roommate

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build.VERSION_CODES.S
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import umc.cozymate.R
import umc.cozymate.databinding.ActivityRoommateEssentialInfoBinding

class RoommateEssentialInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRoommateEssentialInfoBinding
    private lateinit var spf: SharedPreferences

    private var WakeAmpmOption: TextView? = null
    private var WakeAmpm: String? = null
    private var WakeTimeOption: TextView? = null
    private var WakeTime: String? = null

    private var SleepAmpmOption: TextView? = null
    private var SleepAmpm: String? = null
    private var SleepTimeOption: TextView? = null
    private var SleepTime: String? = null

    private var LightOffAmpmOption: TextView? = null
    private var LightOffAmpm: String? = null
    private var LightOffTimeOption: TextView? = null
    private var LightOffTime: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRoommateEssentialInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize SharedPreferences
        spf = getSharedPreferences("essential_info", Context.MODE_PRIVATE)

        // AM/PM 선택 처리
        binding.tvWakeAm.setOnClickListener { onWakeAmpmOptionSelected(it, "오전") }
        binding.tvWakePm.setOnClickListener { onWakeAmpmOptionSelected(it, "오후") }
        // Wake Time 선택 처리
        val wakeTimeTexts = listOf(
            binding.tvWakeup1 to "1시", binding.tvWakeup2 to "2시", binding.tvWakeup3 to "3시",
            binding.tvWakeup4 to "4시", binding.tvWakeup5 to "5시", binding.tvWakeup6 to "6시",
            binding.tvWakeup7 to "7시", binding.tvWakeup8 to "8시", binding.tvWakeup9 to "9시",
            binding.tvWakeup10 to "10시", binding.tvWakeup11 to "11시", binding.tvWakeup12 to "12시"
        )
        for ((textView, value) in wakeTimeTexts) {
            textView.setOnClickListener { onWakeTimeOptionSelected(it, value) }
        }

        // Sleep AM/PM 선택 처리
        binding.tvSleepAm.setOnClickListener { onSleepAmpmOptionSelected(it, "오전") }
        binding.tvSleepPm.setOnClickListener { onSleepAmpmOptionSelected(it, "오후") }
        // Sleep Time 선택 처리
        val sleepTimeTexts = listOf(
            binding.tvSleep1 to "1시", binding.tvSleep2 to "2시", binding.tvSleep3 to "3시",
            binding.tvSleep4 to "4시", binding.tvSleep5 to "5시", binding.tvSleep6 to "6시",
            binding.tvSleep7 to "7시", binding.tvSleep8 to "8시", binding.tvSleep9 to "9시",
            binding.tvSleep10 to "10시", binding.tvSleep11 to "11시", binding.tvSleep12 to "12시"
        )
        for ((textView, value) in sleepTimeTexts) {
            textView.setOnClickListener { onSleepTimeOptionSelected(it, value) }
        }

        // Light Off AM/PM 선택 처리
        binding.tvLightOffAm.setOnClickListener { onLightOffAmpmOptionSelected(it, "오전") }
        binding.tvLightOffPm.setOnClickListener { onLightOffAmpmOptionSelected(it, "오후") }
        // Light Off Time 선택 처리
        val lightOffTimeTexts = listOf(
            binding.tvLightOff1 to "1시", binding.tvLightOff2 to "2시", binding.tvLightOff3 to "3시",
            binding.tvLightOff4 to "4시", binding.tvLightOff5 to "5시", binding.tvLightOff6 to "6시",
            binding.tvLightOff7 to "7시", binding.tvLightOff8 to "8시", binding.tvLightOff9 to "9시",
            binding.tvLightOff10 to "10시", binding.tvLightOff11 to "11시", binding.tvLightOff12 to "12시"
        )
        for ((textView, value) in lightOffTimeTexts) {
            textView.setOnClickListener { onLightOffTimeOptionSelected(it, value) }
        }

        // Update Next button state on activity start
        updateNextButtonState()

        binding.btnNext.setOnClickListener {
            val intent = Intent(this, RoommateSelectionInfoActivity::class.java)
            startActivity(intent)
        }
    }

    private fun onWakeAmpmOptionSelected(view: View, value: String) {
        WakeAmpmOption?.apply {
            setTextColor(resources.getColor(R.color.unuse_font, null)) // 기본 색상으로 되돌리기
        }
        WakeAmpmOption = view as TextView
        WakeAmpmOption?.apply {
            setTextColor(resources.getColor(R.color.main_blue, null)) // 선택된 색상으로 변경
        }
        WakeAmpm = value
        saveWakeAmpm(value)
        updateNextButtonState() // Update the button state after selection
    }

    private fun onWakeTimeOptionSelected(view: View, value: String) {
        WakeTimeOption?.apply {
            setTextColor(resources.getColor(R.color.unuse_font, null)) // 기본 색상으로 되돌리기
            background = resources.getDrawable(R.drawable.custom_option_box_background_default, null)
        }
        WakeTimeOption = view as TextView
        WakeTimeOption?.apply {
            setTextColor(resources.getColor(R.color.main_blue, null)) // 선택된 색상으로 변경
            background = resources.getDrawable(R.drawable.custom_option_box_background_selected_6dp, null)
        }
        WakeTime = value
        saveWakeTime(value)
        updateNextButtonState() // Update the button state after selection
    }

    private fun onSleepAmpmOptionSelected(view: View, value: String) {
        SleepAmpmOption?.apply {
            setTextColor(resources.getColor(R.color.unuse_font, null)) // 기본 색상으로 되돌리기
        }
        SleepAmpmOption = view as TextView
        SleepAmpmOption?.apply {
            setTextColor(resources.getColor(R.color.main_blue, null)) // 선택된 색상으로 변경
        }
        SleepAmpm = value
        saveSleepAmpm(value)
        updateNextButtonState() // Update the button state after selection
    }

    private fun onSleepTimeOptionSelected(view: View, value: String) {
        SleepTimeOption?.apply {
            setTextColor(resources.getColor(R.color.unuse_font, null)) // 기본 색상으로 되돌리기
            background = resources.getDrawable(R.drawable.custom_option_box_background_default, null)
        }
        SleepTimeOption = view as TextView
        SleepTimeOption?.apply {
            setTextColor(resources.getColor(R.color.main_blue, null)) // 선택된 색상으로 변경
            background = resources.getDrawable(R.drawable.custom_option_box_background_selected_6dp, null)
        }
        SleepTime = value
        saveSleepTime(value)
        updateNextButtonState() // Update the button state after selection
    }

    private fun onLightOffAmpmOptionSelected(view: View, value: String) {
        LightOffAmpmOption?.apply {
            setTextColor(resources.getColor(R.color.unuse_font, null)) // 기본 색상으로 되돌리기
        }
        LightOffAmpmOption = view as TextView
        LightOffAmpmOption?.apply {
            setTextColor(resources.getColor(R.color.main_blue, null)) // 선택된 색상으로 변경
        }
        LightOffAmpm = value
        saveLightOffAmpm(value)
        updateNextButtonState() // Update the button state after selection
    }

    private fun onLightOffTimeOptionSelected(view: View, value: String) {
        LightOffTimeOption?.apply {
            setTextColor(resources.getColor(R.color.unuse_font, null)) // 기본 색상으로 되돌리기
            background = resources.getDrawable(R.drawable.custom_option_box_background_default, null)
        }
        LightOffTimeOption = view as TextView
        LightOffTimeOption?.apply {
            setTextColor(resources.getColor(R.color.main_blue, null)) // 선택된 색상으로 변경
            background = resources.getDrawable(R.drawable.custom_option_box_background_selected_6dp, null)
        }
        LightOffTime = value
        saveLightOffTime(value)
        updateNextButtonState() // Update the button state after selection
    }

    private fun saveWakeAmpm(value: String) {
        with(spf.edit()) {
            putString("wake_ampm", value)
            apply()
        }
        Log.d("Essential Info", "Selected Wake AM/PM: $value")
    }

    private fun saveWakeTime(value: String) {
        with(spf.edit()) {
            putString("wake_time", value)
            apply()
        }
        Log.d("Essential Info", "Selected Wake Time: $value")
    }

    private fun saveSleepAmpm(value: String) {
        with(spf.edit()) {
            putString("sleep_ampm", value)
            apply()
        }
        Log.d("Essential Info", "Selected Sleep AM/PM: $value")
    }

    private fun saveSleepTime(value: String) {
        with(spf.edit()) {
            putString("sleep_time", value)
            apply()
        }
        Log.d("Essential Info", "Selected Sleep Time: $value")
    }

    private fun saveLightOffAmpm(value: String) {
        with(spf.edit()) {
            putString("light_off_ampm", value)
            apply()
        }
        Log.d("Essential Info", "Selected Light Off AM/PM: $value")
    }

    private fun saveLightOffTime(value: String) {
        with(spf.edit()) {
            putString("light_off_time", value)
            apply()
        }
        Log.d("Essential Info", "Selected Light Off Time: $value")
    }

    private fun updateNextButtonState() {
        // Check if all options have been selected
        val isNextButtonEnabled = WakeAmpm != null &&
                WakeTime != null &&
                SleepAmpm != null &&
                SleepTime != null &&
                LightOffAmpm != null &&
                LightOffTime != null

        // Update button state
        binding.btnNext.isEnabled = isNextButtonEnabled
        binding.btnNext.isClickable = isNextButtonEnabled
        // Update visibility
        binding.btnNext.visibility = if (isNextButtonEnabled) View.VISIBLE else View.GONE
    }
}
