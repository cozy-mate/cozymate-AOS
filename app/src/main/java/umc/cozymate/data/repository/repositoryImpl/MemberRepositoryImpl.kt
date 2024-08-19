package umc.cozymate.data.repository.repositoryImpl

import retrofit2.Response
import umc.cozymate.data.api.MemberService
import umc.cozymate.data.model.entity.MemberInfo
import umc.cozymate.data.model.request.SignInRequest
import umc.cozymate.data.model.response.member.CheckNicknameResponse
import umc.cozymate.data.model.response.member.MemberInfoResponse
import umc.cozymate.data.model.response.member.ReissueResponse
import umc.cozymate.data.model.response.member.SignInResponse
import umc.cozymate.data.model.response.member.SignUpResponse
import umc.cozymate.data.repository.repository.MemberRepository
import javax.inject.Inject

class MemberRepositoryImpl @Inject constructor(
    private val api: MemberService
) : MemberRepository {

    override suspend fun reissue(refreshToken: String): Response<ReissueResponse> {
        return api.reissue(refreshToken)
    }

    override suspend fun signOut(accessToken: String) {
        return api.signOut(accessToken)
    }

    override suspend fun getMemberInfo(accessToken: String): Response<MemberInfoResponse> {
        return api.getMemberInfo(accessToken)
    }

    override suspend fun checkNickname(accessToken: String, nickname: String): Response<CheckNicknameResponse> {
        return api.checkNickname(accessToken, nickname)
    }

    override suspend fun signUp(token: String, memberInfo: MemberInfo): Response<SignUpResponse> {
        return api.signUp(token, memberInfo)
    }

    override suspend fun signIn(request: SignInRequest): Response<SignInResponse> {
        return api.signIn(request)
    }
}