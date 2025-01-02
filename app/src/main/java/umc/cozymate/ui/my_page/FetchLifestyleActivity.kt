package umc.cozymate.ui.my_page

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import umc.cozymate.R
import umc.cozymate.databinding.ActivityFetchLifestyleBinding

class FetchLifestyleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFetchLifestyleBinding
    private lateinit var spf: SharedPreferences

    // 임시 데이터 저장 변수
    private var selectedNumOfRoommate: Int = -1
    private var selectedWakeUpMeridian: String? = null
    private var selectedAcceptance: String? = null
    private var selectedSleepingHabits: MutableList<String> = mutableListOf()
    private var selectedLifePattern: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFetchLifestyleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        spf = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        // 초기화
        initNumOfRoommate(spf.getInt("user_numOfRoommate", -1))
        initWakeUpMeridian(spf.getString("user_wakeUpMeridian", null))
        initAcceptance(spf.getString("user_acceptance", null))
        initSleepingHabits(getStringListFromPreferences("user_sleepingHabit"))
        initLifePattern(spf.getString("user_lifePattern", null))

        // 뒤로가기 버튼
        binding.ivBack.setOnClickListener { finish() }

        // 저장 버튼
        binding.btnSave.setOnClickListener {
            saveAllSelections()
        }

        // 플로팅 버튼
        setFloatingButton()
    }

    private fun initNumOfRoommate(selectedValue: Int?) {
        val views = listOf(
            binding.num0, binding.num2, binding.num3,
            binding.num4, binding.num5, binding.num6
        )

        // 초기화
        views.forEach { view ->
            if (getNumFromTextView(view) == selectedValue) {
                view.setBackgroundResource(R.drawable.custom_option_box_background_selected_6dp)
                view.setTextColor(getColor(R.color.main_blue))
                selectedNumOfRoommate = selectedValue ?: -1
            } else {
                view.setBackgroundResource(R.drawable.custom_option_box_background_default)
                view.setTextColor(getColor(R.color.unuse_font))
            }
        }

        // 클릭 이벤트
        views.forEach { view ->
            view.setOnClickListener {
                updateNumOfRoommate(view, views)
            }
        }
    }

    private fun updateNumOfRoommate(view: TextView, views: List<TextView>) {
        views.forEach {
            it.setBackgroundResource(R.drawable.custom_option_box_background_default)
            it.setTextColor(getColor(R.color.unuse_font))
        }

        view.setBackgroundResource(R.drawable.custom_option_box_background_selected_6dp)
        view.setTextColor(getColor(R.color.main_blue))

        selectedNumOfRoommate = getNumFromTextView(view)
    }

    private fun getNumFromTextView(view: TextView): Int {
        return when (view.id) {
            binding.num0.id -> 0
            binding.num2.id -> 2
            binding.num3.id -> 3
            binding.num4.id -> 4
            binding.num5.id -> 5
            binding.num6.id -> 6
            else -> -1
        }
    }

    private fun initWakeUpMeridian(selectedValue: String?) {
        val views = listOf(binding.tvWakeAm, binding.tvWakePm)

        // 초기화: 저장된 값과 일치하는 TextView의 상태를 설정
        views.forEach { view ->
            if (view.text.toString() == selectedValue) {
                view.setTextColor(getColor(R.color.main_blue)) // 선택된 텍스트는 파란색
                selectedWakeUpMeridian = selectedValue
            } else {
                view.setTextColor(getColor(R.color.unuse_font)) // 기본 텍스트 색상
            }
        }

        // 클릭 이벤트 처리
        views.forEach { view ->
            view.setOnClickListener {
                updateWakeUpMeridian(view, views)
            }
        }
    }
    private fun initLifePattern(selectedValue: String?) {
        val views = listOf(binding.livingMorning, binding.livingDawn)

        views.forEach { view ->
            if (view.text.toString() == selectedValue) {
                view.setBackgroundResource(R.drawable.custom_option_box_background_selected_6dp)
                view.setTextColor(getColor(R.color.main_blue))
                selectedLifePattern = selectedValue
            } else {
                view.setBackgroundResource(R.drawable.custom_option_box_background_default)
                view.setTextColor(getColor(R.color.unuse_font))
            }
        }
        views.forEach { view ->
            view.setOnClickListener {
                updateLifePattern(view, views)
            }
        }
    }

    private fun updateLifePattern(view: TextView, views: List<TextView>) {
        views.forEach {
            it.setBackgroundResource(R.drawable.custom_option_box_background_default)
            it.setTextColor(getColor(R.color.unuse_font))
        }
        view.setBackgroundResource(R.drawable.custom_option_box_background_selected_6dp)
        view.setTextColor(getColor(R.color.main_blue))
        selectedLifePattern = view.text.toString()
    }

    private fun updateWakeUpMeridian(view: TextView, views: List<TextView>) {
        // 모든 텍스트 색상 초기화
        views.forEach { it.setTextColor(getColor(R.color.unuse_font)) }

        // 선택된 텍스트 색상 변경
        view.setTextColor(getColor(R.color.main_blue))

        // 선택된 값 저장
        selectedWakeUpMeridian = view.text.toString()
    }

    private fun initAcceptance(selectedValue: String?) {
        val views = listOf(
            binding.dormitoryPass,
            binding.dormitoryWaiting,
            binding.dormitoryNumber
        )

        views.forEach { view ->
            if (view.text.toString() == selectedValue) {
                view.setBackgroundResource(R.drawable.custom_option_box_background_selected_6dp)
                view.setTextColor(getColor(R.color.main_blue))
                selectedAcceptance = selectedValue
            } else {
                view.setBackgroundResource(R.drawable.custom_option_box_background_default)
                view.setTextColor(getColor(R.color.unuse_font))
            }
        }

        views.forEach { view ->
            view.setOnClickListener {
                updateAcceptance(view, views)
            }
        }
    }

    private fun updateAcceptance(view: TextView, views: List<TextView>) {
        views.forEach {
            it.setBackgroundResource(R.drawable.custom_option_box_background_default)
            it.setTextColor(getColor(R.color.unuse_font))
        }

        view.setBackgroundResource(R.drawable.custom_option_box_background_selected_6dp)
        view.setTextColor(getColor(R.color.main_blue))

        selectedAcceptance = view.text.toString()
    }

    private fun initSleepingHabits(selectedValues: List<String>) {
        val views = listOf(
            binding.sleepHabitNo,
            binding.sleepHabitNoise,
            binding.sleepHabitMoveSick
        )

        views.forEach { view ->
            if (selectedValues.contains(view.text.toString())) {
                view.setBackgroundResource(R.drawable.custom_option_box_background_selected_6dp)
                view.setTextColor(getColor(R.color.main_blue))
                selectedSleepingHabits.add(view.text.toString())
            } else {
                view.setBackgroundResource(R.drawable.custom_option_box_background_default)
                view.setTextColor(getColor(R.color.unuse_font))
            }
        }

        views.forEach { view ->
            view.setOnClickListener {
                updateSleepingHabits(view, views)
            }
        }
    }

    private fun updateSleepingHabits(view: TextView, views: List<TextView>) {
        if (selectedSleepingHabits.contains(view.text.toString())) {
            selectedSleepingHabits.remove(view.text.toString())
            view.setBackgroundResource(R.drawable.custom_option_box_background_default)
            view.setTextColor(getColor(R.color.unuse_font))
        } else {
            selectedSleepingHabits.add(view.text.toString())
            view.setBackgroundResource(R.drawable.custom_option_box_background_selected_6dp)
            view.setTextColor(getColor(R.color.main_blue))
        }
    }

    private fun saveAllSelections() {
        val editor = spf.edit()
        editor.putInt("user_numOfRoommate", selectedNumOfRoommate)
        editor.putString("user_wakeUpMeridian", selectedWakeUpMeridian)
        editor.putString("user_acceptance", selectedAcceptance)
        editor.putStringSet("user_sleepingHabit", selectedSleepingHabits.toSet())
        editor.apply()
        finish()
    }

    private fun getStringListFromPreferences(key: String): List<String> {
        val set = spf.getStringSet(key, emptySet())
        return set?.toList() ?: emptyList()
    }

    private fun setFloatingButton() {
        binding.fabGoTop.setOnClickListener {
            binding.nestedScrollView.smoothScrollTo(0, 0)
        }
    }
}