package umc.cozymate.data.repository.repositoryImpl

import umc.cozymate.data.DefaultResponse
import umc.cozymate.data.ResponseBody
import umc.cozymate.data.api.MemberService
import umc.cozymate.data.model.entity.MemberInfo
import umc.cozymate.data.model.request.DeleteChatRoomsRequest
import umc.cozymate.data.model.response.JoinMemberResponse
import umc.cozymate.data.repository.repository.OnboardingRepository
import umc.cozymate.util.NetworkResult
import umc.cozymate.util.handleApi
import javax.inject.Inject

class OnboardingRepositoryImpl @Inject constructor(
    private val api: MemberService
) : OnboardingRepository {

    override suspend fun joinMember(
        request: MemberInfo
    ): NetworkResult<JoinMemberResponse> {
        return handleApi({api.requestJoinMember(request) }) {response: ResponseBody<JoinMemberResponse> -> response.result}

        /*return try {
            val response = api.requestJoinMember(memberInfo = request)
            if (response.isSuccessful) {
                NetworkResult.Success(response.body()!!)
            } else {
                NetworkResult.Fail(response.code(), response.message())
            }
        } catch (e: Exception){
            NetworkResult.Error(e)
        }*/
    }

    override suspend fun nicknameCheck(
        chatRoomId: Int,
        request: DeleteChatRoomsRequest
    ): NetworkResult<DefaultResponse> {
        TODO("Not yet implemented")
    }
}