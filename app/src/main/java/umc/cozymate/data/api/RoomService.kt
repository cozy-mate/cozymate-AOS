package umc.cozymate.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import umc.cozymate.data.model.request.CreatePublicRoomRequest
import umc.cozymate.data.model.response.room.CreatePublicRoomResponse
import umc.cozymate.data.model.response.room.DeleteRoomResponse
import umc.cozymate.data.model.response.room.GetRoomInfoByInviteCodeResponse
import umc.cozymate.data.model.response.room.GetRoomInfoResponse
import umc.cozymate.data.model.response.room.IsRoomExistResponse
import umc.cozymate.data.model.response.room.JoinRoomResponse

interface RoomService {

    // 방 삭제
    @DELETE("rooms/{roomId}")
    suspend fun deleteRoom(
        @Header("Authorization") accessToken: String,
        @Path("roomId") roomId: Int,
        @Query("memberId") memberId: Int? = null
    ): Response<DeleteRoomResponse>

    // 방장이 생성한 방 정보 조회
    @GET("/rooms/{roomId}")
    suspend fun getRoomInfo(
        @Header("Authorization") accessToken: String,
        @Path("roomId") roomId: Int,
    ): Response<GetRoomInfoResponse>

    // 참여자가 초대코드로 방 정보 조회
    @GET("/rooms/join")
    suspend fun getRoomInfoByInviteCode(
        @Header("Authorization") accessToken: String,
        @Query("inviteCode") inviteCode: String
    ) : Response<GetRoomInfoByInviteCodeResponse>

    // 방 존재 여부 조회
    @GET("/rooms/exist")
    suspend fun isRoomExist(
        @Header("Authorization") accessToken: String
    ) : Response<IsRoomExistResponse>

    // 방 참여
    @POST("/rooms/{roomId}/join")
    suspend fun joinRoom(
        @Header("Authorization") accessToken: String,
        @Path("roomId") roomId: Int,
    ) : Response<JoinRoomResponse>

    /*// 방 생성
    @POST("/rooms/create")
    suspend fun createRoom(
        @Header("Authorization") accessToken: String,
        @Body roomInfo: CreateRoomRequest
    ) : Response<CreateRoomResponse>*/

    // 공개 방 생성
    @POST("/rooms/create-public")
    suspend fun createPublicRoom(
        @Header("Authorization") accessToken: String,
        @Body roomInfo: CreatePublicRoomRequest
    ) : Response<CreatePublicRoomResponse>
}