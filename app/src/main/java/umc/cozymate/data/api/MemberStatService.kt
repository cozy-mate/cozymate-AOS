package umc.cozymate.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query
import umc.cozymate.data.model.request.MemberStatRequest
import umc.cozymate.data.model.response.member.stat.GetMemberSearchListResponse
import umc.cozymate.data.model.response.member.stat.GetRecommendedRoommateResponse
import umc.cozymate.data.model.response.member.stat.GetRoommateListByEqualityResponse

interface MemberStatService {

    // 사용자 검색
    @GET("/members/stat/search")
    suspend fun getMemberSearchList(
        @Header("Authorization") accessToken: String,
        @Query("keyword") keyword: String
    ) : Response<GetMemberSearchListResponse>

    // 사용자 랜덤 추천 (상세 정보가 없을 때)
    @GET("/members/stat/random")
    suspend fun getRandomRecommendedRoommateList(
        @Header("Authorization") accessToken: String
    ) : Response<GetRecommendedRoommateResponse>

    // 일치율 순 사용자 목록 받아 오기 (상세 정보가 있을 때)
    @GET("/members/stat/filter")
    suspend fun getRoommateListByEquality(
        @Header("Authorization") accessToken: String,
        @Query("page") page: Int
    ) : Response<GetRoommateListByEqualityResponse>

    // 필터링 사용자 목록 받아 오기 (상세 정보가 있을 때)
    @POST("/members/stat/filter/search")
    suspend fun postRecommendedRoommate(
        @Header("Authorization") accessToken: String,
        @Query("page") page: Int,
        @Body additionalPropList: MemberStatRequest
    )

}