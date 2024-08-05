package umc.cozymate.ui.splash

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.util.Utility
import kotlinx.coroutines.launch
import retrofit2.HttpException
import umc.cozymate.R
import umc.cozymate.databinding.ActivitySplashBinding
import umc.cozymate.ui.onboarding.OnboardingActivity

class SplashActivity : AppCompatActivity() {

    private val TAG = this.javaClass.simpleName
    lateinit var binding: ActivitySplashBinding

    val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Log.e(TAG, "카카오계정으로 로그인 실패", error)
        } else if (token != null) {
            Log.i(TAG, "카카오계정으로 로그인 성공 accessToken: ${token.accessToken}")
            GoOnboarding()
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

        Log.d(TAG, "keyhash : ${Utility.getKeyHash(this)}")

        initView()
    }

    fun initView() {
        val context: Context = this
        try {
            with(binding) {
                btnKakaoLogin.setOnClickListener {
                    performKakaoLogin()
                    /*// 카카오톡 설치여부 확인 -> 카카오톡 또는 카카오 계정으로 로그인
                    if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
                        UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                            if (error != null) {
                                Log.e(TAG, "카카오톡으로 로그인 실패", error)

                                // 사용자가 의도적으로 로그인을 취소한 경우(ex. 뒤로가기) 리턴
                                if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                                    return@loginWithKakaoTalk
                                }
                                UserApiClient.instance.loginWithKakaoAccount(
                                    context,
                                    callback = callback
                                )
                            } else if (token != null) {
                                Log.i(TAG, "카카오톡으로 로그인 성공 accessToken: ${token.accessToken}")
                                GoOnboarding()
                            }
                        }
                    } else {
                        // 카카오 계정으로 로그인
                        UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
                    }*/
                }
            }
        }catch (e: Exception){
            Log.e(TAG, "${e.message}")
        }
    }

    fun GoOnboarding() {
        val intent = Intent(this, OnboardingActivity::class.java)
        startActivity(intent)
    }

    private fun performKakaoLogin() {
        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.api.getKakaoRedirectUrl()
                if (response.isSuccessful) {
                    val redirectUrl = response.body()?.redirect_url
                    if (redirectUrl != null) {
                        Log.d("SplashActivity", "Redirect URL: $redirectUrl")

                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(redirectUrl))
                        startActivity(intent)
                    } else {
                        Log.e(TAG, "Redirect URL is null")
                    }
                } else {
                    Log.e("SplashActivity", "Failed to get redirect URL: ${response.code()}")
                }
            } catch (e: HttpException) {
                Log.e(TAG, "Exception: ${e.message()}")
            } catch (e: Exception) {
                Log.e("SplashActivity", "Unexpected Exception: ${e.message}")
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // 액세스 토큰을 받을 경우 처리
        intent?.getStringExtra("access_token")?.let { accessToken ->
            Log.d("MainActivity", "Received Access Token: $accessToken")
            // 여기에서 액세스 토큰을 이용해 추가 작업 수행
        }
    }
}