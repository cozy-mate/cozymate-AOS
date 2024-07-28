package umc.cozymate.ui.roommate

import android.content.Intent
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

    lateinit var binding: ActivityRoommateBasicInfoBinding

    private var selectedNumPeopleOption: TextView? = null
    private var selectedLivingOption: TextView? = null

    private var selectedNumPeople: String? = null
    private var selectedLiving: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRoommateBasicInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dormitoryLayout = binding.lyDormitory
        val peopleNumLayout = binding.lyPeopleNumber
        val progressBar = binding.pbBasic

        progressBar.max = 100

        for (i in 0 until dormitoryLayout.childCount) {
            val option = dormitoryLayout.getChildAt(i) as TextView
            option.setOnClickListener {
                Log.d("Basic Info", "Living Option Clicked: ${option.text}")
                onNumPeopleOptionSelected(it)
            }
        }

        for (i in 0 until peopleNumLayout.childCount) {
            val option = peopleNumLayout.getChildAt(i) as TextView
            option.setOnClickListener {
                Log.d("Basic Info", "Num People Option Clicked: ${option.text}")
                onLivingOptionSelected(it)
            }
        }

        binding.btnNext.setOnClickListener {
            val intent = Intent(this, RoommateEssentialInfoActivity::class.java)
            startActivity(intent)
        }
    }

    private fun onLivingOptionSelected(view: View) {
        selectedLivingOption?.isSelected = false
        selectedLivingOption = view as TextView
        selectedLivingOption?.isSelected = true

        selectedLiving = when (selectedLivingOption?.text.toString()) {
            "합격" -> "PASS"
            "대기중" -> "Waiting"
            "예비번호를 받았어요!" -> "Waiting_Num"
            else -> null
        }
        Log.d("Basic Info", "Selected Living: $selectedLiving")
    }

    private fun onNumPeopleOptionSelected(view: View) {
        selectedNumPeopleOption?.isSelected = false
        selectedNumPeopleOption = view as TextView
        selectedNumPeopleOption?.isSelected = true

        selectedNumPeople = when (selectedNumPeopleOption?.text.toString()) {
            "2명" -> "TWO_PEOPLE"
            "3명" -> "THREE_PEOPLE"
            "4명" -> "FOUR_PEOPLE"
            "5명" -> "Five"
            "6명" -> "Six"
            else -> null
        }
        Log.d("Basic Info", "Selected Num People: $selectedNumPeople")
    }
}