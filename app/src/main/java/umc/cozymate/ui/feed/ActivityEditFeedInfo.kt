package umc.cozymate.ui.feed

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.databinding.ActivityEditFeedInfoBinding
import umc.cozymate.ui.viewmodel.FeedViewModel
import umc.cozymate.util.StatusBarUtil


@AndroidEntryPoint
class ActivityEditFeedInfo: AppCompatActivity() {
    private val TAG = this.javaClass.simpleName
    private lateinit var binding : ActivityEditFeedInfoBinding
    private val viewModel : FeedViewModel by viewModels()
    private var name : String = ""
    private var description : String  = ""
    private var roomId : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditFeedInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        roomId = intent.getIntExtra("roomId",0)
        StatusBarUtil.updateStatusBarColor(this, Color.WHITE)
    }

    override fun onStart() {
        super.onStart()
        setTextListener()
        setupObserver()
        initOnClickListener()

    }

    private fun setupObserver() {

    }

    private fun  initOnClickListener(){
        binding.btnInputButton.setOnClickListener{
            name = binding.etFeedName.text.toString()
            description = binding.etInputDescription.text.toString()
            viewModel.editFeedInfo(roomId, name,description)
        }

        binding.ivBack.setOnClickListener {
            finish()
        }
    }

    private fun setTextListener() {
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