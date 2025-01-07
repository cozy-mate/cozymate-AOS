package umc.cozymate.data.repository.repository

import retrofit2.Response
import umc.cozymate.data.model.response.roomlog.NotificationLogResponse
import umc.cozymate.data.model.response.roomlog.RoomLogResponse

interface RoomLogRepository {

    suspend fun getRoomLog(accessToken: String, roomId: Int, page: Int? = 0, size: Int? = 10,): Response<RoomLogResponse>
    suspend fun getNotificationLog(accessToken: String): Response<NotificationLogResponse>
}