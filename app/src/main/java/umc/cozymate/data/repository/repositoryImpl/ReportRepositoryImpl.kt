package umc.cozymate.data.repository.repositoryImpl

import retrofit2.Response
import umc.cozymate.data.DefaultResponse
import umc.cozymate.data.api.ReportService
import umc.cozymate.data.model.request.ReportRequest
import umc.cozymate.data.repository.repository.ReportRepository
import javax.inject.Inject

class ReportRepositoryImpl @Inject constructor(
    private val api : ReportService
): ReportRepository {
    override suspend fun postReport(
        accessToken: String,
        request: ReportRequest
    ): Response<DefaultResponse> {
        return api.postReport(accessToken,request)
    }

}