package umc.cozymate.data.repository.repository

import retrofit2.Response
import umc.cozymate.data.model.entity.MemberDetail
import umc.cozymate.data.model.request.SendMailRequest
import umc.cozymate.data.model.request.SignInRequest
import umc.cozymate.data.model.response.member.CheckNicknameResponse
import umc.cozymate.data.model.response.member.GetMyUniversityResponse
import umc.cozymate.data.model.response.member.GetUniversityListResponse
import umc.cozymate.data.model.response.member.MemberInfoResponse
import umc.cozymate.data.model.response.member.ReissueResponse
import umc.cozymate.data.model.response.member.SendMailResponse
import umc.cozymate.data.model.response.member.SignInResponse
import umc.cozymate.data.model.response.member.SignUpResponse

interface MemberRepository {

    suspend fun reissue(refreshToken: String): Response<ReissueResponse>

    suspend fun signOut(accessToken: String): Response<Unit>

    suspend fun getMemberInfo(accessToken: String): Response<MemberInfoResponse>

    suspend fun checkNickname(accessToken: String, nickname: String): Response<CheckNicknameResponse>

    suspend fun signUp(token: String, memberDetail: MemberDetail): Response<SignUpResponse>

    suspend fun signIn(request: SignInRequest): Response<SignInResponse>

    suspend fun myUniversity(accessToken: String): Response<GetMyUniversityResponse>

    suspend fun getUniversityList(accessToken: String): Response<GetUniversityListResponse>

    suspend fun sendMail(accessToken: String, request: SendMailRequest): Response<SendMailResponse>

}