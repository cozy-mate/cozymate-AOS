package umc.cozymate.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query
import umc.cozymate.data.model.response.roomlog.NotificationLogResponse
import umc.cozymate.data.model.response.roomlog.RoomLogResponse

interface RoomLogService {

    // 방 룸로그 조회
    @GET("/roomlog/{roomId}")
    suspend fun getRoomLog(
        @Header("Authorization") accessToken: String,
        @Path("roomId") roomId: Int,
        @Query("page") memberId: Int? = 0,
        @Query("size") size: Int? = 10,
    ) : Response<RoomLogResponse>

    // 알림 조회
    @GET("/notificationLogs")
    suspend fun getNotificationLogs(
        @Header("Authorization") accessToken: String,
    ) : Response<NotificationLogResponse>
}