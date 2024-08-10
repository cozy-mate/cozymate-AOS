package umc.cozymate.ui.splash

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.json.JsonPlugin
import io.ktor.client.plugins.kotlinx.serializer.KotlinxSerializer
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.Parameters
import io.ktor.http.contentType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class AuthCodeHandlerActivity : AppCompatActivity() {

    private val clientId = "865f38b72ba12c1cd8d44d3727770c09"
    private val redirectUri = "https://cozymate.store/oauth2/kakao/code"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleRedirectUri()
    }

    private fun handleRedirectUri() {
        val uri = intent.data
        val authorizeCode = uri?.getQueryParameter("code")
        if (authorizeCode != null) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val client = HttpClient {
                        install(JsonPlugin) {
                            serializer = KotlinxSerializer(Json { ignoreUnknownKeys = true })
                        }
                        install(HttpTimeout) {
                            requestTimeoutMillis = 10000
                        }
                    }
                    val response: HttpResponse = client.post("https://kauth.kakao.com/oauth/token") {
                        contentType(ContentType.Application.FormUrlEncoded)
                        setBody(
                            FormDataContent(Parameters.build {
                                append("grant_type", "authorization_code")
                                append("client_id", clientId)
                                append("redirect_uri", redirectUri)
                                append("code", authorizeCode)
                            })
                        )
                    }

                    val responseText = response.toString()
                    Log.d("KakaoSignIn", "Response: $responseText")
                    //val tokenResponse = Json.decodeFromString<TokenResponse>(responseText)

                    //if (tokenResponse.accessToken != null) {
                    //    Log.d("KakaoSignIn", "Access Token: ${tokenResponse.accessToken}")
                        // Use the access token here
                    //} else {
                    //    Log.e("KakaoSignIn", "Invalid token response")
                    //}

                } catch (e: Exception) {
                    Log.e("KakaoSignIn", "Request failed: ${e.message}")
                }
            }
        } else {
            Log.e("KakaoSignIn", "Missing authorization code")
        }
    }

    @Serializable
    data class TokenResponse(
        val accessToken: String? = null,
        val tokenType: String? = null,
        val refreshToken: String? = null,
        val expiresIn: Int? = null,
        val scope: String? = null
    )

/*

    private val TAG = this.javaClass.simpleName

    private val mCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Log.e(TAG, "로그인 실패 $error")
        } else if (token != null) {
            Log.e(TAG, "로그인 성공 ${token.accessToken}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("SplashActivity", "리다이렉트")

        handleRedirectUrl(intent?.data)

       */
/* val webView = WebView(this)
        setContentView(webView)

        webView.settings.javaScriptEnabled = true


        val redirectUrl = intent.getStringExtra("redirectUrl")
        // WebViewClient를 설정하여 페이지 로딩 완료를 감지
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)

                // 로그인 성공 후의 URL을 체크
                if (url != null && url.startsWith(redirectUrl!!)) {
                    // 로그인 성공 후 리다이렉트 URL에서 JSON 데이터를 가져옴
                    view?.evaluateJavascript(
                        "(function() { return document.body.innerText; })();"
                    ) { value ->
                        handleJsonData(value)
                    }
                }
            }
        }*//*


        // SplashActivity에서 전달된 리다이렉트 URL을 로드
*/
/*
        if (redirectUrl != null) {
            //webView.loadUrl(redirectUrl)
            // 카카오 로그인 페이지 로드
            val loginUrl = "https://kauth.kakao.com/oauth/authorize?client_id=YOUR_CLIENT_ID&redirect_uri=$redirectUrl&response_type=code"
            webView.loadUrl(loginUrl)
        } else {
            // URL이 전달되지 않았을 때 에러 처리
            finish()
        }*//*

        */
/*//*
/ 리다이렉트 URL에서 쿼리 파라미터 추출
        val uri: Uri? = intent?.data
        uri?.let {

            val jsonResponse = it.getQueryParameter("response")  // 예시로 "response" 쿼리 파라미터를 사용
            if (jsonResponse != null) {
                // JSON 응답을 파싱하고 처리
                val gson = Gson()
                val kakaoResponse: KakaoLoginResponse = gson.fromJson(jsonResponse, KakaoLoginResponse::class.java)

                // SplashActivity에 결과 전달
                val resultIntent = Intent()
                resultIntent.putExtra("kakao_response", kakaoResponse.toString())
                setResult(Activity.RESULT_OK, resultIntent)
            } else {
                // 실패 시 빈 인텐트로 RESULT_CANCELED 설정
                setResult(Activity.RESULT_CANCELED)
            }
        }*//*


        finish()  // 이 액티비티를 종료



        //handleKakaoRedirect(intent)

        */
/*//*
/ 카카오톡 설치 여부 확인
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
            // 카카오톡으로 로그인
            UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                if (error != null) {
                    Log.e(TAG, "로그인 실패 $error")
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    } else {
                        UserApiClient.instance.loginWithKakaoAccount(
                            this,
                            callback = mCallback
                        )
                    }
                } else if (token != null) {
                    Log.e(TAG, "로그인 성공 ${token.accessToken}")
                }
            }
        } else {
            // 카카오 계정으로 로그인
            UserApiClient.instance.loginWithKakaoAccount(
                this,
                callback = mCallback
            )
        }*//*

    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleRedirectUrl(intent?.data)
    }

    private fun handleRedirectUrl(uri: Uri?) {
        uri?.let {
            val code = it.getQueryParameter("code")
            if (code != null) {
                Log.d("AuthHandlerActivity", "Authorization Code: $code")
                // 인증 코드로 액세스 토큰을 얻는 작업 수행
            } else {
                Log.d("AuthHandlerActivity", "No code found in the URL")
            }
        }
    }

    private fun handleJsonData(jsonData: String) {
        // JSON 데이터가 큰따옴표로 감싸져 있으므로, 이를 제거
        val cleanedJsonData = jsonData.trim('"')

        // JSON 데이터를 파싱하여 KakaoLoginResponse로 변환
        val gson = Gson()
        Log.d("SplashActivity", "json: $cleanedJsonData")
        //val kakaoResponse = gson.fromJson(jsonData, KakaoLoginResponse::class.java)?.toString() ?: "null"

        // 결과를 SplashActivity에 전달
        val resultIntent = Intent()
        resultIntent.putExtra("kakao_response", "null..")
        setResult(Activity.RESULT_OK, resultIntent)

        finish()  // 이 액티비티를 종료
    }

    fun handleKakaoLoginResponse(jsonResponse: String) {
        val gson = Gson()

        // JSON 문자열을 KakaoLoginResponse 데이터 클래스로 변환
        val kakaoResponse: KakaoLoginResponse = gson.fromJson(jsonResponse, KakaoLoginResponse::class.java)

        // 성공 여부 확인
        if (kakaoResponse.isSuccess) {
            val refreshToken = kakaoResponse.result.refreshToken
            val memberInfo = kakaoResponse.result.memberInfoDTO

            // 받은 정보를 사용하여 원하는 작업을 수행
            println("Login Successful!")
            println("Refresh Token: $refreshToken")
            println("Member Info: Name = ${memberInfo.name}, Nickname = ${memberInfo.nickname}")
        } else {
            // 오류 처리
            println("Login Failed: ${kakaoResponse.message}")
        }
    }

    // 리다이렉트 코드 받기
    private fun handleKakaoRedirect(intent: Intent?) {
        intent?.data?.let { uri ->
            if (uri.scheme == "7feaa7120f9192caa40266c210bae2b9" && uri.host == "oauth") {
                val code = uri.getQueryParameter("code")
                if (code != null) {
                    //Log.d("AuthCodeHandlerActivity", "Authorization code: $jsonResponse")
                    getAccessToken(code)
                } else {
                    Log.e("AuthCodeHandlerActivity", "Authorization code is null")
                }
            }
        }
    }

    private fun getAccessToken(code: String) {
        lifecycleScope.launch {
            try {
                val response: Response<KakaoAccessTokenResponse> = RetrofitInstance.api.getKakaoAccessToken(code)
                if (response.isSuccessful) {
                    val accessToken = response.body()?.access_token
                    val refreshToken = response.body()?.refresh_token
                    Log.d("AuthCodeHandlerActivity", "Access Token: $accessToken")
                    Log.d("AuthCodeHandlerActivity", "Refresh Token: $refreshToken")

                    // Access token을 사용하여 추가 작업을 수행하거나 Activity로 반환
                    val mainIntent = Intent(this@AuthCodeHandlerActivity, SplashActivity::class.java)
                    mainIntent.putExtra("access_token", accessToken)
                    startActivity(mainIntent)
                    finish()
                } else {
                    Log.e("AuthCodeHandlerActivity", "Failed to get access token: ${response.code()}")
                }
            } catch (e: HttpException) {
                Log.e("AuthCodeHandlerActivity", "Exception: ${e.message()}")
            } catch (e: Exception) {
                Log.e("AuthCodeHandlerActivity", "Unexpected exception: ${e.message}")
            }
            fetchJwtToken()
        }
    }

    private fun fetchJwtToken() {
        lifecycleScope.launch {
            try {
                val code = "authorization_code_here" // 인가 코드 (실제 코드에서는 동적으로 받는 부분)
                val response: Response<UserInfoResponse> = RetrofitInstance.api.getUserInfo("Bearer $code")

                if (response.isSuccessful) {
                    val jwtToken = response.headers()["Authorization"]
                    Log.d("MainActivity", "JWT Token: $jwtToken")

                    // JWT 토큰을 저장하거나 다른 작업 수행
                    saveToken(jwtToken)
                } else {
                    Log.e("MainActivity", "Failed to get user info: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Exception: ${e.message}")
            }
        }
    }

    private fun saveToken(accessToken: String?) {
        // 예를 들어, SharedPreferences에 토큰 저장
        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("jwt_token", accessToken)
            apply()
        }
    }

 */


 */
 */
}
