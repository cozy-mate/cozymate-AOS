package umc.cozymate.ui.splash

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import umc.cozymate.ui.pop_up.ServerErrorPopUp
import umc.cozymate.ui.university_certification.UniversityCertificationActivity
import umc.cozymate.ui.viewmodel.SplashViewModel

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private val TAG = this.javaClass.simpleName
    lateinit var binding: ActivitySplashBinding
    private val splashViewModel: SplashViewModel by viewModels()
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable

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
        // gif 뷰페이저 설정
        val adapter = GIFAdapter(this)
        binding.vpGif.adapter = adapter
        binding.dotsIndicator.attachTo(binding.vpGif)
        handler = Handler(Looper.getMainLooper())
        runnable = Runnable {
            val currentItem = binding.vpGif.currentItem
            val nextItem = if (currentItem + 1 < adapter.itemCount) currentItem + 1 else 0
            binding.vpGif.setCurrentItem(nextItem, true)
            handler.postDelayed(runnable, 2000)
        }
        handler.postDelayed(runnable, 2000)

        // 카카오 SDK 초기화
        KakaoSdk.init(this, getString(R.string.kakao_app_key))
        KakaoSdk.loggingEnabled = true

        // 뷰모델 옵저빙
        binding.progressBar.visibility = View.GONE
        observeSignInResponse()
        observeLoading()
        observeError()

        binding.btnKakaoLogin.setOnClickListener {
            //openKakaoLoginPage() // 테스트
            testSignIn()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable) // Activity 종료 시 Handler 리소스 해제
    }

    private fun testSignIn() {
        splashViewModel.setClientId("TEST")
        splashViewModel.setSocialType("TEST")
        splashViewModel.signIn()
    }

    private fun observeError() {
        splashViewModel.errorResponse.observe(this) { errorResponse ->
            errorResponse?.let {
                val errorDialog =
                    ServerErrorPopUp.newInstance(errorResponse.code, errorResponse.message)
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
                    // 로딩이 완료되었을 때, 멤버 여부를 확인하고 화면 전환
                    splashViewModel.isMember.observe(this) { isMember ->
                        if (isMember == true) {
                            goCozyHome()
                            finish()
                            binding.progressBar.visibility = View.GONE
                        } else {
                            //goOnboarding()
                            binding.progressBar.visibility = View.GONE
                        }
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
                    try {
                        splashViewModel.setTokenInfo(result.body()!!.result.tokenResponseDTO)
                        splashViewModel.saveToken()
                        splashViewModel.memberCheck()
                        splashViewModel.isMember.observe(this) { isMember ->
                            if (isMember == true) {
                                goCozyHome()
                            } else if (isMember == false) goUnivCert()
                            else if (isMember == null) Log.w(TAG, "회원 상태 확인 실패")
                        }
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
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }

    private fun goUnivCert() {
        val intent = Intent(this, UniversityCertificationActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun goLoginFail() {
        val intent = Intent(this, LoginFailActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun openKakaoLoginPage() {
        // 카카오톡 또는 카카오 계정으로 로그인 시도
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
                    getUserId()
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
                        splashViewModel.setSocialType("TEST") // "KAKAO"
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