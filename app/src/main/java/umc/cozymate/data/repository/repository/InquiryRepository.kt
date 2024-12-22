package umc.cozymate.data.repository.repository

import retrofit2.Response
import umc.cozymate.data.DefaultResponse
import umc.cozymate.data.ResponseBody
import umc.cozymate.data.model.request.InquiryRequest
import umc.cozymate.data.model.response.InquiryResponse

interface InquiryRepository {
    suspend fun getInquiry( accessToken : String): Response<InquiryResponse>

    suspend fun getInquiryExistence(accessToken : String): Response<ResponseBody<Boolean>>

    suspend fun updateInquiryStatus(accessToken: String, inquiryId : Int): Response<DefaultResponse>

    suspend fun postInquiry( accessToken: String, request : InquiryRequest) : Response<DefaultResponse>
}