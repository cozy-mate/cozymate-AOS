package umc.cozymate.ui.message

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.R
import umc.cozymate.databinding.ActivityMessageBinding
import umc.cozymate.util.StatusBarUtil

@AndroidEntryPoint
class MessageActivity : AppCompatActivity(){
    private lateinit var binding: ActivityMessageBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtil.updateStatusBarColor(this@MessageActivity, Color.WHITE)
        binding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initFragment()

    }
    private fun initFragment() {
        val fragment = MessageMemberFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.layout_container, fragment) // layout_container는 activity_main.xml에 있는 FrameLayout 등의 컨테이너
            .commit()
    }

}