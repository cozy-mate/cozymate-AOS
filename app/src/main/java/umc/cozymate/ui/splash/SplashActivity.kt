package umc.cozymate.ui.splash

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import umc.cozymate.R
import umc.cozymate.databinding.ActivitySplashBinding
import umc.cozymate.ui.onboarding.OnboardingActivity

class SplashActivity : AppCompatActivity() {

    private val KAKAO_APP_KEY = "865f38b72ba12c1cd8d44d3727770c09" // 카카오 앱 키
    private val REDIRECT_URI = "https://cozymate.store/oauth2/kakao/code"

    private val TAG = this.javaClass.simpleName
    lateinit var binding: ActivitySplashBinding

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

        // 로그인 버튼 클릭 시 크롬 카카오 로그인 페이지 열기
        binding.btnKakaoLogin.setOnClickListener {
            //openKakaoLoginPage()
            goOnboarding()
        }


    }

    private fun goOnboarding() {
        val intent = Intent(this, OnboardingActivity::class.java)
        startActivity(intent)
    }

    private fun openKakaoLoginPage() {
        val loginUrl = "https://kauth.kakao.com/oauth/authorize?client_id=$KAKAO_APP_KEY&redirect_uri=$REDIRECT_URI&response_type=code"

        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(this, Uri.parse(loginUrl))
    }
}


/*
class SplashActivity : AppCompatActivity() {

    private val REQUEST_CODE_KAKAO_LOGIN = 1001

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

        intent?.data?.let {
            handleRedirectUrl(it)
        }

        initView()
    }

    fun initView() {
        val context: Context = this
        try {
            with(binding) {
                btnKakaoLogin.setOnClickListener {
                    performKakaoLogin()
                    */
/*//*
/ 카카오톡 설치여부 확인 -> 카카오톡 또는 카카오 계정으로 로그인
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
                    }*//*

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
                    val redirectUrl = response.body()?.result?.redirectUrl
                    if (redirectUrl != null) {
                        Log.d("SplashActivity", "Redirect URL: ${redirectUrl}")



                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(redirectUrl))
                        startActivity(intent)

                        intent.data = Uri.parse(redirectUrl)

                        GoOnboarding()
                        //startActivityForResult(intent, REQUEST_CODE_KAKAO_LOGIN)

                        // AuthCodeHandlerActivity를 호출하여 WebView에서 URL을 로드하게 함
                        //val intent = Intent(baseContext, AuthCodeHandlerActivity::class.java)
                        //intent.putExtra("redirectUrl", redirectUrl)
                        //startActivity(intent)

                        // Chrome Custom Tabs 인텐트 설정
                        //val builder = CustomTabsIntent.Builder()
                        //val customTabsIntent = builder.build()
                        //customTabsIntent.launchUrl(this@SplashActivity, Uri.parse(redirectUrl))

                        //handleRedirectUrl(it)

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
        */
/*intent?.getStringExtra("access_token")?.let { accessToken ->
            Log.d("MainActivity", "Received Access Token: $accessToken")
            // 여기에서 액세스 토큰을 이용해 추가 작업 수행
        }*//*

        Log.d(TAG, "음... 되긴하는걸까")
        if (intent != null && intent.data != null) {
            // 리다이렉트된 URL에서 데이터를 처리
            handleRedirectUrl(intent.data!!)
        }
    }

    private fun handleRedirectUrl(uri: Uri) {
        // 예를 들어, 'code' 쿼리 파라미터를 추출하여 처리
        Log.d(TAG, uri.toString())
        val code = uri.getQueryParameter("code")
        if (code != null) {
            Log.d(TAG, " code: ${code.toString()}")

            // 인증 코드가 성공적으로 얻어졌다면 서버로 인증 코드를 보내서 토큰을 받거나 추가적인 작업을 수행합니다.
            // 서버에서 인증 코드를 토큰으로 교환하는 작업을 여기에 구현할 수 있습니다.
            //getAccessTokenFromServer(code)
        }
        else {
            Log.d("SplashActivity", "No code found in the URL")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_KAKAO_LOGIN) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                val kakaoResponse: String? =
                    data.getStringExtra("kakao_response")

                // 카카오 로그인 성공 시 처리
                kakaoResponse?.let {
                    Log.d(TAG, "로그인 성공!!!! ${kakaoResponse}")
                }
            } else {
                // 로그인 실패 처리
                Log.d(TAG, "로그인 실패 ㅠㅠㅠ")

                //showErrorMessage("Login failed")
            }
        }
    }
}*/
*/
