package umc.cozymate.ui.roommate

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import umc.cozymate.databinding.ActivityMessageBinding

class MessageActivity : AppCompatActivity() {
    lateinit var binding: ActivityMessageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}