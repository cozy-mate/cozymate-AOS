package umc.cozymate.data.repository.repositoryImpl

import umc.cozymate.data.DefaultResponse
import umc.cozymate.data.ResponseBody
import umc.cozymate.data.api.ChatService
import umc.cozymate.data.model.request.ChatMemberRequest
import umc.cozymate.data.model.request.DeleteChatRoomsRequest
import umc.cozymate.data.repository.repository.ChatRepository
import umc.cozymate.util.NetworkResult
import umc.cozymate.util.handleApi
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val api: ChatService
): ChatRepository {
    override suspend fun sendChat(recipientId: Int, request: ChatMemberRequest): NetworkResult<DefaultResponse> {
        return handleApi({api.postChat(recipientId, request) }) {response: ResponseBody<DefaultResponse> -> response.result}
    }

    override suspend fun deleteChat(chatRoomId: Int, request: DeleteChatRoomsRequest): NetworkResult<DefaultResponse> {
        return handleApi({api.deleteChat(chatRoomId, request) }) {response: ResponseBody<DefaultResponse> -> response.result}
    }
}