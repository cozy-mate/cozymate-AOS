package umc.cozymate.data.repository.repositoryImpl

import retrofit2.Response
import umc.cozymate.data.api.MemberStatService
import umc.cozymate.data.model.response.member.stat.GetMemberDetailInfoResponse
import umc.cozymate.data.model.response.member.stat.GetMemberSearchListResponse
import umc.cozymate.data.model.response.member.stat.GetRecommendedRoommateResponse
import umc.cozymate.data.model.response.member.stat.GetRoommateListByEqualityResponse
import umc.cozymate.data.repository.repository.MemberStatRepository
import javax.inject.Inject

class MemberStatRepositoryImpl @Inject constructor(
    private val api: MemberStatService
) : MemberStatRepository {

    override suspend fun getMemberSearchList(
        accessToken: String,
        keyword: String
    ): Response<GetMemberSearchListResponse> {
        return api.getMemberSearchList(accessToken, keyword)
    }

    override suspend fun getRecommendedRoommateList(
        accessToken: String
    ): Response<GetRecommendedRoommateResponse> {
        return api.getRandomRecommendedRoommateList(accessToken)
    }

    override suspend fun getRoommateListByEquality(
        accessToken: String,
        page: Int
    ): Response<GetRoommateListByEqualityResponse> {
        return api.getRoommateListByEquality(accessToken, page)
    }

    override suspend fun getMemberDetailInfo(
        accessToken: String,
        memberId: Int
    ): Response<GetMemberDetailInfoResponse> {
        return api.getMemberDetailInfo(accessToken, memberId)
    }

}