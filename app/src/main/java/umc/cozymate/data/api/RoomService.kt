package umc.cozymate.data.api

import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import umc.cozymate.data.model.request.CreateRoomRequest

interface RoomService {
    @POST("/rooms/create")
    suspend fun requestCreateRoom() : Response<CreateRoomRequest>

    @DELETE("rooms/{roomId}")
    suspend fun deleteRoom(
        @Path("roomId") roomId: Int,
        @Query("memberId") memberId: Int? = null
    ): Response<Unit>
}