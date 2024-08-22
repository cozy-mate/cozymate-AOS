package umc.cozymate.data.repository.repository

import umc.cozymate.data.DefaultResponse
import umc.cozymate.data.model.request.FcmInfoRequest
import umc.cozymate.data.model.request.UserInfoRequest
import umc.cozymate.data.model.response.roommate.OtherUserInfoResponse
import umc.cozymate.util.NetworkResult

interface RoommateRepository {
    suspend fun sendUserInfo(accessToken: String, request: UserInfoRequest): NetworkResult<DefaultResponse>

    suspend fun getOtherUserInfo(accessToken: String, page: Int, filterList: List<String>): NetworkResult<OtherUserInfoResponse>

    suspend fun sendFcmInfo(accessToken: String, request: FcmInfoRequest) : NetworkResult<DefaultResponse>
}