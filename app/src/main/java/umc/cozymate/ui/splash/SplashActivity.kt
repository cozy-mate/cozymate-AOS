package umc.cozymate.ui.splash

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient
import umc.cozymate.R
import umc.cozymate.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private val TAG = this.javaClass.simpleName
    lateinit var binding: ActivitySplashBinding

    val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Log.e(TAG, "카카오계정으로 로그인 실패", error)
        } else if (token != null) {
            Log.i(TAG, "카카오계정으로 로그인 성공 accessToken: ${token.accessToken}")
            // GoOnboarding()
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
        with(binding) {
            btnKakaoLogin.setOnClickListener {
                // 카카오톡 설치여부 확인 -> 카카오톡 또는 카카오 계정으로 로그인
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
                            // GoOnboarding()
                        }
                    }
                } else {
                    // 카카오 계정으로 로그인
                    UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
                }
            }
        }
    }
}