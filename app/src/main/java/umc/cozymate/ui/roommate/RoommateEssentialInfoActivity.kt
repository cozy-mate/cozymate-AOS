package umc.cozymate.ui.roommate

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import umc.cozymate.R
import umc.cozymate.databinding.ActivityRoommatreEssentialInfoBinding

class RoommateEssentialInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRoommatreEssentialInfoBinding
    private var selectedOption: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRoommatreEssentialInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val amTextView = binding.tvAm
        val pmTextView = binding.tvPm

        amTextView.setOnClickListener { onOptionSelected(it) }
        pmTextView.setOnClickListener { onOptionSelected(it) }
    }

    private fun onOptionSelected(view: View) {
        selectedOption?.isSelected = false
        selectedOption?.setTextColor(resources.getColor(R.color.unuse_font, null))
        selectedOption = view as TextView
        selectedOption?.isSelected = true
        selectedOption?.setTextColor(resources.getColor(R.color.main_blue, null))
    }
}