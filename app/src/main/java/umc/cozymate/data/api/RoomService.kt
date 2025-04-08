package umc.cozymate.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import umc.cozymate.data.DefaultResponse
import umc.cozymate.data.model.request.CreatePrivateRoomRequest
import umc.cozymate.data.model.request.CreatePublicRoomRequest
import umc.cozymate.data.model.request.UpdateRoomInfoRequest
import umc.cozymate.data.model.response.room.CancelInvitationResponse
import umc.cozymate.data.model.response.room.CancelJoinRequestResponse
import umc.cozymate.data.model.response.room.ChangeRoomStatusResult
import umc.cozymate.data.model.response.room.CheckRoomNameResponse
import umc.cozymate.data.model.response.room.CreatePrivateRoomResponse
import umc.cozymate.data.model.response.room.CreatePublicRoomResponse
import umc.cozymate.data.model.response.room.DeleteRoomResponse
import umc.cozymate.data.model.response.room.GetInvitedMembersResponse
import umc.cozymate.data.model.response.room.GetInvitedRoomListResponse
import umc.cozymate.data.model.response.room.GetPendingMemberListResponse
import umc.cozymate.data.model.response.room.GetRecommendedRoomListResponse
import umc.cozymate.data.model.response.room.GetRequestedRoomListResponse
import umc.cozymate.data.model.response.room.GetRoomInfoByInviteCodeResponse
import umc.cozymate.data.model.response.room.GetRoomInfoResponse
import umc.cozymate.data.model.response.room.GetRoomMemberStatResponse
import umc.cozymate.data.model.response.room.GetRoomPendingMemberResponse
import umc.cozymate.data.model.response.room.IsRoomExistResponse
import umc.cozymate.data.model.response.room.JoinRoomResponse
import umc.cozymate.data.model.response.room.QuitRoomResponse
import umc.cozymate.data.model.response.room.SearchRoomResponse
import umc.cozymate.data.model.response.room.UpdateRoomInfoResponse

// (16/29) 구현
interface RoomService {

    // 방 삭제 (방장 권한)
    // deprecated
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

    // 방(공개방) 검색
    @GET("/rooms/search")
    suspend fun searchRoom(
        @Header("Authorization") accessToken: String,
        @Query("keyword") keyword: String,
    ): Response<SearchRoomResponse>

    // 사용자가 참여요청을 보낸 방 목록 조회
    @GET("/rooms/requested")
    suspend fun getRequestedRoomList(
        @Header("Authorization") accessToken: String,
        @Query("size") size: Int,
        @Query("page") page: Int
    ) : Response<GetRequestedRoomListResponse>

    // 방장이 받은 방 참여요청 목록 조회
    @GET("/rooms/pending-members")
    suspend fun getPendingMemberList(
        @Header("Authorization") accessToken: String,
    ): Response<GetPendingMemberListResponse>

    // 사용자가 참여 요청한 방인지 조회
    @GET("/rooms/{roomId}/pending-status")
    suspend fun getPendingRoom(
        @Header("Authorization") accessToken: String,
        @Path("roomId") roomId: Int
    ): Response<GetRoomPendingMemberResponse>

    // 사용자 -> 사용자가 초대받은 방인지 조회
    @GET("/rooms/{roomId}/invited-status")
    suspend fun getInvitedStatusRoom(
        @Header("Authorization") accessToken: String,
        @Path("roomId") roomId: Int
    ): Response<GetRoomPendingMemberResponse>

    // 참여자가 초대코드로 방 정보 조회
    @GET("/rooms/join")
    suspend fun getRoomInfoByInviteCode(
        @Header("Authorization") accessToken: String,
        @Query("inviteCode") inviteCode: String
    ) : Response<GetRoomInfoByInviteCodeResponse>

    // 방장 -> 방에 참여 요청한 사용자인지 조회
    @GET("/rooms/pending-status/{memberId}")
    suspend fun getPendingMember(
        @Header("Authorization") accessToken: String,
        @Path("memberId") memberId: Int
    ): Response<GetRoomPendingMemberResponse>

    // 방장 -> 방장이 초대한 사용자인지 조회
    @GET("/rooms/invited-status/{memberId}")
    suspend fun getInvitedStatus(
        @Header("Authorization") accessToken: String,
        @Path("memberId") memberId: Int
    ): Response<GetRoomPendingMemberResponse>

    // 사용자가 초대 요청을 받은 방 목록 조회
    @GET("/rooms/invited")
    suspend fun getInvitedRoomList(
        @Header("Authorization") accessToken: String
    ) : Response<GetInvitedRoomListResponse>

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

    // 방 정보 수정
    @PATCH("/rooms/{roomId}")
    suspend fun updateRoomInfo(
        @Header("Authorization") accessToken: String,
        @Path("roomId") roomId: Int,
        @Body roomInfo: UpdateRoomInfoRequest
    ) : Response<UpdateRoomInfoResponse>

    // 공개방으로 전환
    @PATCH("/rooms/{roomId}/to-public")
    suspend fun changeToPublicRoom(
        @Header("Authorization") accessToken: String,
        @Path("roomId") roomId: Int
    ) : Response<ChangeRoomStatusResult>

    // 비공개방으로 전환
    @PATCH("/rooms/{roomId}/to-private")
    suspend fun changeToPrivateRoom(
        @Header("Authorization") accessToken: String,
        @Path("roomId") roomId: Int
    ) : Response<ChangeRoomStatusResult>

    // 방 나가기
    @PATCH("/rooms/{roomId}/quit")
    suspend fun quitRoom(
        @Header("Authorization") accessToken: String,
        @Path("roomId") roomId: Int
    ) : Response<QuitRoomResponse>

    //방 참여 요청
    @POST("/rooms/{roomId}/request-join")
    suspend fun requestJoinRoom(
        @Header("Authorization") accessToken: String,
        @Path("roomId") roomId: Int
    ): Response<DefaultResponse>

    // 방 입장
    @POST("/rooms/{roomId}/join")
    suspend fun joinRoom(
        @Header("Authorization") accessToken: String,
        @Path("roomId") roomId: Int,
    ) : Response<JoinRoomResponse>

    // 방장 -> 내 방으로 초대하기
    @POST("/rooms/invite/{inviteeId}")
    suspend fun inviteMember(
        @Header("Authorization") accessToken: String,
        @Path("inviteeId") inviteeId: Int,
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

    // 방에 속해 있는 메이트 멤버 상세정보 조회
    @GET("/rooms/{roomId}/memberStat/{memberStatKey}")
    suspend fun getRoomMemberStat(
        @Header("Authorization") accessToken: String,
        @Path("roomId") roomId: Int,
        @Path("memberStatKey") memberStatKey: String
    ): Response<GetRoomMemberStatResponse>

    // 우리방으로 초대한 멤버 목록 조회
    @GET("/rooms/{roomId}/invited-members")
    suspend fun getInvitedMembers(
        @Header("Authorization") accessToken: String,
        @Path("roomId") roomId: Int
    ) : Response<GetInvitedMembersResponse>

    // 방장 -> 방 참여 요청 수락/거절
    @PATCH("/rooms/request-join/{requesterId}")
    suspend fun acceptMemberRequest(
        @Header("Authorization") accessToken: String,
        @Path("requesterId") requesterId: Int,
        @Query("accept") accept: Boolean
    ) : Response<DefaultResponse>

    // 사용자 -> 방 초대 요청/수락
    @POST("/rooms/{roomId}/invite-request")
    suspend fun acceptRoomEnter(
        @Header("Authorization") accessToken: String,
        @Path("roomId") roomId: Int,
        @Query("accept") accept: Boolean
    ) : Response<DefaultResponse>
}