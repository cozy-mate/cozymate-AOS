package umc.cozymate.ui.cozy_home.make_room

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import umc.cozymate.databinding.ActivityCozyHomeRoomInfoBinding

class CozyHomeRoomInfoActivity : AppCompatActivity() {

    lateinit var binding: ActivityCozyHomeRoomInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCozyHomeRoomInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            btnNext.setOnClickListener {
                startActivity(Intent(baseContext, CozyHomeCharacterSelectionActivity::class.java))
            }
        }

    }

}
