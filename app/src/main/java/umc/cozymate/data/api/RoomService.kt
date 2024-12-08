package umc.cozymate.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import umc.cozymate.data.model.request.CreatePrivateRoomRequest
import umc.cozymate.data.model.request.CreatePublicRoomRequest
import umc.cozymate.data.model.response.room.CancelInvitationResponse
import umc.cozymate.data.model.response.room.CancelJoinRequestResponse
import umc.cozymate.data.model.response.room.CheckRoomNameResponse
import umc.cozymate.data.model.response.room.CreatePrivateRoomResponse
import umc.cozymate.data.model.response.room.CreatePublicRoomResponse
import umc.cozymate.data.model.response.room.DeleteRoomResponse
import umc.cozymate.data.model.response.room.GetRecommendedRoomListResponse
import umc.cozymate.data.model.response.room.GetRoomInfoByInviteCodeResponse
import umc.cozymate.data.model.response.room.GetRoomInfoResponse
import umc.cozymate.data.model.response.room.IsRoomExistResponse
import umc.cozymate.data.model.response.room.JoinRoomResponse

// (10/29) 구현
interface RoomService {

    // 방 삭제 (방장 권한)
    @DELETE("rooms/{roomId}")
    suspend fun deleteRoom(
        @Header("Authorization") accessToken: String,
        @Path("roomId") roomId: Int,
    ): Response<DeleteRoomResponse>

    // 사용자 -> 방 참여 요청 취소
    @DELETE("/rooms/{roomId}/request-join")
    suspend fun cancelJoinRequest(
        @Header("Authorization") accessToken: String,
        @Path("roomId") roomId: Int
    ): Response<CancelJoinRequestResponse>

    // 방장 -> 내방으로 초대 취소
    @DELETE("/rooms/invitee/{inviteeId}")
    suspend fun cancelInvitation(
        @Header("Authorization") accessToken: String,
        @Path("inviteeId") inviteeId: Int
    ): Response<CancelInvitationResponse>

    // 방 정보 조회
    // 공개방은 모두 조회 가능, 비공개방은 사용자가 속한 방만 조회 가능
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

    // 방 추천 리스트 조회
    @GET("/rooms/list")
    suspend fun getRecommendedRoomList(
        @Header("Authorization") accessToken: String,
        @Query("size") size: Int,
        @Query("page") page: Int,
        @Query("sortType") sortType: String?
    ) : Response<GetRecommendedRoomListResponse>

    // 방 이름 중복 검증
    @GET("/rooms/check-roomname")
    suspend fun checkRoomName(
        @Header("Authorization") accessToken: String,
        @Query("roomName") roomName: String
    ) : Response<CheckRoomNameResponse>

    // 방 참여
    @POST("/rooms/{roomId}/join")
    suspend fun joinRoom(
        @Header("Authorization") accessToken: String,
        @Path("roomId") roomId: Int,
    ) : Response<JoinRoomResponse>

    // 공개 방 생성
    @POST("/rooms/create-public")
    suspend fun createPublicRoom(
        @Header("Authorization") accessToken: String,
        @Body roomInfo: CreatePublicRoomRequest
    ) : Response<CreatePublicRoomResponse>

    // 초대코드 방 생성
    @POST("/rooms/create-private")
    suspend fun createPrivateRoom(
        @Header("Authorization") accessToken: String,
        @Body roomInfo: CreatePrivateRoomRequest
    ) : Response<CreatePrivateRoomResponse>
}