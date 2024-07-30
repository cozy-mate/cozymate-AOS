package umc.cozymate.ui.cozy_home.enter_room

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import umc.cozymate.databinding.ActivityCozyHomeEnterInviteCodeBinding
import umc.cozymate.ui.cozy_home.waiting.CozyHomeWaitingActivity
import umc.cozymate.util.navigationHeight
import umc.cozymate.util.setStatusBarTransparent

class CozyHomeEnterInviteCodeActivity : AppCompatActivity() {

    lateinit var binding: ActivityCozyHomeEnterInviteCodeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCozyHomeEnterInviteCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initScreen()

        with(binding){
            btnNext.setOnClickListener {
                startActivity(Intent(baseContext, CozyHomeWaitingActivity::class.java))
            }
        }
    }

    private fun initScreen() {
        this.setStatusBarTransparent()
        binding.mainCl.setPadding(0, 0, 0, this.navigationHeight())
    }

}
