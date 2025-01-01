package umc.cozymate.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import umc.cozymate.data.model.entity.PreferenceList
import umc.cozymate.data.model.response.member.stat.GetMyPreferenceResponse
import umc.cozymate.data.model.response.member.stat.PostMyPreferenceResponse
import umc.cozymate.data.model.response.member.stat.UpdatePreferenceResponse

interface MemberStatPreferenceService {

    // 멤버 선호 항목 조회
    @GET("/members/stat/preference")
    suspend fun getMyPreference(
        @Header("Authorization") accessToken: String
    ) : Response<GetMyPreferenceResponse>

    // 멤버 선호 항목 생성
    @POST("/members/stat/preference")
    suspend fun postMyPreference(
        @Header("Authorization") accessToken: String,
        @Body preferenceList: PreferenceList
    ) : Response<PostMyPreferenceResponse>

    // 멤버 선호 항목 업데이트
    @PUT("/members/stat/preference")
    suspend fun updateMyPreference(
        @Header("Authorization") accessToken: String,
        @Body preferenceList: PreferenceList
    ) : Response<UpdatePreferenceResponse>

}