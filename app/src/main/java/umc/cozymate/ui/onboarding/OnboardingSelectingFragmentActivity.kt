package umc.cozymate.ui.onboarding

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.R
import umc.cozymate.ui.cozy_home.room.making_room.GivingInviteCodeFragment
import umc.cozymate.ui.roommate.RoommateOnboardingActivity
import umc.cozymate.ui.viewmodel.OnboardingViewModel

@AndroidEntryPoint
class OnboardingSelectingFragmentActivity : AppCompatActivity() {

    private val TAG = this.javaClass.simpleName
    private val viewModel: OnboardingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_onboarding)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.navigationBarColor = Color.WHITE

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_onboarding, OnboardingSelectingPreferenceFragment())
                .commit()
        }

    }

    companion object {
        val ARG_TARGET_FRAGMENT = "TARGET_FRAGMENT"
        val PREF_FRAGMENT = "prefFragment"
    }
    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val targetFragment = result.data?.getStringExtra(ARG_TARGET_FRAGMENT)
                if (targetFragment == PREF_FRAGMENT) {
                    // 선호 칩 선택 프래그먼트로 전환
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_onboarding, OnboardingSelectingPreferenceFragment())
                        .commit()
                }
            }
        }

    fun loadRoommateOnboardingActivity(nickname: String?) {
        val intent = Intent(this, RoommateOnboardingActivity::class.java)
        intent.putExtra("nickname", nickname)
        launcher.launch(intent)
    }

}