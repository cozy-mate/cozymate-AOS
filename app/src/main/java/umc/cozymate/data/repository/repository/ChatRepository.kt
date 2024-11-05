package umc.cozymate.data.repository.repository

import retrofit2.Response
import umc.cozymate.data.DefaultResponse
import umc.cozymate.data.model.request.ChatRequest
import umc.cozymate.data.model.response.chat.ChatContentsResponse
import umc.cozymate.data.model.response.chat.ChatRoomResponse
import umc.cozymate.data.model.response.chat.WriteChatResponse

interface ChatRepository {

    suspend fun getChatContents(accessToken: String, chatRoomId: Int ): Response<ChatContentsResponse>
    suspend fun postChat(accessToken: String, recipientId: Int, request : ChatRequest): Response<WriteChatResponse>

    suspend fun deleteChatRooms (accessToken: String, chatRoomId: Int ): Response<DefaultResponse>
    suspend fun getChatRooms (accessToken: String): Response<ChatRoomResponse>


    //suspend fun deleteChat(accessToken: String, chatRoomId: Int, request: DeleteChatRoomsRequest): NetworkResult<DefaultResponse>
}