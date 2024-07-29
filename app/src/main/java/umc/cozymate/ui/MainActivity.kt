package umc.cozymate.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import umc.cozymate.R
import umc.cozymate.databinding.ActivityMainBinding
import umc.cozymate.firebase.FCMService
import umc.cozymate.ui.cozy_home.CozyHomeDefaultFragment
import umc.cozymate.ui.feed.FeedFragment
import umc.cozymate.ui.my_page.MyPageFragment
import umc.cozymate.ui.role_rule.RoleAndRuleFragment
import umc.cozymate.ui.roommate.RoommateFragment

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setBottomNavigationView()

        // 화면 영역 확장
        //enableEdgeToEdge()
        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }*/

        // 앱 초기 실행 시 홈화면으로 설정
        if (savedInstanceState == null) {
            binding.bottomNavigationView.selectedItemId = R.id.fragment_home
        }

        FCMService().getFirebaseToken()
        // 알림 확인을 위해 작성, 추후 삭제 요망
    }


    fun setBottomNavigationView() {
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.fragment_home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_container, CozyHomeDefaultFragment()).commit()
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
