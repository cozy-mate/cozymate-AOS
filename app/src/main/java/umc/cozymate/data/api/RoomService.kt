package umc.cozymate.data.api

import retrofit2.Response
import retrofit2.http.POST
import umc.cozymate.data.model.request.CreateRoomRequest

interface RoomService {
    @POST("/rooms/create")
    suspend fun requestCreateRoom() : Response<CreateRoomRequest>
}