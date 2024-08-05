package umc.cozymate.data.repository.repository

import umc.cozymate.data.DefaultResponse
import umc.cozymate.data.model.request.ChatMemberRequest
import umc.cozymate.data.model.request.DeleteChatRoomsRequest
import umc.cozymate.util.NetworkResult

interface ChatRepository {
    suspend fun sendChat(recipientId: Int, request: ChatMemberRequest): NetworkResult<DefaultResponse>

    suspend fun deleteChat(chatRoomId: Int, request: DeleteChatRoomsRequest): NetworkResult<DefaultResponse>
}