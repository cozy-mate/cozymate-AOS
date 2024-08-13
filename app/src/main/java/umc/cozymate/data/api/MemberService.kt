package umc.cozymate.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import umc.cozymate.data.DefaultResponse
import umc.cozymate.data.ResponseBody
import umc.cozymate.data.model.entity.MemberInfo
import umc.cozymate.data.model.response.SignUpResponse

interface MemberService {
    @GET("/api/v3/check-nickname")
    suspend fun requestNicknameCheck() : Response<ResponseBody<DefaultResponse>>

    @POST("api/v3/member/sign-up")
    suspend fun signUp(
        @Header("Authorization") accessToken: String,
        @Body memberInfo: MemberInfo
    ) : Response<SignUpResponse>
}