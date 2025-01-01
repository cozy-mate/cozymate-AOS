package umc.cozymate.ui.my_page

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import umc.cozymate.databinding.ActivityFetchLifestyleBinding

class FetchLifestyleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFetchLifestyleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFetchLifestyleBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}