package umc.cozymate.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path
import umc.cozymate.data.DefaultResponse
import umc.cozymate.data.ResponseBody
import umc.cozymate.data.model.request.ChatMemberRequest
import umc.cozymate.data.model.request.DeleteChatRoomsRequest

interface ChatService {
    @POST("chats/members/{recipientId}")
    suspend fun postChat(
        @Path("recipientId") recipientId: Int,
        @Body request: ChatMemberRequest
    ): Response<ResponseBody<DefaultResponse>>

    @DELETE("chatrooms/{chatRoomId}")
    suspend fun deleteChat(
        @Path("chatRoomId") chatRoomId: Int,
        @Body request: DeleteChatRoomsRequest
    ): Response<ResponseBody<DefaultResponse>>
}