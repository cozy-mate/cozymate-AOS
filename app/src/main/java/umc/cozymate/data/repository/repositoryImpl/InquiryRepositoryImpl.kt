package umc.cozymate.data.repository.repositoryImpl

import retrofit2.Response
import umc.cozymate.data.DefaultResponse
import umc.cozymate.data.ResponseBody
import umc.cozymate.data.api.InquiryService
import umc.cozymate.data.model.request.InquiryRequest
import umc.cozymate.data.model.response.InquiryResponse
import umc.cozymate.data.repository.repository.InquiryRepository
import javax.inject.Inject

class InquiryRepositoryImpl @Inject constructor(
    private val api : InquiryService
): InquiryRepository {
    override suspend fun getInquiry(accessToken: String): Response<InquiryResponse> {
        return api.getInquiry(accessToken)
    }

    override suspend fun getInquiryExistence(accessToken: String): Response<ResponseBody<Boolean>> {
        return api.getInquiryExistence(accessToken)
    }

    override suspend fun updateInquiryStatus(accessToken: String, inquiryId : Int): Response<DefaultResponse> {
        return api.updateInquiryStatus(accessToken, inquiryId)
    }

    override suspend fun postInquiry(
        accessToken: String,
        request: InquiryRequest
    ): Response<DefaultResponse> {
        return api.postInquiry(accessToken, request)
    }
}