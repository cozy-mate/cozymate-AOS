package umc.cozymate.data.repository.repositoryImpl

import retrofit2.Response
import umc.cozymate.data.api.RoomLogService
import umc.cozymate.data.model.response.roomlog.NotificationLogResponse
import umc.cozymate.data.model.response.roomlog.RoomLogResponse
import umc.cozymate.data.repository.repository.RoomLogRepository
import javax.inject.Inject

class RoomLogRepositoryImpl @Inject constructor(
    private val api: RoomLogService
) : RoomLogRepository {

    override suspend fun getRoomLog(
        accessToken: String,
        roomId: Int,
        page: Int?,
        size: Int?,
    ): Response<RoomLogResponse> {
        return api.getRoomLog(accessToken, roomId, page, size)
    }

    override suspend fun getNotificationLog(accessToken: String, page: Int?, size: Int?): Response<NotificationLogResponse> {
        return api.getNotificationLogs(accessToken, page, size)
    }
}