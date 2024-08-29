package umc.cozymate.data.repository.repository

import retrofit2.Response
import umc.cozymate.data.model.entity.MemberInfo
import umc.cozymate.data.model.request.SignInRequest
import umc.cozymate.data.model.response.member.CheckNicknameResponse
import umc.cozymate.data.model.response.member.MemberInfoResponse
import umc.cozymate.data.model.response.member.ReissueResponse
import umc.cozymate.data.model.response.member.SignInResponse
import umc.cozymate.data.model.response.member.SignUpResponse

interface MemberRepository {

    suspend fun reissue(refreshToken: String): Response<ReissueResponse>

    suspend fun signOut(accessToken: String): Response<Unit>

    suspend fun getMemberInfo(accessToken: String): Response<MemberInfoResponse>

    suspend fun checkNickname(accessToken: String, nickname: String): Response<CheckNicknameResponse>

    suspend fun signUp(token: String, memberInfo: MemberInfo): Response<SignUpResponse>

    suspend fun signIn(request: SignInRequest): Response<SignInResponse>

}