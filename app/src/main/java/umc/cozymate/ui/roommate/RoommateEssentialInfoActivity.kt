package umc.cozymate.ui.roommate

import android.content.Context
import android.content.SharedPreferences
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

    private var selectedWakeTimeOption: TextView? = null
    private var selectedWakeTime: String? = null
    private var selectedAmpmOption: TextView? = null
    private var selectedAmpm: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRoommateEssentialInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize SharedPreferences
        spf = getSharedPreferences("essential_info", Context.MODE_PRIVATE)

        // AM/PM 선택 처리
        binding.tvWakeAm.setOnClickListener { onWakeAmpmOptionSelected(it, "오전") }
        binding.tvWakePm.setOnClickListener { onWakeAmpmOptionSelected(it, "오후") }

        // 1~12 시간 선택 처리
        val wakeTimeTexts = listOf(
            binding.tvWakeup1 to "1시", binding.tvWakeup2 to "2시", binding.tvWakeup3 to "3시",
            binding.tvWakeup4 to "4시", binding.tvWakeup5 to "5시", binding.tvWakeup6 to "6시",
            binding.tvWakeup7 to "7시", binding.tvWakeup8 to "8시", binding.tvWakeup9 to "9시",
            binding.tvWakeup10 to "10시", binding.tvWakeup11 to "11시", binding.tvWakeup12 to "12시"
        )
        for ((textView, value) in wakeTimeTexts) {
            textView.setOnClickListener { onWakeTimeOptionSelected(it, value) }
        }
    }

    private fun onWakeAmpmOptionSelected(view: View, value: String) {
        // Deselect previous option
        selectedAmpmOption?.apply {
            setTextColor(resources.getColor(R.color.unuse_font, null)) // 기본 색상으로 되돌리기
        }

        // Select new option
        selectedAmpmOption = view as TextView
        selectedAmpmOption?.apply {
            setTextColor(resources.getColor(R.color.main_blue, null)) // 선택된 색상으로 변경
        }

        // Save the selected AM/PM value to SharedPreferences
        selectedAmpm = value
        saveSelectedAmpm(value)
    }

    private fun onWakeTimeOptionSelected(view: View, value: String) {
        // Deselect previous option
        selectedWakeTimeOption?.apply {
            setTextColor(resources.getColor(R.color.unuse_font, null)) // 기본 색상으로 되돌리기
            background = resources.getDrawable(R.drawable.custom_option_box_background_default, null)
        }

        // Select new option
        selectedWakeTimeOption = view as TextView
        selectedWakeTimeOption?.apply {
            setTextColor(resources.getColor(R.color.main_blue, null)) // 선택된 색상으로 변경
            background = resources.getDrawable(R.drawable.custom_option_box_background_selected_6dp, null)
        }

        // Save the selected wake time value to SharedPreferences
        selectedWakeTime = value
        saveSelectedWakeTime(value)
    }

    private fun saveSelectedAmpm(value: String) {
        with(spf.edit()) {
            putString("wake_ampm", value)
            apply()
        }
        Log.d("Essential Info", "Selected AM/PM: $value")
    }

    private fun saveSelectedWakeTime(value: String) {
        with(spf.edit()) {
            putString("wake_time", value)
            apply()
        }
        Log.d("Essential Info", "Selected Wake Time: $value")
    }
}