package umc.cozymate.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query
import umc.cozymate.data.model.entity.MemberDetail
import umc.cozymate.data.model.request.SendMailRequest
import umc.cozymate.data.model.request.SignInRequest
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
    @GET("/members/sign-out")
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
    @POST("/members/sign-in")
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

    // 사용자 프로필 이미지 수정
    @POST("/members/update-persona")
    suspend fun updatePersona(
        @Header("Authorization") accessToken: String,
        @Query("persona") persona: Int
    ): Response<UpdateInfoCommonResponse>

    // 사용자 닉네임 수정
    @POST("/members/update-nickname")
    suspend fun updateNickname(
        @Header("Authorization") accessToken: String,
        @Query("nickname") nickname: String
    ): Response<UpdateInfoCommonResponse>

    // 사용자 프로필 이미지 수정
    @POST("/members/update-majorName")
    suspend fun updateMajorName(
        @Header("Authorization") accessToken: String,
        @Query("majorName") majorName: String
    ): Response<UpdateInfoCommonResponse>

    // 사용자 생일 수정
    // yyyy-MM-dd 형식
    @POST("/members/update-birthday")
    suspend fun updateBirthday(
        @Header("Authorization") accessToken: String,
        @Query("localDate") localDate: String
    ): Response<UpdateInfoCommonResponse>
}