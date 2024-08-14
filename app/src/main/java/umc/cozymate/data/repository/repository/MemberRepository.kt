package umc.cozymate.data.repository.repository

import retrofit2.Response
import umc.cozymate.data.model.entity.MemberInfo
import umc.cozymate.data.model.request.SignInRequest
import umc.cozymate.data.model.response.SignInResponse
import umc.cozymate.data.model.response.SignUpResponse

interface MemberRepository {

    suspend fun signUp(token: String, memberInfo: MemberInfo): Response<SignUpResponse>

    suspend fun signIn(request: SignInRequest): Response<SignInResponse>

}