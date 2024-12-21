package umc.cozymate.data.repository.repository

import umc.cozymate.data.DefaultResponse
import umc.cozymate.data.model.request.FcmInfoRequest
import umc.cozymate.data.model.request.UserInfoRequest
<<<<<<< HEAD
import umc.cozymate.data.model.response.roommate.OtherMemberDetailInfoResponse
import umc.cozymate.data.model.response.roommate.OtherUsersInfoListResponse
import umc.cozymate.data.model.response.roommate.RandomMemberResponse
=======
import umc.cozymate.data.model.response.roommate.OtherUserInfoResponse
import umc.cozymate.data.model.response.roommate.SearchRoommateResponse
>>>>>>> parent of 9912f699 (Merge branch 'COZY-399-방-룸메-상세페이지-구현')
import umc.cozymate.util.NetworkResult

interface RoommateRepository {
    suspend fun sendUserInfo(accessToken: String, request: UserInfoRequest): NetworkResult<DefaultResponse>

    suspend fun getOtherUserInfo(accessToken: String, page: Int, filterList: List<String>): NetworkResult<OtherUserInfoResponse>

    suspend fun sendFcmInfo(accessToken: String, request: FcmInfoRequest) : NetworkResult<DefaultResponse>

<<<<<<< HEAD
    suspend fun getRandomMember(accessToken: String) : NetworkResult<RandomMemberResponse>

    suspend fun getOtherUserDetailInfo(accessToken: String, memberId: Int) : NetworkResult<OtherMemberDetailInfoResponse>
=======
    suspend fun searchRoommate(accessToken: String, keyword: String) : Response<SearchRoommateResponse>
>>>>>>> parent of 9912f699 (Merge branch 'COZY-399-방-룸메-상세페이지-구현')
}