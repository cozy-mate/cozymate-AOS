package umc.cozymate.data.repository.repository

import retrofit2.Response
import umc.cozymate.data.DefaultResponse
import umc.cozymate.data.model.request.FcmInfoRequest
import umc.cozymate.data.model.request.UserInfoRequest
import umc.cozymate.data.model.response.roommate.OtherUserInfoResponse
import umc.cozymate.data.model.response.roommate.SearchRoommateResponse
import umc.cozymate.data.model.response.roommate.OtherMemberDetailInfoResponse
import umc.cozymate.data.model.response.roommate.OtherUsersInfoListResponse
import umc.cozymate.data.model.response.roommate.RandomMemberResponse
import umc.cozymate.util.NetworkResult

interface RoommateRepository {
    suspend fun sendUserInfo(accessToken: String, request: UserInfoRequest): NetworkResult<DefaultResponse>

    suspend fun getOtherUserInfo(accessToken: String, page: Int, filterList: List<String>): NetworkResult<OtherUsersInfoListResponse>

    suspend fun sendFcmInfo(accessToken: String, request: FcmInfoRequest) : NetworkResult<DefaultResponse>

    suspend fun searchRoommate(accessToken: String, keyword: String) : Response<SearchRoommateResponse>

    suspend fun getRandomMember(accessToken: String) : NetworkResult<RandomMemberResponse>

    suspend fun getOtherUserDetailInfo(accessToken: String, memberId: Int) : NetworkResult<OtherMemberDetailInfoResponse>
}