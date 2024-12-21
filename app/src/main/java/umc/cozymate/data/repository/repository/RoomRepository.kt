package umc.cozymate.data.repository.repository

import retrofit2.Response
import umc.cozymate.data.model.request.CreatePrivateRoomRequest
import umc.cozymate.data.model.request.CreatePublicRoomRequest
import umc.cozymate.data.model.request.UpdateRoomInfoRequest
import umc.cozymate.data.model.response.room.ChangeRoomStatusResult
import umc.cozymate.data.model.response.room.CheckRoomNameResponse
import umc.cozymate.data.model.response.room.CreatePrivateRoomResponse
import umc.cozymate.data.model.response.room.CreatePublicRoomResponse
import umc.cozymate.data.model.response.room.DeleteRoomResponse
import umc.cozymate.data.model.response.room.GetRecommendedRoomListResponse
import umc.cozymate.data.model.response.room.GetRoomInfoByInviteCodeResponse
import umc.cozymate.data.model.response.room.GetRoomInfoResponse
import umc.cozymate.data.model.response.room.IsRoomExistResponse
import umc.cozymate.data.model.response.room.JoinRoomResponse
import umc.cozymate.data.model.response.room.QuitRoomResponse
import umc.cozymate.data.model.response.room.UpdateRoomInfoResponse

interface RoomRepository {

    suspend fun deleteRoom(accessToken: String, roomID: Int): Response<DeleteRoomResponse>

    suspend fun getRoomInfo(accessToken: String, roomId: Int): Response<GetRoomInfoResponse>

    suspend fun getRoomInfoByInviteCode(accessToken: String, inviteCode: String): Response<GetRoomInfoByInviteCodeResponse>

    suspend fun joinRoom(accessToken: String, roomId: Int) : Response<JoinRoomResponse>

    suspend fun isRoomExist(accessToken: String) : Response<IsRoomExistResponse>

    suspend fun getRecommendedRoomList(accessToken: String, size: Int, page:Int, sortType: String?) : Response<GetRecommendedRoomListResponse>

    suspend fun checkRoomName(accessToken: String, roomName: String) : Response<CheckRoomNameResponse>

    suspend fun createPrivateRoom(accessToken: String, roomInfo: CreatePrivateRoomRequest): Response<CreatePrivateRoomResponse>

    suspend fun createPublicRoom(accessToken: String, roomInfo: CreatePublicRoomRequest): Response<CreatePublicRoomResponse>

    suspend fun updateRoomInfo(accessToken: String, roomId: Int, roomInfoRequest: UpdateRoomInfoRequest): Response<UpdateRoomInfoResponse>

    suspend fun changeToPublicRoom(accessToken: String, roomId: Int): Response<ChangeRoomStatusResult>

    suspend fun changeToPrivateRoom(accessToken: String, roomId: Int): Response<ChangeRoomStatusResult>

    suspend fun quitRoom(accessToken: String, roomId: Int): Response<QuitRoomResponse>
<<<<<<< HEAD
}
=======

    suspend fun searchRoom(accessToken: String, keyword: String): Response<SearchRoomResponse>
}

>>>>>>> parent of 9912f699 (Merge branch 'COZY-399-방-룸메-상세페이지-구현')
