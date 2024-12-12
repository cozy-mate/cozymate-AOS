package umc.cozymate.data.api


import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import umc.cozymate.data.DefaultResponse
import umc.cozymate.data.model.request.RuleRequest
import umc.cozymate.data.model.response.ruleandrole.CreateResponse
import umc.cozymate.data.model.response.ruleandrole.RuleResponse

interface RuleService {
    @POST("/rooms/{roomId}/rules")
    suspend fun createRule(
        @Header("Authorization") accessToken: String,
        @Path("roomId") roomId : Int,
        @Body request : RuleRequest
    ): Response<CreateResponse>

    @GET("/rooms/{roomId}/rules")
    suspend fun getRule(
        @Header("Authorization") accessToken: String,
        @Path("roomId") roomId : Int
    ): Response<RuleResponse>

    @DELETE("/rooms/{roomId}/rules/{ruleId}")
    suspend fun deleteRule(
        @Header("Authorization") accessToken: String,
        @Path("roomId") roomId : Int,
        @Path("ruleId") ruleId : Int
    ): Response<DefaultResponse>

    @PUT("/rooms/{roomId}/rules/{ruleId}")
    suspend fun editRule(
        @Header("Authorization") accessToken: String,
        @Path("roomId") roomId : Int,
        @Path("ruleId") ruleId : Int,
        @Body request: RuleRequest
    ): Response<DefaultResponse>
}