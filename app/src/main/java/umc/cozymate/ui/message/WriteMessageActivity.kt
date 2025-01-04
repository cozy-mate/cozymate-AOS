package umc.cozymate.ui.message

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.data.model.request.ChatRequest
import umc.cozymate.databinding.ActivityWriteMessageBinding
import umc.cozymate.ui.viewmodel.ChatViewModel
import umc.cozymate.util.StatusBarUtil

@AndroidEntryPoint
class WriteMessageActivity : AppCompatActivity() {
    lateinit var binding : ActivityWriteMessageBinding
    private val TAG = this.javaClass.simpleName
    private val viewModel: ChatViewModel by viewModels()
    private var recipientId : Int = 0
    private var nickname : String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWriteMessageBinding.inflate(layoutInflater)
        StatusBarUtil.updateStatusBarColor(this, Color.WHITE)
        setContentView(binding.root)
        checkInput()
        setTextinput()
        setOnClickListener()
        recipientId = intent.getIntExtra("recipientId",0)
        nickname = intent.getStringExtra("nickname").toString()
        viewModel.postChatResponse.observe(this){response ->
            if (response.isSuccessful) {
                val intent = Intent(this, MessageDetailActivity::class.java)
                intent.putExtra("chatRoomId", response.body()!!.result.chatRoomId)
                intent.putExtra("nickname",nickname)
                startActivity(intent)
                finish()
            }
        }

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


        }
    }


}