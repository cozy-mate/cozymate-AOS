package umc.cozymate.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query
import umc.cozymate.data.model.entity.MemberDetail
import umc.cozymate.data.model.request.SendMailRequest
import umc.cozymate.data.model.request.SignInRequest
import umc.cozymate.data.model.request.UpdateInfoRequest
import umc.cozymate.data.model.request.VerifyMailRequest
import umc.cozymate.data.model.response.member.CheckNicknameResponse
import umc.cozymate.data.model.response.member.GetMailVerifyResponse
import umc.cozymate.data.model.response.member.GetMyUniversityResponse
import umc.cozymate.data.model.response.member.GetUniversityInfoResponse
import umc.cozymate.data.model.response.member.GetUniversityListResponse
import umc.cozymate.data.model.response.member.MemberInfoResponse
import umc.cozymate.data.model.response.member.ReissueResponse
import umc.cozymate.data.model.response.member.SignInResponse
import umc.cozymate.data.model.response.member.SignUpResponse
import umc.cozymate.data.model.response.member.UpdateInfoCommonResponse
import umc.cozymate.data.model.response.member.VerifyMailResponse
import umc.cozymate.data.model.response.member.WithdrawResponse

interface MemberService {

    // 토큰 재발행
    @GET("/auth/reissue")
    suspend fun reissue(
        @Header("Authorization") refreshToken: String,
    ): Response<ReissueResponse>

    // 회원 탈퇴
    @DELETE("/members/withdraw")
    suspend fun withdraw(
        @Header("Authorization") accessToken: String,
        @Query("withdrawReason") reason : String
    ): Response<WithdrawResponse>

    // 로그아웃
    @GET("/auth/logout")
    suspend fun signOut(
        @Header("Authorization") accessToken: String,
    ): Response<Unit>

    // 사용자 정보 조회
    @GET("/members/member-info")
    suspend fun getMemberInfo(
        @Header("Authorization") accessToken: String,
    ): Response<MemberInfoResponse>

    // 닉네임 유효성 검증
    @GET("/members/check-nickname")
    suspend fun checkNickname(
        @Header("Authorization") accessToken: String,
        @Query("nickname") nickname: String,
    ): Response<CheckNicknameResponse>

    // 회원가입
    @POST("/members/sign-up")
    suspend fun signUp(
        @Header("Authorization") accessToken: String,
        @Body memberDetail: MemberDetail
    ): Response<SignUpResponse>

    // 로그인
    @POST("/auth/sign-in")
    suspend fun signIn(
        @Body request: SignInRequest
    ): Response<SignInResponse>

    // 사용자 대학교 조회(인증 후)
    @GET("/university/get-member-univ-info")
    suspend fun myUniversity(
        @Header("Authorization") accessToken: String
    ): Response<GetMyUniversityResponse>

    // 대학교 전체 조회
    @GET("/university/get-list")
    suspend fun getUniversityList(
        @Header("Authorization") accessToken: String
    ): Response<GetUniversityListResponse>

    // 대학교 정보 조회
    @GET("/university/get-info")
    suspend fun getUniversityInfo(
        @Header("Authorization") accessToken: String,
        @Query("universityId") id: Int
    ): Response<GetUniversityInfoResponse>

    // 메일 인증 여부
    @GET("/members/mail/verify")
    suspend fun getMailVerify(
        @Header("Authorization") accessToken: String,
    ): Response<GetMailVerifyResponse>

    // 메일 보내기
    @POST("/members/mail")
    suspend fun sendMail(
        @Header("Authorization") accessToken: String,
        @Body request: SendMailRequest
    ): Response<Unit>

    // 메일 인증
    @POST("/members/mail/verify")
    suspend fun verifyMail(
        @Header("Authorization") accessToken: String,
        @Body request: VerifyMailRequest
    ): Response<VerifyMailResponse>

    // 사용자 정보 수정
    @PATCH("/members/update")
    suspend fun updateUserInfo(
        @Header("Authorization") accessToken: String,
        @Body request: UpdateInfoRequest
    ): Response<UpdateInfoCommonResponse>
}