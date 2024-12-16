package umc.cozymate.ui

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import umc.cozymate.R
import umc.cozymate.data.model.request.FcmInfoRequest
import umc.cozymate.databinding.ActivityMainBinding
import umc.cozymate.firebase.FCMService
import umc.cozymate.ui.cozy_bot.CozyBotFragment
import umc.cozymate.ui.cozy_home.home.CozyHomeMainFragment
import umc.cozymate.ui.my_page.MyPageFragment
import umc.cozymate.ui.pop_up.ServerErrorPopUp
import umc.cozymate.ui.role_rule.RoleAndRuleFragment
import umc.cozymate.ui.university_certification.UniversityCertificationFragment
import umc.cozymate.ui.viewmodel.CozyHomeViewModel
import umc.cozymate.ui.viewmodel.RoommateViewModel
import umc.cozymate.util.navigationHeight
import umc.cozymate.util.setStatusBarTransparent
import java.util.UUID

// 메인화면 진입 시 방존재 여부를 불러옵니다.
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private val homeViewModel: CozyHomeViewModel by viewModels()
    private val roommateViewModel: RoommateViewModel by viewModels()
    private lateinit var menu: Menu
    lateinit var roleAndRuleItem: MenuItem
    lateinit var cozybotItem: MenuItem
    var isRoomExist = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        menu = binding.bottomNavigationView.menu
        roleAndRuleItem = menu.findItem(R.id.fragment_role_and_rule)
        cozybotItem = menu.findItem(R.id.fragment_cozybot)
        setContentView(binding.root)
        initScreen()
        //observeLoading()
        observeRoomID()
        lifecycleScope.launch {
            homeViewModel.getRoomId()
        }
        setBottomNavigationView()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkNotificationPermission()
        }
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
                    binding.bottomNavigationView.selectedItemId = R.id.fragment_home
                }
            }
        }

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
        roommateViewModel.sendFcmInfo(fcmInfoRequest)
        Log.d("MainActivity device ID", "$_deviceId")
        Log.d("MainActivity FCM API", "${fcmInfoRequest.token}")
    }

    // 로딩중 옵저빙
    private fun observeLoading() {
        homeViewModel.isLoading.observe(this) { isLoading ->
            try {
                if (isLoading) {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.main.visibility = View.GONE
                } else {
                    binding.progressBar.visibility = View.GONE
                    binding.main.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                val errorDialog = ServerErrorPopUp.newInstance("", e.message ?: "")
                errorDialog.show(supportFragmentManager, "ServerErrorPopUp")
            }
        }
    }

    // 참여한 방이 있는지를 확인
    fun observeRoomID() {
        binding.progressBar.visibility = View.VISIBLE
        homeViewModel.roomId.observe(this) { roomId ->
            // 방이 없을 때는 롤앤룰, 코지봇 비활성화
            if (roomId == 0 || roomId == null) {
                isRoomExist = false
                binding.progressBar.visibility = View.GONE
                roleAndRuleItem.isEnabled = false
                cozybotItem.isEnabled = false
            } else {
                isRoomExist = true
                binding.progressBar.visibility = View.GONE
                roleAndRuleItem.isEnabled = true
                roleAndRuleItem.isEnabled = true
            }
        }
    }

    // 스크린 설정
    private fun initScreen() {
        this.setStatusBarTransparent()
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                )
        window.navigationBarColor = android.graphics.Color.WHITE
        binding.main.setPadding(0, 0, 0, this.navigationHeight())
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

    // 바텀 네비게이션(홈, 롤앤룰, 코지봇, 마이페이지) 설정
    fun setBottomNavigationView() {
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.fragment_home -> {
                    observeRoomID()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_container, CozyHomeMainFragment()).commit()
                    true
                }

                R.id.fragment_role_and_rule -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_container, RoleAndRuleFragment()).commit()

                    true
                }

                R.id.fragment_cozybot -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_container, CozyBotFragment()).commit()
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
                val errorDialog =
                    ServerErrorPopUp.newInstance(errorResponse.code, errorResponse.message)
                errorDialog.show(supportFragmentManager, "ServerErrorPopUp")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun checkNotificationPermission() {
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
}