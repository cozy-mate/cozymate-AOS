package umc.cozymate.data.repository.repository

import retrofit2.Response
import umc.cozymate.data.DefaultResponse
import umc.cozymate.data.model.request.ReportRequest

interface ReportRepository {
    suspend fun postReport( accessToken: String, request : ReportRequest ): Response<DefaultResponse>
}