package umc.cozymate.data.repository.repository

import retrofit2.Response
import umc.cozymate.data.model.response.member.stat.GetMemberSearchListResponse
import umc.cozymate.data.model.response.member.stat.GetRecommendedRoommateResponse
import umc.cozymate.data.model.response.member.stat.GetRoommateListByEqualityResponse

interface MemberStatRepository {

    suspend fun getMemberSearchList(accessToken: String, keyword: String): Response<GetMemberSearchListResponse>

    suspend fun getRecommendedRoommateList(accessToken: String): Response<GetRecommendedRoommateResponse>

    suspend fun getRoommateListByEquality(accessToken: String, page: Int): Response<GetRoommateListByEqualityResponse>
}