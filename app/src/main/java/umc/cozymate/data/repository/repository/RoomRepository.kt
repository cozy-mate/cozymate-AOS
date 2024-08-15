package umc.cozymate.data.repository.repository

import retrofit2.Response
import umc.cozymate.data.model.request.CreateRoomRequest
import umc.cozymate.data.model.response.room.CreateRoomResponse

interface RoomRepository {
    suspend fun createRoom(accessToken: String, roomInfo: CreateRoomRequest): Response<CreateRoomResponse<CreateRoomResponse.SuccessResult>>
}
