package umc.cozymate.data.model.response.ruleandrole

import umc.cozymate.data.model.entity.RuleData

data class RuleResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: List<RuleData>
)
