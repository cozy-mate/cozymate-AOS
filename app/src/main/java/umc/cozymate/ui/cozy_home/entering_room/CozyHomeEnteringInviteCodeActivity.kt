package umc.cozymate.ui.cozy_home.entering_room

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import umc.cozymate.R
import umc.cozymate.databinding.ActivityCozyHomeEnteringInviteCodeBinding
import umc.cozymate.util.navigationHeight
import umc.cozymate.util.setStatusBarTransparent

class CozyHomeEnteringInviteCodeActivity : AppCompatActivity() {

    lateinit var binding: ActivityCozyHomeEnteringInviteCodeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCozyHomeEnteringInviteCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //initScreen()

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
