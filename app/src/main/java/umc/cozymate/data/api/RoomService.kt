package umc.cozymate.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import umc.cozymate.data.model.request.CreateRoomRequest
import umc.cozymate.data.model.response.CreateRoomResponse

interface RoomService {

    @POST("/rooms/create")
    suspend fun createRoom(
        @Body roomInfo: CreateRoomRequest
    ) : Response<CreateRoomResponse<CreateRoomResponse.SuccessResult>>

    @DELETE("rooms/{roomId}")
    suspend fun deleteRoom(
        @Path("roomId") roomId: Int,
        @Query("memberId") memberId: Int? = null
    ): Response<Unit>

    @GET("/rooms/{roomId}")
    suspend fun getRoomInfo(
        @Path("roomId") roomId: Int,
        @Query("memberId") memberId: Int? = null
    ): Response<Unit>

    @GET("/rooms/join")
    suspend fun getRoomInfoByInviteCode(
        @Query("inviteCode") inviteCode: String
    )
}