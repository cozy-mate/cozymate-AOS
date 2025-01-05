package umc.cozymate.ui.message

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
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
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
    private var prev : View? = null
    private lateinit var mDetector: GestureDetectorCompat
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWriteMessageBinding.inflate(layoutInflater)
        StatusBarUtil.updateStatusBarColor(this, Color.WHITE)
        setContentView(binding.root)
        checkInput()
        setTextinput()
        setOnClickListener()
        mDetector = GestureDetectorCompat(this, SingleTapListener())
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

    // 밖 터치시 키보드 숨기기
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if(ev!!.action ==  MotionEvent.ACTION_UP)
            prev = currentFocus
        val result = super.dispatchTouchEvent(ev)
        mDetector.onTouchEvent(ev)
        return result
    }
    private inner class SingleTapListener : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapUp(e: MotionEvent): Boolean {
            // ACTION_UP 이벤트에서 포커스를 가진 뷰가 EditText일 때 터치 영역을 확인하여 키보드를 토글
            if (e.action == MotionEvent.ACTION_UP && prev is EditText) {
                val prevFocus = prev ?: return false
                // 포커를 가진 EditText의 터치 영역 계산
                val hitRect = Rect()
                prevFocus.getGlobalVisibleRect(hitRect)

                // 터치 이벤트가 EditText의 터치 영역에 속하지 않을 때 키보드를 숨길지 결정
                if (!hitRect.contains(e.x.toInt(), e.y.toInt())) {
                    if (currentFocus is EditText && currentFocus != prevFocus) {
                        // 터치한 영역의 뷰가 다른 EditText일 때는 키보드를 가리지 않는다.
                        return false
                    } else {
                        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        // 터치한 영역이 EditText의 터치 영역 밖이면서 다른 EditText가 아닐 때 키보드 hide
                        imm.hideSoftInputFromWindow(prevFocus.windowToken, 0)
                        prevFocus.clearFocus()
                    }
                }
            }
            return super.onSingleTapUp(e)
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