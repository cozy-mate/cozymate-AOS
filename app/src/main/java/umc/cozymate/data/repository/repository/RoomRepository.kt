package umc.cozymate.data.repository.repository

import retrofit2.Response
import umc.cozymate.data.model.request.CreateRoomRequest
import umc.cozymate.data.model.response.room.CreateRoomResponse
import umc.cozymate.data.model.response.room.GetRoomInfoByInviteCodeResponse
import umc.cozymate.data.model.response.room.JoinRoomResponse

interface RoomRepository {

    suspend fun getRoomInfoByInviteCode(accessToken: String, inviteCode: String): Response<GetRoomInfoByInviteCodeResponse>

    suspend fun joinRoom(accessToken: String, roomId: Int) : Response<JoinRoomResponse>

    suspend fun createRoom(accessToken: String, roomInfo: CreateRoomRequest): Response<CreateRoomResponse<CreateRoomResponse.SuccessResult>>
}

