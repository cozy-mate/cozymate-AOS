package umc.cozymate.ui.my_page.my_info

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.R
import umc.cozymate.databinding.ActivityUpdateMyInfoBinding
import umc.cozymate.util.StatusBarUtil

@AndroidEntryPoint
class UpdateMyInfoActivity : AppCompatActivity() {
    lateinit var binding: ActivityUpdateMyInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateMyInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        StatusBarUtil.updateStatusBarColor(this, Color.WHITE)
        setContentView(R.layout.activity_update_my_info)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_update_my_info, UpdateMyInfoFragment())
                .commit()
        }
    }

    fun loadUpdateCharacterFragment() {
        val fragment = UpdateCharacterFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_update_my_info, fragment)
            .addToBackStack(null)
            .commit()
    }
    fun loadUpdateNicknameFragment() {
        val fragment = UpdateNicknameFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_update_my_info, fragment)
            .addToBackStack(null)
            .commit()
    }

//    fun loadUpdateMajorFragment() {
//        val fragment = UpdateMajorFragment()
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.fragment_update_my_info, fragment)
//            .addToBackStack(null)
//            .commit()
//    }

    fun loadUpdateBirthFragment() {
        val fragment = UpdateBirthFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_update_my_info, fragment)
            .addToBackStack(null)
            .commit()
    }

    fun loadUpdatePreferenceBottomSheet() {
        val fragment = UpdatePreferenceFragment()
        fragment.show(supportFragmentManager, fragment.TAG)
    }
}