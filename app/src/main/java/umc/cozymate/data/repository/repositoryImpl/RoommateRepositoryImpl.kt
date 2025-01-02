package umc.cozymate.data.repository.repositoryImpl

import retrofit2.Response
import umc.cozymate.data.DefaultResponse
import umc.cozymate.data.ResponseBody
import umc.cozymate.data.api.RoommateService
import umc.cozymate.data.model.request.FcmInfoRequest
import umc.cozymate.data.model.request.UserInfoRequest
import umc.cozymate.data.model.response.member.stat.FetchUserInfoResponse
import umc.cozymate.data.model.response.roommate.GetUserInfoResponse
import umc.cozymate.data.model.response.roommate.OtherUserInfoResponse
import umc.cozymate.data.model.response.roommate.SearchRoommateResponse
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

    override suspend fun getUserInfo(accessToken: String): Response<GetUserInfoResponse> {
        return api.getUserInfo(accessToken)
    }

    override suspend fun fetchUserInfo(
        accessToken: String,
        request: UserInfoRequest
    ): Response<FetchUserInfoResponse> {
        return api.fetchUserInfo(accessToken, request)
    }

    override suspend fun getOtherUserInfo(accessToken: String, page: Int, filterList: List<String>): NetworkResult<OtherUserInfoResponse> {
        val filterQuery = filterList.joinToString(",")
        return handleApi({ api.getOtherUserInfo(accessToken, page, filterQuery) }) { response: ResponseBody<OtherUserInfoResponse> -> response.result}
    }

    override suspend fun sendFcmInfo(
        accessToken: String,
        request: FcmInfoRequest
    ): NetworkResult<DefaultResponse> {
        return handleApi({ api.sendFcmInfo(accessToken, request) }) {response: ResponseBody<DefaultResponse> -> response.result}
    }

    override suspend fun searchRoommate(
        accessToken: String,
        keyword: String
    ): Response<SearchRoommateResponse> {
        return api.searchRoommate(accessToken, keyword)
    }
}