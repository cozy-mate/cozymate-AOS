package umc.cozymate.data.model.response

import umc.cozymate.data.model.entity.RuleInfo

data class RuleResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: List<RuleInfo>
)
