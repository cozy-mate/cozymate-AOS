package umc.cozymate.data.repository.repositoryImpl

import umc.cozymate.data.DefaultResponse
import umc.cozymate.data.ResponseBody
import umc.cozymate.data.api.RoommateService
import umc.cozymate.data.model.request.UserInfoRequest
import umc.cozymate.data.model.response.OtherUserInfoResponse
import umc.cozymate.data.repository.repository.RoommateRepository
import umc.cozymate.util.NetworkResult
import umc.cozymate.util.handleApi
import javax.inject.Inject

class RoommateRepositoryImpl @Inject constructor(
    private val api: RoommateService
) : RoommateRepository {
    override suspend fun sendUserInfo(accessToken: String, request: UserInfoRequest): NetworkResult<DefaultResponse> {
        return handleApi({ api.sendUserInfo(accessToken, request) }) { response: ResponseBody<DefaultResponse> -> response.result }
    }

    override suspend fun getOtherUserInfo(page: Int, filterList: List<String>): NetworkResult<List<OtherUserInfoResponse>> {
        val filterQuery = filterList.joinToString(",")
        return handleApi({ api.getOtherUserInfo(page, filterQuery) }) { response: ResponseBody<List<OtherUserInfoResponse>> -> response.result}
    }
}