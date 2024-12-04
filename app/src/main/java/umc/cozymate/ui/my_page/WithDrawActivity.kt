package umc.cozymate.ui.my_page

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.databinding.ActivityWithdrawBinding

@AndroidEntryPoint
class WithDrawActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWithdrawBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWithdrawBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}