package umc.cozymate.data.repository.repositoryImpl

import retrofit2.Response
import umc.cozymate.data.DefaultResponse
import umc.cozymate.data.api.RuleService
import umc.cozymate.data.model.request.RuleRequest
import umc.cozymate.data.model.response.RuleResponse
import umc.cozymate.data.repository.repository.RuleRepository
import javax.inject.Inject

class RuleRepositoryImpl @Inject constructor(
    private val api : RuleService
) : RuleRepository {


    override suspend fun createRule(
        accessToken: String,
        roomId: Int,
        request: RuleRequest
    ): Response<DefaultResponse> {
        return api.createRule(accessToken, roomId, request)
    }

    override suspend fun getRule(
        accessToken: String,
        roomId: Int
    ): Response<RuleResponse> {
        return api.getRule(accessToken, roomId)
    }

    override suspend fun deleteRule(
        accessToken: String,
        roomId: Int,
        ruleId: Int
    ): Response<DefaultResponse> {
        return api.deleteRule(accessToken, roomId, ruleId)
    }

}