package umc.cozymate.ui.cozy_home.make_room

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import umc.cozymate.databinding.ActivityCozyhomeCharacterSelectionBinding
import umc.cozymate.ui.cozy_home.waiting.CozyHomeWaitingActivity

class CozyHomeCharacterSelectionActivity : AppCompatActivity() {

    lateinit var binding: ActivityCozyhomeCharacterSelectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCozyhomeCharacterSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            btnNext.setOnClickListener {
                startActivity(Intent(baseContext, CozyHomeWaitingActivity::class.java))
            }
        }
    }
}
