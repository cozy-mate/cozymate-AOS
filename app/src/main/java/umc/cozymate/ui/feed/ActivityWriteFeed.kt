package umc.cozymate.ui.feed

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.databinding.ActivityWriteFeedBinding
import umc.cozymate.ui.viewmodel.FeedViewModel
import umc.cozymate.util.StatusBarUtil

@AndroidEntryPoint
class ActivityWriteFeed : AppCompatActivity() {
    private val TAG  = this.javaClass.simpleName
    lateinit var binding : ActivityWriteFeedBinding
    lateinit var spf : SharedPreferences
    private var content : String = ""
    private val viewModel : FeedViewModel by viewModels()
    private var roomId : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWriteFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        roomId = intent.getIntExtra("roomId",0)
        initOnClickListener()
        StatusBarUtil.updateStatusBarColor(this, Color.WHITE)
    }

    private fun  initOnClickListener(){
        binding.btnInputButton.setOnClickListener{
            content = binding.etInputContent.text.toString()

        }

        binding.ivBack.setOnClickListener {
            finish()
        }

    }
}