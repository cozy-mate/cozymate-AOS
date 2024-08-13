package umc.cozymate.ui.splash

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import umc.cozymate.R
import umc.cozymate.databinding.ActivitySplashBinding
import umc.cozymate.ui.onboarding.OnboardingActivity

class SplashActivity : AppCompatActivity() {

    private val TAG = this.javaClass.simpleName
    lateinit var binding: ActivitySplashBinding

    val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Log.e(TAG, "카카오계정으로 로그인 실패", error)
            Toast.makeText(this@SplashActivity, "카카오계정으로 로그인 실패", Toast.LENGTH_SHORT).show()
        } else if (token != null) {
            Log.i(TAG, "카카오계정으로 로그인 성공")
            Log.d(TAG, "accessToken: ${token.accessToken}")
            Log.d(TAG, "idToken: ${token.idToken}")
            Toast.makeText(this@SplashActivity, "카카오계정으로 로그인 성공", Toast.LENGTH_SHORT).show()
            goOnboarding()
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

        // 카카오 SDK 초기화
        KakaoSdk.init(this, getString(R.string.kakao_app_key))

        // 로그인 버튼 클릭 시 크롬 카카오 로그인 페이지 열기
        binding.btnKakaoLogin.setOnClickListener {
            openKakaoLoginPage()
            getuserid()
        }
    }

    private fun goOnboarding() {
        val intent = Intent(this, OnboardingActivity::class.java)
        startActivity(intent)
    }

    private fun openKakaoLoginPage() {
        try {
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this@SplashActivity)) {
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
                        Log.d(TAG, "accessToken: ${token.accessToken}")
                        Log.d(TAG, "idToken: ${token.idToken}")
                        Toast.makeText(this@SplashActivity, "카카오톡으로 로그인 성공", Toast.LENGTH_SHORT)
                            .show()
                        goOnboarding()
                    }


                }
            } else {
                // 카카오 계정으로 로그인
                UserApiClient.instance.loginWithKakaoAccount(
                    this@SplashActivity,
                    callback = callback
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "${e.message}")
            Toast.makeText(this@SplashActivity, "로그인 실패", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getuserid(){
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.e(TAG, "사용자 정보 요청 실패", error)
            } else if (user != null) {
                Log.i(TAG, "사용자 정보 요청 성공")
                val userId = user.id // 사용자 ID

                Log.d(TAG, "사용자 ID: $userId")
            }
        }
    }
}