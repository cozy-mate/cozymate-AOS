package umc.cozymate.ui.feed

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.data.model.request.EditPostRequest
import umc.cozymate.databinding.ActivityWriteFeedBinding
import umc.cozymate.ui.viewmodel.FeedViewModel
import umc.cozymate.util.StatusBarUtil

@AndroidEntryPoint
class WriteFeedActivity : AppCompatActivity() {
    private val TAG  = this.javaClass.simpleName
    lateinit var binding : ActivityWriteFeedBinding
    lateinit var spf : SharedPreferences
    private var content : String? = ""
    private val viewModel : FeedViewModel by viewModels()
    private var roomId : Int = 0
    private var postId : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWriteFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getIntentData()
        initOnClickListener()
        setupObserver()
        setTextListener()
        checkInput()
        StatusBarUtil.updateStatusBarColor(this, Color.WHITE)
    }

    private fun getIntentData(){
        roomId = intent.getIntExtra("roomId",0)
        postId = intent.getIntExtra("postId",0)
        content = intent.getStringExtra("content")
    }

    private fun  initOnClickListener(){
        binding.btnInputButton.setOnClickListener{
            content = binding.etInputContent.text.toString()
            val request = EditPostRequest(roomId = roomId, postId = postId, content = content!!, imageList = emptyList() )
            if (postId == 0)viewModel.createPost(request)
            else viewModel.editPost(request)
        }

        binding.ivBack.setOnClickListener {
            finish()
        }
    }
    private fun setupObserver() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        viewModel.isSuccess.observe(this) { isSuccess ->
            if(isSuccess) finish()
        }
    }

    private fun setTextListener() {
        if(!content.isNullOrBlank()) binding.etInputContent.setText(content)
        binding.etInputContent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {checkInput()}
            override fun afterTextChanged(p0: Editable?) {
                checkInput()
            }
        })

    }

    private fun checkInput() {
        binding.btnInputButton.isEnabled = !binding.etInputContent.text.isNullOrEmpty()
    }

}