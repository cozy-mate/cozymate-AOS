package umc.cozymate.data.repository.repository

import retrofit2.Response
import umc.cozymate.data.model.request.CreatePublicRoomRequest
import umc.cozymate.data.model.response.room.CreatePublicRoomResponse
import umc.cozymate.data.model.response.room.GetRoomInfoByInviteCodeResponse
import umc.cozymate.data.model.response.room.GetRoomInfoResponse
import umc.cozymate.data.model.response.room.IsRoomExistResponse
import umc.cozymate.data.model.response.room.JoinRoomResponse

interface RoomRepository {

    suspend fun getRoomInfo(accessToken: String, roomId: Int): Response<GetRoomInfoResponse>

    suspend fun getRoomInfoByInviteCode(accessToken: String, inviteCode: String): Response<GetRoomInfoByInviteCodeResponse>

    suspend fun joinRoom(accessToken: String, roomId: Int) : Response<JoinRoomResponse>

    suspend fun isRoomExist(accessToken: String) : Response<IsRoomExistResponse>

    //suspend fun createRoom(accessToken: String, roomInfo: CreateRoomRequest): Response<CreateRoomResponse>

    suspend fun createPublicRoom(accessToken: String, roomInfo: CreatePublicRoomRequest): Response<CreatePublicRoomResponse>

}

