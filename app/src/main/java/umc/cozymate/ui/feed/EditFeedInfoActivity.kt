package umc.cozymate.ui.feed

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.databinding.ActivityEditFeedInfoBinding
import umc.cozymate.ui.viewmodel.FeedViewModel
import umc.cozymate.util.StatusBarUtil


@AndroidEntryPoint
class EditFeedInfoActivity: AppCompatActivity() {
    private val TAG = this.javaClass.simpleName
    private lateinit var binding : ActivityEditFeedInfoBinding
    private val viewModel : FeedViewModel by viewModels()
    private var name : String? = ""
    private var description : String?  = ""
    private var roomId : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditFeedInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getIntentData()
        StatusBarUtil.updateStatusBarColor(this, Color.WHITE)
    }

    override fun onStart() {
        super.onStart()
        setTextListener()
        setupObserver()
        initOnClickListener()
        checkInput()

    }

    private fun getIntentData(){
        roomId = intent.getIntExtra("roomId",0)
        name = intent.getStringExtra("feed_name")
        description = intent.getStringExtra("feed_description")
        Log.d(TAG, " roomId : ${roomId} name: ${name} description : ${description}")
    }

    private fun setupObserver() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            finish()
        }
    }

    private fun  initOnClickListener(){
        binding.btnInputButton.setOnClickListener{
            name = binding.etFeedName.text.toString()
            description = binding.etInputDescription.text.toString()
            viewModel.editFeedInfo(roomId, name!!,description!!)
        }

        binding.ivBack.setOnClickListener {
            finish()
        }
    }

    private fun setTextListener() {
        if (!name.isNullOrBlank()) binding.etFeedName.setText(name)
        binding.etInputDescription.setText(description)
        binding.etFeedName.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {checkInput()}
            override fun afterTextChanged(p0: Editable?) {
                checkInput()
            }
        })
        binding.etInputDescription.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {checkInput()}
            override fun afterTextChanged(p0: Editable?) {
                checkInput()
            }
        })
    }

    private fun checkInput() {
        binding.btnInputButton.isEnabled = !(binding.etFeedName.text.isNullOrEmpty() || binding.etInputDescription.text.isNullOrEmpty())
    }
}