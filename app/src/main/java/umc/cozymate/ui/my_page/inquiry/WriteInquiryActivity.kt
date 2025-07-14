package umc.cozymate.ui.my_page.inquiry
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.databinding.ActivityWriteInquiryBinding
import umc.cozymate.ui.viewmodel.InquiryViewModel
import umc.cozymate.util.StatusBarUtil
import umc.cozymate.util.TextObserver

@AndroidEntryPoint
class WriteInquiryActivity: AppCompatActivity() {
    private val TAG = this.javaClass.simpleName
    lateinit var binding : ActivityWriteInquiryBinding
    lateinit var textObserver: TextObserver
    private val viewModel : InquiryViewModel by viewModels()
    private var content : String = ""
    private var email : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWriteInquiryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        StatusBarUtil.updateStatusBarColor(this, Color.WHITE)
        textObserver = TextObserver(this, 200, binding.tvTextLengthInfo, binding.etInputContent)
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
        viewModel.errorType.observe (this, Observer{
            if(it == null) return@Observer
            val message = if (it == 0) "이메일 형식이 올바르지 않아요" else "네트워크 연결 상태를 확인해주세요"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        })
    }


    private fun setTextListener(){
        binding.etInputContent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val overflow = textObserver.updateView()
                checkInput(overflow)
            }
            override fun afterTextChanged(p0: Editable?) {
                val overflow = textObserver.updateView()
                checkInput(overflow)
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

    private fun checkInput(overflow: Boolean = false) {
        binding.btnInputButton.isEnabled = !(binding.etInputContent.text.isNullOrEmpty() ||binding.etInputEmail.text.isNullOrEmpty() || overflow)
    }


}