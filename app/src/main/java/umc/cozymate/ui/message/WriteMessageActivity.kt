package umc.cozymate.ui.message

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.view.GestureDetectorCompat
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.R
import umc.cozymate.data.model.request.ChatRequest
import umc.cozymate.databinding.ActivityWriteMessageBinding
import umc.cozymate.ui.viewmodel.MessageViewModel
import umc.cozymate.util.StatusBarUtil
import umc.cozymate.util.TextObserver

@AndroidEntryPoint
class WriteMessageActivity : AppCompatActivity() {
    lateinit var binding : ActivityWriteMessageBinding
    lateinit var mDetector: GestureDetectorCompat
    lateinit var textObserver: TextObserver

    private val TAG = this.javaClass.simpleName
    private val viewModel: MessageViewModel by viewModels()
    private var recipientId : Int = 0
    private var nickname : String = ""
    private var prev : View? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWriteMessageBinding.inflate(layoutInflater)
        textObserver = TextObserver(this, 200, binding.tvTextLengthInfo, binding.etInputMessage)

        StatusBarUtil.updateStatusBarColor(this, Color.WHITE)
        setContentView(binding.root)
        checkInput()
        setTextinput()
        setOnClickListener()
        mDetector = GestureDetectorCompat(this, SingleTapListener())
        recipientId = intent.getIntExtra("recipientId",0)
        nickname = intent.getStringExtra("nickname").toString()


        // 하드코딩하지 말란 경고 무시용
        @SuppressLint("SetTextI18n")
        binding.tvNickname.text = nickname+"님에게"
        viewModel.postChatResponse.observe(this){response ->
            if (response.isSuccessful) {
                val intent = Intent(this, MessageDetailActivity::class.java)
                intent.putExtra("chatRoomId", response.body()!!.result.chatRoomId)
                intent.putExtra("userId",recipientId)
                intent.putExtra("nickname",nickname)
                startActivity(intent)
                finish()
            }
        }

    }

    // 밖 터치시 키보드 숨기기
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if(ev?.action == MotionEvent.ACTION_UP)
            prev = currentFocus
        val result = super.dispatchTouchEvent(ev)
        ev?.let { mDetector.onTouchEvent(it) }
        return result
    }

    private inner class SingleTapListener : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapUp(e: MotionEvent): Boolean {
            if (prev is EditText) {
                val prevFocus = prev ?: return false
                val location = IntArray(2)
                prevFocus.getLocationOnScreen(location)
                val hitRect = Rect(
                    location[0],
                    location[1],
                    location[0] + prevFocus.width,
                    location[1] + prevFocus.height
                )

                if (!hitRect.contains(e.rawX.toInt(), e.rawY.toInt())) {
                    if (currentFocus is EditText && currentFocus != prevFocus) {
                        return false
                    } else {
                        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(prevFocus.windowToken, 0)
                        prevFocus.clearFocus()
                    }
                }
            }
            return super.onSingleTapUp(e)
        }
    }

    private fun checkInput(overflow: Boolean = false) {
        binding.btnInputButton.isEnabled = !(binding.etInputMessage.text.isNullOrEmpty() || overflow)
    }

    private fun setTextinput() {
        binding.etInputMessage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // 변경 전 텍스트에 대한 처리
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val overflow = textObserver.updateView()
                checkInput(overflow)
            }
            override fun afterTextChanged(s: Editable?) {
                val overflow = textObserver.updateView()
                checkInput(overflow)
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