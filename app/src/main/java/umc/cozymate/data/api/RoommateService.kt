package umc.cozymate.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query
import umc.cozymate.data.DefaultResponse
import umc.cozymate.data.ResponseBody
import umc.cozymate.data.model.request.FcmInfoRequest
import umc.cozymate.data.model.request.UserInfoRequest
import umc.cozymate.data.model.response.roommate.OtherUserInfoResponse

interface RoommateService {
    @POST("/members/stat")
    suspend fun sendUserInfo(
        @Header("Authorization") accessToken: String,
        @Body request: UserInfoRequest
    ): Response<ResponseBody<DefaultResponse>>

    //    @GET("/members/stat/search/details")
//    suspend fun getOtherUserInfo(
//        @Header("Authorization") accessToken: String,
//        @Query("page") page: Int,
//        @Query("filterList") filterList: String
//    ): Response<OtherUserResponseBody<List<OtherUserInfoResponse>>>
    @GET("/members/stat/search/details")
    suspend fun getOtherUserInfo(
        @Header("Authorization") accessToken: String,
        @Query("page") page: Int,
        @Query("filterList") filterList: String
    ): Response<ResponseBody<OtherUserInfoResponse>>

    @POST("/fcm")
    suspend fun sendFcmInfo(
        @Header("Authorization") accessToken: String,
        @Body request: FcmInfoRequest
    ) : Response<ResponseBody<DefaultResponse>>
}
