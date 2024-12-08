package umc.cozymate.data.repository.repositoryImpl

import retrofit2.Response
import umc.cozymate.data.api.RoomService
import umc.cozymate.data.model.request.CreatePrivateRoomRequest
import umc.cozymate.data.model.request.CreatePublicRoomRequest
import umc.cozymate.data.model.response.room.CheckRoomNameResponse
import umc.cozymate.data.model.response.room.CreatePrivateRoomResponse
import umc.cozymate.data.model.response.room.CreatePublicRoomResponse
import umc.cozymate.data.model.response.room.GetRecommendedRoomListResponse
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

    override suspend fun getRecommendedRoomList(
        accessToken: String,
        size: Int,
        page: Int,
        sortType: String?
    ): Response<GetRecommendedRoomListResponse> {
        return api.getRecommendedRoomList(accessToken, size, page, sortType)
    }

    override suspend fun checkRoomName(
        accessToken: String,
        roomName: String
    ): Response<CheckRoomNameResponse> {
        return api.checkRoomName(accessToken, roomName)
    }

    override suspend fun createPrivateRoom(
        accessToken: String,
        roomInfo: CreatePrivateRoomRequest
    ): Response<CreatePrivateRoomResponse> {
        return api.createPrivateRoom(accessToken, roomInfo)
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

    override suspend fun createPublicRoom(
        accessToken: String,
        roomInfo: CreatePublicRoomRequest
    ): Response<CreatePublicRoomResponse> {
        return api.createPublicRoom(accessToken, roomInfo)
    }

}