package umc.cozymate.data.repository.repositoryImpl

import io.ktor.client.request.request
import retrofit2.Response
import umc.cozymate.data.api.MemberService
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
import umc.cozymate.data.repository.repository.MemberRepository
import javax.inject.Inject

class MemberRepositoryImpl @Inject constructor(
    private val api: MemberService
) : MemberRepository {

    override suspend fun reissue(refreshToken: String): Response<ReissueResponse> {
        return api.reissue(refreshToken)
    }

    override suspend fun signOut(accessToken: String): Response<Unit> {
        return api.signOut(accessToken)
    }

    override suspend fun getMemberInfo(accessToken: String): Response<MemberInfoResponse> {
        return api.getMemberInfo(accessToken)
    }

    override suspend fun checkNickname(accessToken: String, nickname: String): Response<CheckNicknameResponse> {
        return api.checkNickname(accessToken, nickname)
    }

    override suspend fun signUp(token: String, memberDetail: MemberDetail): Response<SignUpResponse> {
        return api.signUp(token, memberDetail)
    }

    override suspend fun signIn(request: SignInRequest): Response<SignInResponse> {
        return api.signIn(request)
    }

    override suspend fun myUniversity(accessToken: String): Response<GetMyUniversityResponse> {
        return api.myUniversity(accessToken)
    }

    override suspend fun getUniversityList(accessToken: String): Response<GetUniversityListResponse> {
        return api.getUniversityList(accessToken)
    }

    override suspend fun sendMail(
        accessToken: String,
        request: SendMailRequest
    ): Response<SendMailResponse> {
        return api.sendMail(accessToken, request)
    }
}