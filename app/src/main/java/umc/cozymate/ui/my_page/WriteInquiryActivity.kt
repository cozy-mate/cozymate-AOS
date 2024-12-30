package umc.cozymate.ui.my_page

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.databinding.ActivityWriteInquiryBinding
import umc.cozymate.ui.viewmodel.InquiryViewModel
import umc.cozymate.util.StatusBarUtil

@AndroidEntryPoint
class WriteInquiryActivity: AppCompatActivity() {
    private val TAG = this.javaClass.simpleName
    private lateinit var binding : ActivityWriteInquiryBinding
    private val viewModel : InquiryViewModel by viewModels()
    private var content : String = ""
    private var email : String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWriteInquiryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        StatusBarUtil.updateStatusBarColor(this, Color.WHITE)

    }

    override fun onStart() {
        super.onStart()
        setTextListener()
        setObserver()
        //viewModel.checkInquryExistance()
        binding.btnInputButton.setOnClickListener {
            content = binding.etInputContent.text.toString()
            email = binding.etInputEmail.text.toString()
            viewModel.createInquiry(content, email)
        }
        binding.ivBack.setOnClickListener { finish() }
    }

    private fun setObserver(){
        viewModel.createInquiryResponse.observe(this, Observer { response ->
            if(response == null) return@Observer
            if(response.isSuccessful){
                val intent : Intent = Intent(this, InquiryActivity::class.java)
                startActivity(intent)
                finish()
            }
        })
    }


    private fun setTextListener(){
        binding.etInputContent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                checkInput()
            }
            override fun afterTextChanged(p0: Editable?) {
                checkInput()
            }
        })
        binding.etInputEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                checkInput()
            }
            override fun afterTextChanged(p0: Editable?) {checkInput()}
        })
    }

    private fun checkInput() {
        binding.btnInputButton.isEnabled = !(binding.etInputContent.text.isNullOrEmpty() ||binding.etInputEmail.text.isNullOrEmpty())
    }


}