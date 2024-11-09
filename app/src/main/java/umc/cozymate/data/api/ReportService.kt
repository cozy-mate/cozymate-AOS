package umc.cozymate.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import umc.cozymate.data.DefaultResponse
import umc.cozymate.data.model.request.ReportRequest

interface ReportService {
    @POST("/report")
    suspend fun postReport(
        @Header("Authorization") accessToken: String,
        @Body request : ReportRequest
    ): Response<DefaultResponse>
}