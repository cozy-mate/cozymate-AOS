package umc.cozymate.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query
import umc.cozymate.data.DefaultResponse
import umc.cozymate.data.ResponseBody
import umc.cozymate.data.model.request.UserInfoRequest
import umc.cozymate.data.model.response.OtherUserInfoResponse

interface RoommateService {
    @POST("/members/stat/")
    suspend fun sendUserInfo(
        @Header("Authorization") accessToken: String,
        @Body request: UserInfoRequest
    ): Response<ResponseBody<DefaultResponse>>

    @GET("members/stat/search")
    suspend fun getOtherUserInfo(
        @Query("page") page: Int,
        @Query("filterList") filterList: String
    ): Response<ResponseBody<List<OtherUserInfoResponse>>>
}
