package umc.cozymate.data.repository.repositoryImpl

import retrofit2.Response
import umc.cozymate.data.api.MemberStatService
import umc.cozymate.data.model.response.member.stat.GetMemberSearchListResponse
import umc.cozymate.data.model.response.member.stat.GetRecommendedRoommateResponse
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

    override suspend fun getRecommendedRoommate(
        accessToken: String
    ): Response<GetRecommendedRoommateResponse> {
        return api.getRecommendedRoommate(accessToken)
    }


}