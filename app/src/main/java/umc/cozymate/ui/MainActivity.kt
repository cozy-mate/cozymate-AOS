package umc.cozymate.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import umc.cozymate.R
import umc.cozymate.databinding.ActivityMainBinding
import umc.cozymate.firebase.FCMService
import umc.cozymate.ui.cozy_home.CozyHomeActiveFragment
import umc.cozymate.ui.cozy_home.CozyHomeDefaultFragment
import umc.cozymate.ui.feed.FeedFragment
import umc.cozymate.ui.my_page.MyPageFragment
import umc.cozymate.ui.role_rule.RoleAndRuleFragment
import umc.cozymate.ui.roommate.RoommateFragment
import umc.cozymate.util.navigationHeight
import umc.cozymate.util.setStatusBarTransparent

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setBottomNavigationView()

        initScreen()

        // 앱 초기 실행 시 홈화면으로 설정
        if (savedInstanceState == null) {
            binding.bottomNavigationView.selectedItemId = R.id.fragment_home


        }

        FCMService().getFirebaseToken()
        // 알림 확인을 위해 작성, 추후 삭제 요망
    }

    private fun initScreen() {
        this.setStatusBarTransparent()
        binding.main.setPadding(0, 0, 0, this.navigationHeight())
    }


    fun setBottomNavigationView() {
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.fragment_home -> {

                    // 방생성한 적이 없으면 -> default home
                    // 방생성했으면 -> active home
                    val isActiveHome = intent.getStringExtra("isActive")
                    if (isActiveHome != null){
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.main_container, CozyHomeActiveFragment.newInstance(isActiveHome)).commit()
                    } else {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.main_container, CozyHomeDefaultFragment()).commit()
                    }

                    true
                }

                R.id.fragment_feed -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_container, FeedFragment()).commit()
                    true
                }

                R.id.fragment_role_and_rule -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_container, RoleAndRuleFragment()).commit()
                    true
                }

                R.id.fragment_rommate -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_container, RoommateFragment()).commit()
                    true
                }

                R.id.fragment_mypage -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_container, MyPageFragment()).commit()
                    true
                }

                else -> false
            }
        }
    }
}
