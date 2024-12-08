package umc.cozymate.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import umc.cozymate.data.DefaultResponse
import umc.cozymate.data.ResponseBody
import umc.cozymate.data.model.request.InquiryRequest
import umc.cozymate.data.model.response.InquiryResponse

interface InquiryService {
    @GET("/inquires")
    suspend fun getInquiry(
        @Header("Authorization") accessToken : String
    ): Response<InquiryResponse>

    @GET("/inquires/exist")
    suspend fun getInquiryExistence(
        @Header("Authorization") accessToken : String
    ): Response<ResponseBody<Boolean>>

    @PATCH("/inquiries/{inquiryId}")
    suspend fun updateInquiryStatus(
        @Header("Authorization") accessToken: String,
        @Path("inquiryId") inquiryId : Int
    ): Response<DefaultResponse>

    @POST("/inquiries")
    suspend fun postInquiry(
        @Header("Authorization") accessToken: String,
        @Body request : InquiryRequest
    ): Response<DefaultResponse>
}