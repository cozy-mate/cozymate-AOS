package umc.cozymate.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.R
import umc.cozymate.data.model.request.FcmInfoRequest
import umc.cozymate.databinding.ActivityMainBinding
import umc.cozymate.firebase.FCMService
import umc.cozymate.ui.cozy_home.CozyHomeActiveFragment
import umc.cozymate.ui.cozy_home.CozyHomeDefaultFragment
import umc.cozymate.ui.feed.FeedFragment
import umc.cozymate.ui.my_page.MyPageFragment
import umc.cozymate.ui.pop_up.ServerErrorPopUp
import umc.cozymate.ui.role_rule.RoleAndRuleFragment
import umc.cozymate.ui.roommate.RoommateFragment
import umc.cozymate.ui.roommate.RoommateMakeCrewableFragment
import umc.cozymate.ui.roommate.RoommateOnboardingFragment
import umc.cozymate.ui.viewmodel.CozyHomeViewModel
import umc.cozymate.ui.viewmodel.RoommateViewModel
import umc.cozymate.util.navigationHeight
import umc.cozymate.util.setStatusBarTransparent

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    private val homeViewModel: CozyHomeViewModel by viewModels()
    private val roommateViewModel : RoommateViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setBottomNavigationView()
        window.navigationBarColor = getResources().getColor(R.color.white)

        initScreen()

        homeViewModel.getRoomId()    //// 이 코드 추가 !!!!!
        // 현재 참여 중인 방이 있다면, CozyHomeActiveFragment로 이동
        homeViewModel.roomId.observe(this) { roomId ->
            if (roomId == 0 || roomId == null) {
                loadDefaultFragment()
                binding.bottomNavigationView.menu.findItem(R.id.fragment_role_and_rule).isEnabled = false
                binding.bottomNavigationView.menu.findItem(R.id.fragment_feed).isEnabled = false
            } else {
                //observeError()
                loadActiveFragment()
            }
        }

        // Check and fetch RoomId if needed
        // homeViewModel.fetchRoomIdIfNeeded() ///// 이 코드 삭제!!!!!

        // 앱 초기 실행 시 홈화면으로 설정
//        if (savedInstanceState == null) {
//            binding.bottomNavigationView.selectedItemId = R.id.fragment_home
//
//
//        }
//        if (savedInstanceState == null) {
//            val navigateTo = intent.getStringExtra("navigate_to")
//            if (navigateTo == "RoommateOnboarding") {
//                // RoommateOnboardingFragment로 이동
//                switchToRoommateOnboardingFragment()
//            }
//            else if (navigateTo == "RoommateMakeCrewable") {
//                switchToRoommateMakeCrewableFragment()
//            }
//            else {
//                // 기본 홈 화면 설정
//                binding.bottomNavigationView.selectedItemId = R.id.fragment_home
//            }
//        }
        if (savedInstanceState == null) {
            val navigateTo = intent.getStringExtra("navigate_to")
            Log.d("MainActivity navigation", "navigate_to value: $navigateTo")

            when (navigateTo) {
                "RoommateOnboarding" -> {
                    // RoommateOnboardingFragment로 이동
                    switchToRoommateOnboardingFragment()
                }
                "RoommateMakeCrewable" -> {
                    // RoommateMakeCrewableFragment로 이동
                    switchToRoommateMakeCrewableFragment()
                }
                else -> {
                    // 기본 홈 화면 설정
//                    binding.bottomNavigationView.selectedItemId = R.id.fragment_home
                    switchToRoommateMakeCrewableFragment()
                }
            }
        }


        FCMService().getFirebaseToken()
        // 알림 확인을 위해 작성, 추후 삭제 요망
        val spf = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val accessToken = spf.getString("access_token", "")
        val fcmSpf = getSharedPreferences("FCMtoken", Context.MODE_PRIVATE)
        val fcmToken = fcmSpf.getString("FCMtoken", "")
        val fcmInfoRequest = FcmInfoRequest(
            deviceId = "1",
            token = fcmToken ?: ""  // fcmToken이 null일 경우 빈 문자열로 처리
        )
        roommateViewModel.sendFcmInfo(accessToken!!, fcmInfoRequest)
        Log.d("MainActivity FCM API", "${fcmInfoRequest.token}")
    }

    private fun initScreen() {
        this.setStatusBarTransparent()
        binding.main.setPadding(0, 0, 0, this.navigationHeight())
    }

    fun loadDefaultFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, CozyHomeDefaultFragment())
            .addToBackStack(null)
            .commit()
    }

    fun loadActiveFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, CozyHomeActiveFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun switchToRoommateOnboardingFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, RoommateOnboardingFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun switchToRoommateMakeCrewableFragment() {
        Log.d("MainActivity navigation", "Switching to RoommateMakeCrewableFragment")
        supportFragmentManager.beginTransaction()
//            .replace(R.id.main_container, RoommateMakeCrewableFragment())
//            .replace(R.id.main_container, R.layout.fragment_roommate_make_crewable)
            .replace(R.id.main_container, RoommateMakeCrewableFragment())
            .commitAllowingStateLoss()
    }


//    fun setBottomNavigationView() {
//        binding.bottomNavigationView.setOnItemSelectedListener { item ->
//            when (item.itemId) {
//                R.id.fragment_home -> {
//                    if (homeViewModel.roomId.value == 0 || homeViewModel.roomId.value == null) {
//                        loadDefaultFragment()
//                    } else {
//                        loadActiveFragment()
//                    }
//
//                    true
//                }
//
//                R.id.fragment_feed -> {
//                    supportFragmentManager.beginTransaction()
//                        .replace(R.id.main_container, FeedFragment()).commit()
//                    true
//                }
//
//                R.id.fragment_role_and_rule -> {
//                    supportFragmentManager.beginTransaction()
//                        .replace(R.id.main_container, RoleAndRuleFragment()).commit()
//                    true
//                }
//
//                R.id.fragment_rommate -> {
//                    supportFragmentManager.beginTransaction()
//                        .replace(R.id.main_container, RoommateFragment()).commit()
//                    true
//                }
//
//                R.id.fragment_mypage -> {
//                    supportFragmentManager.beginTransaction()
//                        .replace(R.id.main_container, MyPageFragment()).commit()
//                    true
//                }
//
//                else -> false
//            }
//        }
//    }
fun setBottomNavigationView() {
    binding.bottomNavigationView.setOnItemSelectedListener { item ->
        when (item.itemId) {
            R.id.fragment_home -> {
                // homeViewModel.roomId.value가 null이거나 0일 때만 홈 화면으로 이동하게 조건을 추가
                if (homeViewModel.roomId.value == 0 || homeViewModel.roomId.value == null) {
                    loadDefaultFragment()
                } else {
                    loadActiveFragment()
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


    private fun observeError() {

        homeViewModel.errorResponse.observe(this) { errorResponse ->
            errorResponse?.let {
                val errorDialog = ServerErrorPopUp.newInstance(errorResponse.code, errorResponse.message)
                errorDialog.show(supportFragmentManager, "ServerErrorPopUp")
            }
        }
    }
}
