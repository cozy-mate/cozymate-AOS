package umc.cozymate.ui.roommate

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import umc.cozymate.R
import umc.cozymate.databinding.ActivityMainBinding
import umc.cozymate.databinding.ActivityRoommateBasicInfoBinding
import umc.cozymate.databinding.ActivityRoommateEssentialInfoBinding
import umc.cozymate.databinding.FragmentRoommateBinding

class RoommateBasicInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRoommateBasicInfoBinding
    private lateinit var spf: SharedPreferences

    private var onLivingOption: TextView? = null
    // 선택한 텍스트 뷰
    private var onLiving: String? = null
    // 선택한 텍스트 저장
    private var numPeopleOption: TextView? = null
    private var numPeople: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRoommateBasicInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // SharedPreferences 선언
        spf = getSharedPreferences("basic_info", Context.MODE_PRIVATE)

        val onLivingTexts = listOf(
            binding.dormitoryPass to "합격", binding.dormitoryWaiting to "대기중", binding.dormitoryNumber to "예비번호"
        )
        // 서버로 보낼 때 양식에 맞게 수정해야함
        for ((textView, value) in onLivingTexts) {
            textView.setOnClickListener { onLivingOptionSelected(it, value) }
        }

        val numPeopleTexts = listOf(
            binding.num2 to "2명", binding.num3 to "3명", binding.num4 to "4명",
            binding.num5 to "5명", binding.num6 to "6명"
        )
        for ((textView, value) in numPeopleTexts) {
            textView.setOnClickListener { numPeopleSelected(it, value) }
        }

        binding.btnNext.setOnClickListener {
            val intent = Intent(this, RoommateEssentialInfoActivity::class.java)
            startActivity(intent)
        }
    }

    private fun onLivingOptionSelected(view: View, value: String) {
        onLivingOption?.apply{
            setTextColor(resources.getColor(R.color.unuse_font, null))
            background = resources.getDrawable(R.drawable.custom_option_box_background_default, null)
        }
        onLivingOption = view as TextView
        onLivingOption?.apply {
            setTextColor(resources.getColor(R.color.main_blue, null))
            background = resources.getDrawable(R.drawable.custom_option_box_background_selected_6dp)
        }
        onLiving = value
        saveOnLiving(value)
    }

    private fun saveOnLiving(value: String) {
        with(spf.edit()) {
            putString("on_living", value)
            apply()
        }
        Log.d("Basic Info", "On Living: $value")
    }

    private fun numPeopleSelected(view: View, value: String) {
        numPeopleOption?.apply{
            setTextColor(resources.getColor(R.color.unuse_font, null))
            background = resources.getDrawable(R.drawable.custom_option_box_background_default, null)
        }
        numPeopleOption = view as TextView
        numPeopleOption?.apply {
            setTextColor(resources.getColor(R.color.main_blue, null))
            background = resources.getDrawable(R.drawable.custom_option_box_background_selected_6dp)
        }
        numPeople = value
        saveNumPeople(value)
    }

    private fun saveNumPeople(value: String) {
        with(spf.edit()){
            putString("num_people", value)
            apply()
        }
        Log.d("Basic Info", "Num People: $value")
    }
}