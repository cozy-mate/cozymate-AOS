package umc.cozymate.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import umc.cozymate.data.model.response.member.stat.GetMemberSearchListResponse
import umc.cozymate.data.model.response.member.stat.GetRecommendedRoommateResponse

interface MemberStatService {

    // 사용자 검색
    @GET("/members/stat/search")
    suspend fun getMemberSearchList(
        @Header("Authorization") accessToken: String,
        @Query("keyword") keyword: String
    ) : Response<GetMemberSearchListResponse>

    // 사용자 랜덤 추천
    @GET("/members/stat/random")
    suspend fun getRecommendedRoommate(
        @Header("Authorization") accessToken: String
    ) : Response<GetRecommendedRoommateResponse>
}