package umc.cozymate.ui

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.R
import umc.cozymate.data.model.request.FcmInfoRequest
import umc.cozymate.databinding.ActivityMainBinding
import umc.cozymate.firebase.FCMService
import umc.cozymate.ui.cozy_bot.CozyBotFragment
import umc.cozymate.ui.cozy_home.home.CozyHomeMainFragment
import umc.cozymate.ui.feed.FeedFragment
import umc.cozymate.ui.my_page.MyPageFragment
import umc.cozymate.ui.pop_up.ServerErrorPopUp
import umc.cozymate.ui.role_rule.RoleAndRuleFragment
import umc.cozymate.ui.university_certification.UniversityCertificationFragment
import umc.cozymate.ui.viewmodel.CozyHomeViewModel
import umc.cozymate.ui.viewmodel.RoommateViewModel
import umc.cozymate.util.navigationHeight
import umc.cozymate.util.setStatusBarTransparent
import java.util.UUID

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    private val homeViewModel: CozyHomeViewModel by viewModels()
    private val roommateViewModel: RoommateViewModel by viewModels()

    var isRoomExist = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setBottomNavigationView()
        window.navigationBarColor = getResources().getColor(R.color.white)
        initScreen()

        // 시연용 : 네이버 로그인 버튼 클릭 > 코지홈 비활성화 화면으로
        val showCozyDefault = intent.getBooleanExtra("SHOW_COZYHOME_DEFAULT_FRAGMENT", false)
        if (showCozyDefault) {
            isRoomExist = false
        }

        homeViewModel.getRoomId()
        homeViewModel.fetchMyPreference()

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
        roommateViewModel.sendFcmInfo(accessToken!!, fcmInfoRequest)
        Log.d("MainActivity device ID", "$_deviceId")
        Log.d("MainActivity FCM API", "${fcmInfoRequest.token}")
    }

    fun observeRoomID() {
        // 현재 참여 중인 방이 있다면, CozyHomeActiveFragment로 이동
        binding.progressBar.visibility = View.VISIBLE
        homeViewModel.roomId.observe(this) { roomId ->
            if (roomId == 0 || roomId == null) {
                isRoomExist = false
                //loadDefaultFragment()
                binding.progressBar.visibility = View.GONE
            } else {
                isRoomExist = true
                //loadActiveFragment()
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun initScreen() {
        this.setStatusBarTransparent()
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                )
        window.setNavigationBarColor(android.graphics.Color.WHITE)
        binding.main.setPadding(0, 0, 0, this.navigationHeight())
    }

    private fun switchToRoommateOnboardingFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, UniversityCertificationFragment()) // RoommateOnboardingFragment()
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

    fun setBottomNavigationView() {
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.fragment_home -> {
                    observeRoomID()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_container, CozyHomeMainFragment()).commit()
                    true
                }

                R.id.fragment_feed -> {
                    if (!isRoomExist) {
                        Toast.makeText(this, "방에 참여해야지 사용할 수 있어요!", Toast.LENGTH_SHORT).show()
                        return@setOnItemSelectedListener false // 선택을 막음
                    } else {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.main_container, FeedFragment()).commit()
                    }
                    true
                }

                R.id.fragment_role_and_rule -> {
                    if (!isRoomExist) {
                        Toast.makeText(this, "방에 참여해야지 사용할 수 있어요!", Toast.LENGTH_SHORT).show()
                        return@setOnItemSelectedListener false // 선택을 막음
                    } else {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.main_container, RoleAndRuleFragment()).commit()
                    }
                    true
                }

                R.id.fragment_cozybot -> {
                    if (!isRoomExist) {
                        Toast.makeText(this, "방에 참여해야지 사용할 수 있어요!", Toast.LENGTH_SHORT).show()
                        return@setOnItemSelectedListener false // 선택을 막음
                    } else {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.main_container, CozyBotFragment()).commit()
                    }
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
