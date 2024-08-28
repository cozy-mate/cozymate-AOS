package umc.cozymate.ui.splash

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.user.UserApiClient
import dagger.hilt.android.AndroidEntryPoint
import umc.cozymate.R
import umc.cozymate.databinding.ActivitySplashBinding
import umc.cozymate.ui.MainActivity
import umc.cozymate.ui.onboarding.OnboardingActivity
import umc.cozymate.ui.pop_up.ServerErrorPopUp
import umc.cozymate.ui.viewmodel.SplashViewModel

// 로그인 >> 멤버 확인 Y >> 코지홈(MainActivity)으로 이동
// 로그인 >> 멤버 확인 N >> 온보딩(OnboadrdingActivity)으로 이동
// 로그인 N >> 로그인 실패(LoginFailActivity)로 이동 (<< 현재개발 시점에는 코지홈으로 이동하게 설정함)

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private val TAG = this.javaClass.simpleName
    lateinit var binding: ActivitySplashBinding
    private val splashViewModel: SplashViewModel by viewModels()

    val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Log.e(TAG, "카카오계정으로 로그인 실패", error)
            Toast.makeText(this@SplashActivity, "카카오계정으로 로그인 실패", Toast.LENGTH_SHORT).show()
            goLoginFail()
        } else if (token != null) {
            Log.i(TAG, "카카오계정으로 로그인 성공")
            Log.d(TAG, "accessToken: ${token.accessToken}")
            Log.d(TAG, "idToken: ${token.idToken}")
            Toast.makeText(this@SplashActivity, "카카오계정으로 로그인 성공", Toast.LENGTH_SHORT).show()
            // 로그인 후 사용자 정보를 가져옴
            getUserId()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.navigationBarColor = Color.WHITE

        // 카카오 SDK 초기화
        KakaoSdk.init(this, getString(R.string.kakao_app_key))

        binding.progressBar.visibility = View.VISIBLE

        // 뷰모델 옵저빙
        observeSignInResponse()
        observeLoading()
        observeError()

        // 자동 로그인 시도 : 유효한 토큰이 있다면 자동 로그인
        attemptAutoLogin()

        // 카카오 로그인 버튼 >> 카카오 로그인 >> 멤버 확인 >> 코지홈 또는 온보딩
        binding.btnKakaoLogin.setOnClickListener {
            openKakaoLoginPage()
        }

        // 애플 로그인 버튼 >> 코지홈 비활성화
        binding.btnAppleLogin.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("SHOW_COZYHOME_DEFAULT_FRAGMENT", true) // 플래그 또는 데이터 추가
            }
            startActivity(intent)
        }

        // 회원가입 버튼 >> 테스트 로그인 >> 온보딩
        binding.btnSignIn.setOnClickListener {
            testSignIn()
        }

    }

    private fun attemptAutoLogin() {
        binding.progressBar.visibility = View.VISIBLE
        val tokenInfo = splashViewModel.getToken()
        if (tokenInfo != null) {
            splashViewModel.memberCheck()
            splashViewModel.isMember.observe(this) { isMember ->
                if (isMember) {
                    goCozyHome() // 홈 화면으로 이동
                } else {
                    goOnboarding() // 온보딩 화면으로 이동
                }
                binding.progressBar.visibility = View.GONE
            }
        } else {
            // 로딩 숨김 (토큰이 없으면 즉시 로그인을 시도하지 않기 때문에)
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun testSignIn() {
        splashViewModel.setClientId("TEST")
        splashViewModel.setSocialType("TEST")
        splashViewModel.signIn()
    }

    private fun observeError() {
        splashViewModel.errorResponse.observe(this) { errorResponse ->
            errorResponse?.let {
                val errorDialog = ServerErrorPopUp.newInstance(errorResponse.code, errorResponse.message)
                errorDialog.show(supportFragmentManager, "ServerErrorPopUp")
            }
        }
    }

    private fun observeLoading() {
        splashViewModel.loading.observe(this) { isLoading ->
            try {
                if (isLoading) {
                    binding.progressBar.visibility = View.VISIBLE
                } else {
                    binding.progressBar.visibility = View.GONE
                    // 로딩이 완료되었을 때, 멤버 여부를 확인하고 화면 전환
                    splashViewModel.isMember.observe(this) { isMember ->
                        if (isMember) {
                            goCozyHome()
                        } else {
                            goOnboarding()
                        }
                        finish()
                    }
                }
            } catch (e: Exception) {
                val errorDialog = ServerErrorPopUp.newInstance("", e.message ?: "")
                errorDialog.show(supportFragmentManager, "ServerErrorPopUp")
            }
        }
    }

    private fun observeSignInResponse() {
        // signInResponse 관찰 >> cozymate 로그인 api 성공 >> cozymate 멤버인지 체크
        splashViewModel.signInResponse.observe(this) { result ->
            if (result.isSuccessful) {
                if (result.body()!!.isSuccess) {
                    Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "로그인 성공: ${result.body()!!.result}")
                    try {
                        splashViewModel.setTokenInfo(result.body()!!.result.tokenResponseDTO)
                        splashViewModel.saveToken()

                        splashViewModel.memberCheck()
                        /*splashViewModel.isMember.observe(this) { isMember ->
                            if (isMember) goCozyHome()
                            else goOnboarding()
                            finish()
                        }*/
                    } catch (e: Exception) {
                        goLoginFail()
                        Log.d(TAG, "토큰 저장 실패: $e")
                    }
                } else {
                    Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
                    goLoginFail()
                }
            } else {
                goLoginFail()
            }
        }
    }

    private fun goCozyHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun goOnboarding() {
        val intent = Intent(this, OnboardingActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // 온보딩 백스택에 추가 안함
        startActivity(intent)
        finish()
    }

    private fun goLoginFail() {
        val intent = Intent(this, LoginFailActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun openKakaoLoginPage() {
        // 카카오톡 또는 카카오 계정으로 로그인 시도 >>> 데모 시에는 카카오 계정으로 로그인
        try {
            /*if (UserApiClient.instance.isKakaoTalkLoginAvailable(this@SplashActivity)) {
                UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                    if (error != null) {
                        Log.e(TAG, "카카오톡으로 로그인 실패", error)
                        Toast.makeText(this@SplashActivity, "카카오톡으로 로그인 실패", Toast.LENGTH_SHORT)
                            .show()

                        // 사용자가 의도적으로 로그인을 취소한 경우(ex. 뒤로가기) 리턴
                        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                            return@loginWithKakaoTalk
                        }
                        UserApiClient.instance.loginWithKakaoAccount(
                            this@SplashActivity,
                            callback = callback
                        )
                    } else if (token != null) {
                        Log.i(TAG, "카카오톡으로 로그인 성공")
                        Log.d(TAG, "kakao accessToken: ${token.accessToken}")
                        Log.d(TAG, "kakao refreshToken: ${token.refreshToken}")
                        Toast.makeText(this@SplashActivity, "카카오톡으로 로그인 성공", Toast.LENGTH_SHORT)
                            .show()
                    }

                }
            } else*/

            // 카카오 계정으로 로그인
            UserApiClient.instance.loginWithKakaoAccount(
                this@SplashActivity,
                callback = callback
            )

        } catch (e: Exception) {
            Log.e(TAG, "${e.message}")
            Toast.makeText(this@SplashActivity, "로그인 실패 ${e.message}", Toast.LENGTH_SHORT).show()
            goLoginFail()
        }
    }

    private fun getUserId() {
        try {
            UserApiClient.instance.me { user, error ->
                if (error != null) {
                    Log.e(TAG, "사용자 정보 요청 실패", error)
                } else if (user != null) {
                    Log.i(TAG, "사용자 정보 요청 성공")
                    val userId = user.id
                    Log.d(TAG, "사용자 ID: $userId")

                    if (userId != null) {
                        splashViewModel.setClientId(userId.toString())
                        // splashViewModel.setClientId("9")
                        splashViewModel.setSocialType("KAKAO")
                        splashViewModel.signIn()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "${e.message}")
            Toast.makeText(this@SplashActivity, "로그인 실패 ${e.message}", Toast.LENGTH_SHORT)
                .show()
            goLoginFail()
        }
    }

}