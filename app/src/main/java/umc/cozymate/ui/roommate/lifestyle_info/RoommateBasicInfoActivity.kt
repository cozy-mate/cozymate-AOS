package umc.cozymate.ui.roommate.lifestyle_info

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import umc.cozymate.R
import umc.cozymate.databinding.ActivityRoommateBasicInfoBinding

class RoommateBasicInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRoommateBasicInfoBinding
    private lateinit var spf: SharedPreferences

    private var onLivingOption: TextView? = null
    private var onLiving: String? = null
    private var numPeopleOption: TextView? = null
    private var numPeople: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRoommateBasicInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        spf = getSharedPreferences("basic_info", Context.MODE_PRIVATE)

        val onLivingTexts = listOf(
            binding.dormitoryPass to "합격", binding.dormitoryWaiting to "대기중", binding.dormitoryNumber to "예비번호"
        )
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

        // Add TextWatcher to EditText fields
        binding.etMajor.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = updateNextButtonState()
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.etNumber.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = updateNextButtonState()
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.etBirth.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = updateNextButtonState()
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.etName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = updateNextButtonState()
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun onLivingOptionSelected(view: View, value: String) {
        onLivingOption?.apply {
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
        updateNextButtonState()
    }

    private fun numPeopleSelected(view: View, value: String) {
        numPeopleOption?.apply {
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
        updateNextButtonState()
    }

    private fun saveOnLiving(value: String) {
        with(spf.edit()) {
            putString("on_living", value)
            apply()
        }
        Log.d("Basic Info", "On Living: $value")
    }

    private fun saveNumPeople(value: String) {
        with(spf.edit()) {
            putString("num_people", value)
            apply()
        }
        Log.d("Basic Info", "Num People: $value")
    }

    private fun updateNextButtonState() {
        // 모든 입력 필드와 선택 옵션이 올바르게 채워졌는지 확인
        val isNextButtonEnabled = onLiving != null &&
                numPeople != null &&
                binding.etMajor.text?.toString()?.isNotEmpty() == true &&
                binding.etNumber.text?.toString()?.isNotEmpty() == true &&
                binding.etBirth.text?.toString()?.isNotEmpty() == true &&
                binding.etName.text?.toString()?.isNotEmpty() == true

        // btnNext 버튼의 활성화 상태를 업데이트
        binding.btnNext.isEnabled = isNextButtonEnabled
        binding.btnNext.isClickable = isNextButtonEnabled

        // 버튼의 visibility를 업데이트
        binding.btnNext.visibility = if (isNextButtonEnabled) View.VISIBLE else View.GONE
    }
}