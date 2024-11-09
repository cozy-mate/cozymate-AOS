package umc.cozymate.data.repository.repositoryImpl

import retrofit2.Response
import umc.cozymate.data.DefaultResponse
import umc.cozymate.data.api.ChatService
import umc.cozymate.data.model.request.ChatRequest
import umc.cozymate.data.model.response.chat.ChatContentsResponse
import umc.cozymate.data.model.response.chat.ChatRoomResponse
import umc.cozymate.data.model.response.chat.WriteChatResponse
import umc.cozymate.data.repository.repository.ChatRepository
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val api : ChatService
) : ChatRepository {
    override suspend fun getChatContents(
        accessToken: String,
        chatRoomId: Int
    ): Response<ChatContentsResponse> {
       return api.getChatContents(accessToken, chatRoomId)
    }

    override suspend fun postChat(
        accessToken: String,
        recipientId: Int,
        request : ChatRequest
    ): Response<WriteChatResponse> {
        return api.postChat(accessToken, recipientId, request)
    }

    override suspend fun deleteChatRooms(
        accessToken: String,
        chatRoomId: Int
    ): Response<DefaultResponse> {
        return api.deleteChatRooms(accessToken, chatRoomId)
    }

    override suspend fun getChatRooms(
        accessToken: String
    ): Response<ChatRoomResponse> {
        return api.getChatRooms(accessToken)
    }

}