package umc.cozymate.ui.message

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import umc.cozymate.databinding.ActivityWriteMessageBinding

class WriteMessageActivity : AppCompatActivity() {
    lateinit var binding : ActivityWriteMessageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWriteMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivClose.setOnClickListener {
            finish()
        }

    }



}