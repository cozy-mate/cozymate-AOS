package umc.cozymate.ui

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.google.firebase.analytics.logEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import umc.cozymate.R
import umc.cozymate.data.model.request.FcmInfoRequest
import umc.cozymate.databinding.ActivityMainBinding
import umc.cozymate.firebase.FCMService
import umc.cozymate.ui.cozy_bot.CozyBotFragment
import umc.cozymate.ui.cozy_home.home.CozyHomeFragment
import umc.cozymate.ui.my_page.MyPageFragment
import umc.cozymate.ui.role_rule.RoleAndRuleFragment
import umc.cozymate.ui.university_certification.UniversityCertificationFragment
import umc.cozymate.ui.viewmodel.CozyHomeViewModel
import umc.cozymate.ui.viewmodel.RoommateViewModel
import umc.cozymate.util.StatusBarUtil
import umc.cozymate.util.navigationHeight
import umc.cozymate.util.setStatusBarTransparent
import java.util.UUID

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private val cozyHomeViewModel: CozyHomeViewModel by viewModels()
    private val roommateViewModel: RoommateViewModel by viewModels()
    private lateinit var menu: Menu
    lateinit var roleAndRuleItem: MenuItem
    lateinit var cozybotItem: MenuItem
    lateinit var cozyhomeItem: MenuItem
    lateinit var mypageItem: MenuItem
    var isRoomExist = false
    val firebaseAnalytics = Firebase.analytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setRoomIDObserver()
        fetchData()
        initFCM(savedInstanceState)
        initScreen()
        setBottomNavMenuItems()
        setBottomNavViews()
    }

    private fun initScreen() {
        this.setStatusBarTransparent()
        StatusBarUtil.updateStatusBarColor(this, Color.WHITE)
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
        window.navigationBarColor = android.graphics.Color.WHITE
        binding.main.setPadding(0, 0, 0, this.navigationHeight())
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun setRoomIDObserver() {
        cozyHomeViewModel.roomId.observe(this) { roomId ->
            if (roomId == 0 || roomId == null) {
                isRoomExist = false
                binding.progressBar.visibility = View.GONE
            } else {
                isRoomExist = true
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun fetchData() {
        lifecycleScope.launch {
            cozyHomeViewModel.getRoomId()
            roommateViewModel.getUserInfo()
        }
    }

    private fun initFCM(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkNotificationPermission()
        }
        if (savedInstanceState == null) {
            val navigateTo = intent.getStringExtra("navigate_to")
            Log.d("MainActivity navigation", "navigate_to value: $navigateTo")
            when (navigateTo) {
                "RoommateOnboarding" -> {
                    switchToRoommateOnboardingFragment()
                }

                "RoommateMakeCrewable" -> {
                    switchToRoommateMakeCrewableFragment()
                }

                else -> {
                    // 기본 홈 화면 설정
                    binding.bottomNavigationView.selectedItemId = R.id.fragment_home
                }
            }
        }
        handleFCM()
    }

    private fun switchToRoommateOnboardingFragment() {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.main_container,
                UniversityCertificationFragment()
            ) // RoommateOnboardingFragment()
            .addToBackStack(null)
            .commit()
    }

    private fun switchToRoommateMakeCrewableFragment() {
        Log.d("MainActivity navigation", "Switching to RoommateMakeCrewableFragment")
        supportFragmentManager.beginTransaction()
//            .replace(R.id.main_container, RoommateMakeCrewableFragment())
//            .replace(R.id.main_container, R.layout.fragment_roommate_make_crewable)
            .replace(R.id.main_container, UniversityCertificationFragment())
            .commitAllowingStateLoss()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkNotificationPermission() {
        val permission = android.Manifest.permission.POST_NOTIFICATIONS
        when {
            shouldShowRequestPermissionRationale(permission) -> {
                // permission denied permanently
            }
            else -> {
                requestNotificationPermission.launch(permission)
            }
        }
    }

    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

    private fun handleFCM() {
        FCMService().getFirebaseToken()
        // 알림 확인을 위해 작성, 추후 삭제 요망
        val spf = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val accessToken = spf.getString("access_token", "")
        val fcmSpf = getSharedPreferences("FCMtoken", Context.MODE_PRIVATE)
        val fcmToken = fcmSpf.getString("FCMtoken", "")
        val _deviceId = UUID.randomUUID().toString()
        val fcmInfoRequest = FcmInfoRequest(
//            deviceId = "1",
            deviceId = _deviceId,
            token = fcmToken ?: ""  // fcmToken이 null일 경우 빈 문자열로 처리
        )
        roommateViewModel.sendFcmInfo(accessToken!!, fcmInfoRequest)
        Log.d("MainActivity device ID", _deviceId)
        Log.d("MainActivity FCM API", fcmInfoRequest.token)
    }

    private fun setBottomNavMenuItems() {
        menu = binding.bottomNavigationView.menu
        roleAndRuleItem = menu.findItem(R.id.fragment_role_and_rule)
        cozybotItem = menu.findItem(R.id.fragment_cozybot)
        cozyhomeItem = menu.findItem(R.id.fragment_home)
        mypageItem = menu.findItem(R.id.fragment_mypage)
    }

    private fun setBottomNavViews() {
        var selectedItem: Int = 0
        selectedItem = R.id.fragment_home
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.fragment_home -> {
                    firebaseAnalytics.logEvent("home_nav_button_click") {
                        param("코지홈", "home_button")
                        param("바텀 네비게이션", "bottom_navigation")
                    }
                    selectedItem = item.itemId
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_container, CozyHomeFragment()).commit()
                    true
                }

                R.id.fragment_role_and_rule -> {
                    firebaseAnalytics.logEvent("role_and_rule_button_click") {
                        param("롤앤룰", "role_and_rule_button")
                        param("바텀 네비게이션", "bottom_navigation")
                    }
                    if (!isRoomExist) {
                        Toast.makeText(this, "방에 참여해야지 사용할 수 있어요!", Toast.LENGTH_SHORT).show()
                        binding.bottomNavigationView.menu.findItem(selectedItem).isChecked = true
                        binding.bottomNavigationView.menu.findItem(item.itemId).isChecked = false
                        binding.bottomNavigationView.selectedItemId = selectedItem
                        false
                    } else {
                        selectedItem = item.itemId
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.main_container, RoleAndRuleFragment()).commit()
                        true
                    }
                }

                R.id.fragment_cozybot -> {
                    firebaseAnalytics.logEvent("cozybot_nav_button_click") {
                        param("코지봇", "cozybot_button")
                        param("바텀 네비게이션", "bottom_navigation")
                    }
                    if (!isRoomExist) {
                        Toast.makeText(this, "방에 참여해야지 사용할 수 있어요!", Toast.LENGTH_SHORT).show()
                        binding.bottomNavigationView.menu.findItem(selectedItem).isChecked = true
                        binding.bottomNavigationView.menu.findItem(item.itemId).isChecked = false
                        binding.bottomNavigationView.selectedItemId = selectedItem
                        false
                    } else {
                        selectedItem = item.itemId
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.main_container, CozyBotFragment()).commit()
                        true
                    }
                }

                R.id.fragment_mypage -> {
                    firebaseAnalytics.logEvent("my_page_nav_button_click") {
                        param("마이페이지", "my_page_button")
                        param("바텀 네비게이션", "bottom_navigation")
                    }
                    selectedItem = item.itemId
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_container, MyPageFragment()).commit()
                    true
                }

                else -> false
            }
        }
    }
}