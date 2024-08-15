package umc.cozymate.data.repository.repository

import retrofit2.Response
import umc.cozymate.data.model.request.CreateRoomRequest
import umc.cozymate.data.model.response.room.CreateRoomResponse

interface RoomRepository {
    suspend fun createRoom(roomInfo: CreateRoomRequest): Response<CreateRoomResponse<CreateRoomResponse.SuccessResult>>
}
