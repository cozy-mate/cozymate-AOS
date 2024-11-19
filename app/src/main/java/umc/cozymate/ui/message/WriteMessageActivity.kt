package umc.cozymate.ui.message

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.data.model.request.ChatRequest
import umc.cozymate.databinding.ActivityWriteMessageBinding
import umc.cozymate.ui.viewmodel.ChatViewModel

@AndroidEntryPoint
class WriteMessageActivity : AppCompatActivity() {
    lateinit var binding : ActivityWriteMessageBinding
    private val TAG = this.javaClass.simpleName
    private val viewModel: ChatViewModel by viewModels()
    private var recipientId : Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWriteMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkInput()
        setTextinput()
        setOnClickListener()
        recipientId = intent.getIntExtra("recipientId",0)

    }

    private fun checkInput() {
        val inputFlag = !binding.etInputMessage.text.isNullOrEmpty()
        binding.btnInputButton.isEnabled = inputFlag
    }

    private fun setTextinput() {
        binding.etInputMessage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // 변경 전 텍스트에 대한 처리
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkInput()
            }
            override fun afterTextChanged(s: Editable?) {
                checkInput()
            }
        })
    }

    private fun setOnClickListener() {
        binding.ivClose.setOnClickListener {
            finish()
        }

        binding.btnInputButton.setOnClickListener {
            val request = ChatRequest( binding.etInputMessage.text.toString())
            viewModel.postChat(recipientId,  request)
            viewModel.postChatResponse.observe(this){response ->
                if (response.isSuccessful) {
                    Log.d(TAG,"연결 성공 ${ request}")
                    //tabViewModel.setSelectedTab(1)
                } else {
                    Log.d(TAG,"연결 실패")
                }
            }
            finish()
        }
    }


}