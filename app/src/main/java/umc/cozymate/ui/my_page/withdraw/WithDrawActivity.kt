package umc.cozymate.ui.my_page.withdraw

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import androidx.core.widget.CompoundButtonCompat
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.R
import umc.cozymate.databinding.ActivityWithdrawBinding
import umc.cozymate.ui.splash.SplashActivity
import umc.cozymate.ui.viewmodel.SplashViewModel
import umc.cozymate.util.StatusBarUtil

@AndroidEntryPoint
class WithDrawActivity : AppCompatActivity() {
    private val TAG = this.javaClass.simpleName
    private lateinit var binding: ActivityWithdrawBinding
    private lateinit var spf : SharedPreferences
    private lateinit var mDetector: GestureDetectorCompat
    private var reason : String = ""
    private var memberId : Int = 0
    private var nickname: String = ""
    private val viewModel : SplashViewModel by viewModels()
    private var prev : View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWithdrawBinding.inflate(layoutInflater)
        spf = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        mDetector = GestureDetectorCompat(this, SingleTapListener())

        setContentView(binding.root)
        StatusBarUtil.updateStatusBarColor(this, Color.WHITE) // 상단바 색상 수정
        setTextContent()
        setColor()
        getPreference()
        setUpObserver()
        setClickListener()
    }

    private fun setUpObserver() {
        viewModel.withdrawResponse.observe(this, Observer { response->
            if (response == null) return@Observer
            if(response.isSuccessful){
                spf.edit().clear().commit() // 데이터 삭제
                Log.d(TAG,spf.getString("access_token", "삭제되었습니다.").toString())
                startActivity(Intent(this, SplashActivity::class.java)) // 스플래시 화면으로 복귀
            }
        })

        viewModel.loading.observe(this){
            binding.progressBar.visibility = if(it) View.VISIBLE else View.GONE
        }
    }

    private fun getPreference() {
        memberId =  spf.getInt("user_member_id", 0)
        nickname =  spf.getString("user_nickname", "No user found").toString()
        binding.tvNickname.text = nickname+"님,"
        binding.tvNickname2.text = nickname+"님,"
        Log.d(TAG,"id : ${memberId} / nickname ${nickname}")
    }


    private fun setClickListener(){
        binding.btnAgree.setOnCheckedChangeListener { box, isChecked ->
            binding.btnInputButton.isEnabled = isChecked
            setColor(isChecked)
        }

        binding.btnInputButton.setOnClickListener {
            reason = binding.etInputReasons.text.toString()
            Log.d(TAG,"reason ${reason}")
            viewModel.deleteMember(reason)
        }
        binding.ivBack.setOnClickListener {
            finish()
        }
    }

    private fun setColor( isChecked : Boolean  = false){
        val color = if(isChecked) binding.root.context.getColor(R.color.main_blue)
        else binding.root.context.getColor(R.color.unuse_font)
        binding.btnAgree.setTextColor(color)
        CompoundButtonCompat.setButtonTintList(binding.btnAgree, ColorStateList.valueOf(color))
    }

    // 화면 크기에 따라 텍스트 변경
    private fun setTextContent() {
        val originalText = "탈퇴하시면 모든 정보가 사라지며, 모든 데이터는 복구가 불가능해요"
        val splitText = "탈퇴하시면 모든 정보가 사라지며,모든 데이터는 복구가 \n불가능해요"

        // 화면의 가로 크기 확인
        val screenWidth = resources.displayMetrics.widthPixels
        val textWidth =  binding.tvSplitText.paint.measureText(originalText)
        val padding = this.resources.displayMetrics.density*65
        Log.d(TAG, "로그 screen : ${screenWidth} / text ${textWidth} / result ${textWidth + padding}")

        // 화면 크기와 텍스트 길이를 비교하여 텍스트 설정
        binding.tvSplitText.text = if (textWidth+padding > screenWidth) splitText else originalText
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
}