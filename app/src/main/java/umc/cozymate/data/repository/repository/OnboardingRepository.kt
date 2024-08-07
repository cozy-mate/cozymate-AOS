package umc.cozymate.data.repository.repository

import umc.cozymate.data.DefaultResponse
import umc.cozymate.data.model.entity.MemberInfo
import umc.cozymate.data.model.request.DeleteChatRoomsRequest
import umc.cozymate.data.model.response.JoinMemberResponse
import umc.cozymate.util.NetworkResult

interface OnboardingRepository {
    suspend fun joinMember(request: MemberInfo): NetworkResult<JoinMemberResponse>

    suspend fun nicknameCheck(chatRoomId: Int, request: DeleteChatRoomsRequest): NetworkResult<DefaultResponse>
}
