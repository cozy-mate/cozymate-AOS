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
                    val response: HttpResponse =
                        client.post("https://kauth.kakao.com/oauth/token") {
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

                } catch (e: Exception) {
                    Log.e("KakaoSignIn", "Request failed: ${e.message}")
                }
            }
        } else {
            Log.e("KakaoSignIn", "Missing authorization code")
        }
    }

}