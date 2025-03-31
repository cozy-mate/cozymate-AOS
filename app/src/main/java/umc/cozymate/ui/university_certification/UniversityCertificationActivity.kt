package umc.cozymate.ui.university_certification

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.R
import umc.cozymate.util.StatusBarUtil
import umc.cozymate.util.setStatusBarTransparent

@AndroidEntryPoint
class UniversityCertificationActivity : AppCompatActivity() {
    companion object {
        const val UNIVERSITY_FLAG = "university_flag"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        this.setStatusBarTransparent()
        StatusBarUtil.updateStatusBarColor(this, Color.WHITE)
        setContentView(R.layout.activity_university_certification)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val universityFlag = intent.getBooleanExtra(UNIVERSITY_FLAG, false)
        if (universityFlag) {
            loadUniversityCertificationInfoFragment()
        } else {
            loadUniversityCertificationFragment()
        }
    }

    // [학교 정보 확인 페이지]
    // 학교인증이 이미 되어있을 때 이 페이지를 로드합니다.
    fun loadUniversityCertificationInfoFragment() {
        val fragment = UniversityCertificationInfoFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_university_cert, fragment)
            .addToBackStack(null)
            .commit()
    }

    // [학교인증 페이지]
    fun loadUniversityCertificationFragment() {
        Log.d("TAG", "학교인증 페이지 가야지....")
        val fragment = UniversityCertificationFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_university_cert, fragment)
            .addToBackStack(null)
            .commit()
    }
}