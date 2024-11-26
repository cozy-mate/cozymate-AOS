package umc.cozymate.data.repository.repository

import retrofit2.Response
import umc.cozymate.data.model.response.member.stat.GetMemberSearchListResponse
import umc.cozymate.data.model.response.member.stat.GetRecommendedRoommateResponse

interface MemberStatRepository {

    suspend fun getMemberSearchList(accessToken: String, keyword: String): Response<GetMemberSearchListResponse>

    suspend fun getRecommendedRoommate(accessToken: String): Response<GetRecommendedRoommateResponse>

}