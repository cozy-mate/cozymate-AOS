package umc.cozymate.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

import umc.cozymate.data.DefaultResponse
import umc.cozymate.data.ResponseBody
import umc.cozymate.data.model.response.chat.ChatContentsResponse
import umc.cozymate.data.model.response.chat.ChatRoomResponse

interface ChatService {

    @GET("/chats/chatrooms/{chatRoomId}")
    suspend fun getChatContents(
        @Header("Authorization") accessToken: String,
        @Path("chatRoomId") chatRoomId: Int
    ): Response<ChatContentsResponse>

    @POST("/chats/members")
    suspend fun postChat(
        @Header("Authorization") accessToken: String,
        @Path("recipientId") recipientId: Int,
        @Body content: String
    ): Response<ResponseBody<Int>>

    @DELETE("/chatrooms/{chatRoomId}")
    suspend fun deleteChatRooms (
        @Header("Authorization") accessToken: String,
        @Path("chatRoomId") chatRoomId: Int
    ): Response<DefaultResponse>

    @GET("/chatrooms")
    suspend fun getChatRooms (
        @Header("Authorization") accessToken: String
    ): Response<ChatRoomResponse>


}