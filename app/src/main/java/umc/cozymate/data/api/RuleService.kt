package umc.cozymate.data.api


import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import umc.cozymate.data.DefaultResponse
import umc.cozymate.data.model.request.RuleRequest
import umc.cozymate.data.model.response.ruleandrole.RuleResponse

interface RuleService {
    @POST("/rule/{roomId}")
    suspend fun createRule(
        @Header("Authorization") accessToken: String,
        @Path("roomId") roomId : Int,
        @Body request : RuleRequest
    ): Response<DefaultResponse>

    @GET("/rule/{roomId}")
    suspend fun getRule(
        @Header("Authorization") accessToken: String,
        @Path("roomId") roomId : Int
    ): Response<RuleResponse>

    @DELETE("/rule/{roomId}")
    suspend fun deleteRule(
        @Header("Authorization") accessToken: String,
        @Path("roomId") roomId : Int,
        @Query("ruleId") ruleId : Int
    ): Response<DefaultResponse>
}