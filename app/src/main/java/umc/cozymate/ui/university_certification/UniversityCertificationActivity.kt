package umc.cozymate.ui.university_certification

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
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
        setContentView(R.layout.activity_university_certification)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

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