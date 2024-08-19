package umc.cozymate.data.repository.repositoryImpl

import retrofit2.Response
import umc.cozymate.data.api.RoomService
import umc.cozymate.data.model.request.CreateRoomRequest
import umc.cozymate.data.model.response.room.CreateRoomResponse
import umc.cozymate.data.model.response.room.GetRoomInfoByInviteCodeResponse
import umc.cozymate.data.model.response.room.GetRoomInfoResponse
import umc.cozymate.data.model.response.room.IsRoomExistResponse
import umc.cozymate.data.model.response.room.JoinRoomResponse
import umc.cozymate.data.repository.repository.RoomRepository
import javax.inject.Inject

class RoomRepositoryImpl @Inject constructor(
    private val api: RoomService
) : RoomRepository {

    override suspend fun getRoomInfo(
        accessToken: String,
        roomId: Int
    ): Response<GetRoomInfoResponse> {
        return api.getRoomInfo(accessToken, roomId)
    }

    override suspend fun getRoomInfoByInviteCode(
        accessToken: String,
        inviteCode: String
    ): Response<GetRoomInfoByInviteCodeResponse> {
        return api.getRoomInfoByInviteCode(accessToken, inviteCode)
    }

    override suspend fun joinRoom(
        accessToken: String,
        roomId: Int
    ): Response<JoinRoomResponse> {
        return api.joinRoom(accessToken, roomId)
    }

    override suspend fun isRoomExist(
        accessToken: String
    ): Response<IsRoomExistResponse> {
        return api.isRoomExist(accessToken)
    }

    override suspend fun createRoom(
        accessToken: String,
        roomInfo: CreateRoomRequest
    ): Response<CreateRoomResponse<CreateRoomResponse.SuccessResult>> {
        return api.createRoom(accessToken, roomInfo)
    }

}