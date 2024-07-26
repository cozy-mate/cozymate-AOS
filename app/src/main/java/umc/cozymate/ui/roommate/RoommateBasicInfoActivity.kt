package umc.cozymate.ui.roommate

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import umc.cozymate.R

class RoommateBasicInfoActivity : AppCompatActivity() {

    private var selectedNumPeopleOption: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_roommate_basic_info)

        val numPeopleLayout: LinearLayout = findViewById(R.id.ly_dormitory)
        val livingLayout: LinearLayout = findViewById(R.id.ly_dormitory)

        for (i in 0 until numPeopleLayout.childCount) {
            val option = numPeopleLayout.getChildAt(i) as TextView
            option.setOnClickListener { onNumPeopleOptionSelected(it) }
        }
    }
    private fun onNumPeopleOptionSelected(view: View) {
        selectedNumPeopleOption?.isSelected = false
        selectedNumPeopleOption = view as TextView
        selectedNumPeopleOption?.isSelected = true
    }

}