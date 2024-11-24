package umc.cozymate.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query
import umc.cozymate.data.model.entity.MemberDetail
import umc.cozymate.data.model.request.SignInRequest
import umc.cozymate.data.model.response.member.CheckNicknameResponse
import umc.cozymate.data.model.response.member.MemberInfoResponse
import umc.cozymate.data.model.response.member.ReissueResponse
import umc.cozymate.data.model.response.member.SignInResponse
import umc.cozymate.data.model.response.member.SignUpResponse
import umc.cozymate.data.model.response.member.WithdrawResponse

interface MemberService {

    // 토큰 재발행
    @GET("/auth/reissue")
    suspend fun reissue(
        @Header("Authorization") refreshToken: String,
    ) : Response<ReissueResponse>

    // 회원 탈퇴
    @DELETE("/members/withdraw")
    suspend fun withdraw(
        @Header("Authorization") accessToken: String,
    ) : Response<WithdrawResponse>

    // 로그아웃
    @GET("/members/sign-out")
    suspend fun signOut(
        @Header("Authorization") accessToken: String,
    ): Response<Unit>

    // 사용자 정보 조회
    @GET("/members/member-info")
    suspend fun getMemberInfo(
        @Header("Authorization") accessToken: String,
    ) : Response<MemberInfoResponse>

    // 닉네임 유효성 검증
    @GET("/members/check-nickname")
    suspend fun checkNickname(
        @Header("Authorization") accessToken: String,
        @Query("nickname") nickname: String,
        ) : Response<CheckNicknameResponse>

    // 회원가입
    @POST("/members/sign-up")
    suspend fun signUp(
        @Header("Authorization") accessToken: String,
        @Body memberDetail: MemberDetail
    ) : Response<SignUpResponse>

    // 로그인
    @POST("/members/sign-in")
    suspend fun signIn(
        @Body request: SignInRequest
    ) : Response<SignInResponse>
}