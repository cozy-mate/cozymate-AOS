package umc.cozymate.ui.university_certification

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.R
import umc.cozymate.ui.viewmodel.UniversityViewModel
import umc.cozymate.util.StatusBarUtil
import umc.cozymate.util.setStatusBarTransparent

@AndroidEntryPoint
class UniversityCertificationActivity : AppCompatActivity() {
    private val viewModel: UniversityViewModel by viewModels()

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
            loadUniversityInfoFragment()
        } else {
            loadUniversityCertificationFragment()
        }
    }

    fun loadUniversityInfoFragment() {
        val fragment = UniversityCertificationInfoFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_university_cert, fragment)
            .addToBackStack(null)
            .commit()
    }

    fun loadUniversityCertificationFragment() {
        val fragment = UniversityCertificationFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_university_cert, fragment)
            .addToBackStack(null)
            .commit()
    }
}