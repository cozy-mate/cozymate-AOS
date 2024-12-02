package umc.cozymate.data.repository.repository

import retrofit2.Response
import umc.cozymate.data.DefaultResponse
import umc.cozymate.data.model.request.RuleRequest
import umc.cozymate.data.model.response.ruleandrole.CreateResponse
import umc.cozymate.data.model.response.ruleandrole.RuleResponse

interface RuleRepository {
    suspend fun createRule( accessToken: String, roomId : Int, request : RuleRequest ): Response<CreateResponse>

    suspend fun getRule(accessToken: String, roomId : Int ): Response<RuleResponse>

    suspend fun deleteRule(accessToken: String, roomId : Int, ruleId : Int ): Response<DefaultResponse>
    suspend fun editRule(accessToken: String, roomId : Int, ruleId : Int, request: RuleRequest ): Response<DefaultResponse>
}