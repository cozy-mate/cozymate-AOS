package umc.cozymate.data.repository.repository

import umc.cozymate.data.DefaultResponse
import umc.cozymate.data.model.request.UserInfoRequest
import umc.cozymate.data.model.response.OtherUserInfoResponse
import umc.cozymate.util.NetworkResult

interface RoommateRepository {
    suspend fun sendUserInfo(accessToken: String, request: UserInfoRequest): NetworkResult<DefaultResponse>

    suspend fun getOtherUserInfo(page: Int, filterList: List<String>): NetworkResult<List<OtherUserInfoResponse>>
}