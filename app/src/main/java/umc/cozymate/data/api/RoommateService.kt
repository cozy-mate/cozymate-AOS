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
<<<<<<< HEAD
import umc.cozymate.data.model.response.roommate.OtherMemberDetailInfoResponse
import umc.cozymate.data.model.response.roommate.OtherUsersInfoListResponse
import umc.cozymate.data.model.response.roommate.RandomMemberResponse
=======
import umc.cozymate.data.model.response.roommate.OtherUserInfoResponse
import umc.cozymate.data.model.response.roommate.SearchRoommateResponse
>>>>>>> parent of 9912f699 (Merge branch 'COZY-399-방-룸메-상세페이지-구현')

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
    ): Response<ResponseBody<OtherUserInfoResponse>>

    @POST("/fcm")
    suspend fun sendFcmInfo(
        @Header("Authorization") accessToken: String,
        @Body request: FcmInfoRequest
    ) : Response<ResponseBody<DefaultResponse>>

<<<<<<< HEAD
    @GET("/members/stat/random")
    suspend fun getRandomMember(
        @Header("Authorization") accessToken: String,
    ) : Response<ResponseBody<RandomMemberResponse>>

    @GET("/members/stat/{memberId}")
    suspend fun getOtherUserDetailInfo(
        @Header("Authorization") accessToken: String,
        @Path("memberId") memberId: Int
    ) : Response<ResponseBody<OtherMemberDetailInfoResponse>>
=======
    @GET("/members/stat/search")
    suspend fun searchRoommate(
        @Header("Authorization") accessToken: String,
        @Query("keyword") keyword: String,
    ) : Response<SearchRoommateResponse>
>>>>>>> parent of 9912f699 (Merge branch 'COZY-399-방-룸메-상세페이지-구현')
}
