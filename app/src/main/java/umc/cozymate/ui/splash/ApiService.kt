package umc.cozymate.ui.splash

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ApiService {
    //@GET("/api/auth/kakao/login")
    @GET("/oauth2/kakao/sign-in")
    suspend fun getKakaoRedirectUrl(): Response<KakaoResponse>

    @GET("/oauth2/kakao/code")
    suspend fun getKakaoAccessToken(@Query("code") code: String): Response<KakaoAccessTokenResponse>

    @GET("/oauth2/kakao/code")
    suspend fun getUserInfo(
        @Header("Authorization") authHeader: String
    ): Response<UserInfoResponse>
}

data class KakaoRedirectResponse(
    val redirect_url: String
)

data class KakaoAccessTokenResponse(
    val access_token: String,
    val refresh_token: String
)

data class UserInfoResponse(
    val id: Long,
    val properties: Properties
) {
    data class Properties(
        val nickname: String,
        val profile_image: String
    )
}