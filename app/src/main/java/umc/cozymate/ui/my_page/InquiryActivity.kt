package umc.cozymate.ui.my_page

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.R
import umc.cozymate.databinding.ActivityInquiryBinding
import umc.cozymate.ui.viewmodel.InquiryViewModel
import umc.cozymate.util.StatusBarUtil

@AndroidEntryPoint
class InquiryActivity: AppCompatActivity() {
    private val TAG = this.javaClass.simpleName
    private lateinit var binding : ActivityInquiryBinding
    private lateinit var spf : SharedPreferences
    private var exist : Boolean = false
    private var memberId : Int = 0
    private val viewModel : InquiryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInquiryBinding.inflate(layoutInflater)
        spf = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        setContentView(binding.root)
        StatusBarUtil.updateStatusBarColor(this, Color.WHITE)
        getPreference()
        viewModel.checkInquryExistance()
        viewModel.existance.observe(this, Observer { result ->
            if(result == null) return@Observer
            exist = result
            initFragment()
        })
        binding.btnInputButton.setOnClickListener {
            exist = !exist
            initFragment()
        }
    }

    private fun getPreference() {
        memberId =  spf.getInt("user_member_id", 0)
    }
    private fun initFragment(){
        var fragment = if(exist) InquiryFragment() else  WriteInquiryFragment()
        binding.btnInputButton.text = if(exist) "문의하러 가기" else "문의하러 가기"
        binding.btnInputButton.isEnabled = exist
        supportFragmentManager.beginTransaction()
            .replace(R.id.layout_container, fragment!!) // layout_container는 activity_main.xml에 있는 FrameLayout 등의 컨테이너
            .commit()
    }
}