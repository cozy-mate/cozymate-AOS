package umc.cozymate.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import umc.cozymate.data.DefaultResponse
import umc.cozymate.data.model.request.ChatRequest
import umc.cozymate.data.model.response.chat.ChatContentsResponse
import umc.cozymate.data.model.response.chat.ChatRoomResponse
import umc.cozymate.data.model.response.chat.WriteChatResponse

interface ChatService {

    // 쪽지 방의 쪽지 상세 내역 조회
    @GET("/chats/chatrooms/{chatRoomId}")
    suspend fun getChatContents(
        @Header("Authorization") accessToken: String,
        @Path("chatRoomId") chatRoomId: Int,
        @Query("page") page : Int,
        @Query("size") size : Int
    ): Response<ChatContentsResponse>

    //쪽지 보내기
    @POST("/chats/members/{recipientId}")
    suspend fun postChat(
        @Header("Authorization") accessToken: String,
        @Path("recipientId") recipientId: Int,
        @Body request : ChatRequest
    ): Response<WriteChatResponse>

    // 쪽지방 삭제
    @DELETE("/chatrooms/{chatRoomId}")
    suspend fun deleteChatRooms (
        @Header("Authorization") accessToken: String,
        @Path("chatRoomId") chatRoomId: Int
    ): Response<DefaultResponse>

    // 쪽지방 목록 조회
    @GET("/chatrooms")
    suspend fun getChatRooms (
        @Header("Authorization") accessToken: String,
        @Query("page") page : Int,
        @Query("size") size : Int
    ): Response<ChatRoomResponse>

}