package umc.cozymate.ui.cozy_home.entering_room

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import umc.cozymate.databinding.ActivityCozyHomeEnteringInviteCodeBinding
import umc.cozymate.util.navigationHeight
import umc.cozymate.util.setStatusBarTransparent

class CozyHomeEnteringInviteCodeActivity : AppCompatActivity() {

    lateinit var binding: ActivityCozyHomeEnteringInviteCodeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCozyHomeEnteringInviteCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initScreen()

        with(binding){
            btnNext.setOnClickListener {
                // startActivity(Intent(baseContext, CozyHomeWaitingActivity::class.java))
            }
        }
    }

    private fun initScreen() {
        this.setStatusBarTransparent()
        binding.mainCl.setPadding(0, 0, 0, this.navigationHeight())
    }

}
