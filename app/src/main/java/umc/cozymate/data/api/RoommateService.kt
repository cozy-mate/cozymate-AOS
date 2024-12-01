package umc.cozymate.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import umc.cozymate.data.DefaultResponse
import umc.cozymate.data.ResponseBody
import umc.cozymate.data.model.request.FcmInfoRequest
import umc.cozymate.data.model.request.UserInfoRequest
import umc.cozymate.data.model.response.roommate.OtherMemberDetailInfoResponse
import umc.cozymate.data.model.response.roommate.OtherUsersInfoListResponse
import umc.cozymate.data.model.response.roommate.RandomMemberResponse

interface RoommateService {
    @POST("/members/stat")
    suspend fun sendUserInfo(
        @Header("Authorization") accessToken: String,
        @Body request: UserInfoRequest
    ): Response<ResponseBody<DefaultResponse>>

    @GET("/members/stat/filter")
    suspend fun getOtherUserInfo(
        @Header("Authorization") accessToken: String,
        @Query("page") page: Int,
        @Query("filterList") filterList: String
    ): Response<ResponseBody<OtherUsersInfoListResponse>>

    @POST("/fcm")
    suspend fun sendFcmInfo(
        @Header("Authorization") accessToken: String,
        @Body request: FcmInfoRequest
    ): Response<ResponseBody<DefaultResponse>>

    @GET("/members/stat/random")
    suspend fun getRandomMember(
        @Header("Authorization") accessToken: String,
    ) : Response<ResponseBody<RandomMemberResponse>>

    @GET("/members/stat/{memberId}")
    suspend fun getOtherUserDetailInfo(
        @Header("Authorization") accessToken: String,
        @Path("memberId") memberId: Int
    ) : Response<ResponseBody<OtherMemberDetailInfoResponse>>
}
