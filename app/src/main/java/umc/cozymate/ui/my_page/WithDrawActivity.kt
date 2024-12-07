package umc.cozymate.ui.my_page

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.CompoundButtonCompat
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.R
import umc.cozymate.databinding.ActivityWithdrawBinding
import umc.cozymate.ui.splash.SplashActivity
import umc.cozymate.ui.viewmodel.SplashViewModel

@AndroidEntryPoint
class WithDrawActivity : AppCompatActivity() {
    private val TAG = this.javaClass.simpleName
    private lateinit var binding: ActivityWithdrawBinding
    private lateinit var spf : SharedPreferences
    private var reason : String = ""
    private var memberId : Int = 0
    private var nickname: String = ""
    private val viewModel : SplashViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWithdrawBinding.inflate(layoutInflater)
        spf = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        setContentView(binding.root)
        setTextContent()
        setColor()
        getPreference()
        setClickListener()
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
            spf.edit().clear().commit() // 데이터 삭제
            Log.d(TAG,spf.getString("access_token", "삭제되었습ㄴ다.").toString())
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this, SplashActivity::class.java)) // 스플래시 화면으로 복귀
            }, 200)

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
        val splitText = "탈퇴하시면 모든 정보가 사라지며,\n모든 데이터는 복구가 불가능해요"

        Log.d(TAG, "로그는 왜 안찍히니")
        // 화면의 가로 크기 확인
        val screenWidth = resources.displayMetrics.widthPixels
        val textWidth =  binding.tvSplitText.paint.measureText(originalText)
        Log.d(TAG, "로그 screen : ${screenWidth} / text ${textWidth} / result ${textWidth + 65}")

        // 화면 크기와 텍스트 길이를 비교하여 텍스트 설정
        binding.tvSplitText.text = if (textWidth+65 > screenWidth) splitText else originalText
    }
}