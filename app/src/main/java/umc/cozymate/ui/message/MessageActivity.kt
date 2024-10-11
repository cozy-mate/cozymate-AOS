package umc.cozymate.ui.message

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.R
import umc.cozymate.databinding.ActivityMessageBinding
@AndroidEntryPoint
class MessageActivity : AppCompatActivity(){
    lateinit var binding: ActivityMessageBinding
    private lateinit var messageAdapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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