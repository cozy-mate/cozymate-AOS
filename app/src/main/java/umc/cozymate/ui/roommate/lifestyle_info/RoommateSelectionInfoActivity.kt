package umc.cozymate.ui.roommate.lifestyle_info

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import umc.cozymate.databinding.ActivityRoommateSelectionInfoBinding
import umc.cozymate.ui.roommate.RoommateDetailActivity

class RoommateSelectionInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRoommateSelectionInfoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRoommateSelectionInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnNext.setOnClickListener {
            val intent = Intent(this, RoommateDetailActivity::class.java)
            startActivity(intent)
        }
    }
}