package umc.cozymate.data.repository.repository

import retrofit2.Response
import umc.cozymate.data.model.request.CreatePrivateRoomRequest
import umc.cozymate.data.model.request.CreatePublicRoomRequest
import umc.cozymate.data.model.response.room.CreatePrivateRoomResponse
import umc.cozymate.data.model.response.room.CreatePublicRoomResponse
import umc.cozymate.data.model.response.room.GetRecommendedRoomListResponse
import umc.cozymate.data.model.response.room.GetRoomInfoByInviteCodeResponse
import umc.cozymate.data.model.response.room.GetRoomInfoResponse
import umc.cozymate.data.model.response.room.IsRoomExistResponse
import umc.cozymate.data.model.response.room.JoinRoomResponse

interface RoomRepository {

    suspend fun getRoomInfo(accessToken: String, roomId: Int): Response<GetRoomInfoResponse>

    suspend fun getRoomInfoByInviteCode(accessToken: String, inviteCode: String): Response<GetRoomInfoByInviteCodeResponse>

    suspend fun joinRoom(accessToken: String, roomId: Int) : Response<JoinRoomResponse>

    suspend fun isRoomExist(accessToken: String) : Response<IsRoomExistResponse>

    suspend fun getRecommendedRoomList(accessToken: String, size: Int, page:Int, sortType: String?) : Response<GetRecommendedRoomListResponse>

    suspend fun createPrivateRoom(accessToken: String, roomInfo: CreatePrivateRoomRequest): Response<CreatePrivateRoomResponse>

    suspend fun createPublicRoom(accessToken: String, roomInfo: CreatePublicRoomRequest): Response<CreatePublicRoomResponse>

}

