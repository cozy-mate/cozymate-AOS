package umc.cozymate.data.repository.repositoryImpl

import retrofit2.Response
import umc.cozymate.data.api.RoomService
import umc.cozymate.data.model.request.CreatePrivateRoomRequest
import umc.cozymate.data.model.request.CreatePublicRoomRequest
import umc.cozymate.data.model.request.UpdateRoomInfoRequest
import umc.cozymate.data.model.response.room.CancelJoinRequestResponse
import umc.cozymate.data.model.response.room.ChangeRoomStatusResult
import umc.cozymate.data.model.response.room.CheckRoomNameResponse
import umc.cozymate.data.model.response.room.CreatePrivateRoomResponse
import umc.cozymate.data.model.response.room.CreatePublicRoomResponse
import umc.cozymate.data.model.response.room.DeleteRoomResponse
import umc.cozymate.data.model.response.room.GetInvitedRoomListResponse
import umc.cozymate.data.model.response.room.GetPendingMemberListResponse
import umc.cozymate.data.model.response.room.GetRecommendedRoomListResponse
import umc.cozymate.data.model.response.room.GetRequestedRoomListResponse
import umc.cozymate.data.model.response.room.GetRoomInfoByInviteCodeResponse
import umc.cozymate.data.model.response.room.GetRoomInfoResponse
import umc.cozymate.data.model.response.room.GetRoomMemberStatResponse
import umc.cozymate.data.model.response.room.GetRoomPendingMemberResponse
import umc.cozymate.data.model.response.room.IsRoomExistResponse
import umc.cozymate.data.model.response.room.JoinRoomResponse
import umc.cozymate.data.model.response.room.QuitRoomResponse
import umc.cozymate.data.model.response.room.SearchRoomResponse
import umc.cozymate.data.model.response.room.UpdateRoomInfoResponse
import umc.cozymate.data.repository.repository.RoomRepository
import javax.inject.Inject

class RoomRepositoryImpl @Inject constructor(
    private val api: RoomService
) : RoomRepository {
    override suspend fun deleteRoom(
        accessToken: String,
        roomID: Int
    ): Response<DeleteRoomResponse> {
        return api.deleteRoom(accessToken, roomID)
    }

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

    override suspend fun updateRoomInfo(
        accessToken: String,
        roomId: Int,
        roomInfoRequest: UpdateRoomInfoRequest
    ): Response<UpdateRoomInfoResponse> {
        return api.updateRoomInfo(accessToken, roomId, roomInfoRequest)
    }

    override suspend fun changeToPublicRoom(
        accessToken: String,
        roomId: Int
    ): Response<ChangeRoomStatusResult> {
        return api.changeToPublicRoom(accessToken, roomId)
    }

    override suspend fun changeToPrivateRoom(
        accessToken: String,
        roomId: Int
    ): Response<ChangeRoomStatusResult> {
        return api.changeToPrivateRoom(accessToken, roomId)
    }

    override suspend fun quitRoom(accessToken: String, roomId: Int): Response<QuitRoomResponse> {
        return api.quitRoom(accessToken, roomId)
    }

    override suspend fun searchRoom(
        accessToken: String,
        keyword: String
    ): Response<SearchRoomResponse> {
        return api.searchRoom(accessToken, keyword)
    }

    override suspend fun getRequestedRoomList(accessToken: String): Response<GetRequestedRoomListResponse> {
        return api.getRequestedRoomList(accessToken)
    }

    override suspend fun getPendingMemberLiat(accessToken: String): Response<GetPendingMemberListResponse> {
        return api.getPendingMemberList(accessToken)
    }

    override suspend fun getRoomMemberStat(
        accessToken: String,
        roomId: Int,
        memberStatKey: String
    ): Response<GetRoomMemberStatResponse> {
        return api.getRoomMemberStat(accessToken, roomId, memberStatKey)
    }

    override suspend fun getPendingRoom(
        accessToken: String,
        roomId: Int
    ): Response<GetRoomPendingMemberResponse> {
        return api.getPendingRoom(accessToken, roomId)
    }

    override suspend fun cancelJoinRequest(
        accessToken: String,
        roomId: Int
    ): Response<CancelJoinRequestResponse> {
        return api.cancelJoinRequest(accessToken, roomId)
    }

    override suspend fun getInvitedRoomList(accessToken: String): Response<GetInvitedRoomListResponse> {
        return api.getInvitedRoomList(accessToken)
    }
}