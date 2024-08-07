package umc.cozymate.data.model.response

import umc.cozymate.data.DefaultResponse
import umc.cozymate.data.model.entity.MemberInfo

data class JoinMemberResponse(
    val isSuccess: Boolean,
    val message: String,
    val code: String,
    val result: Boolean
)

fun DefaultResponse.mapToMemberInfo(): MemberInfo {
    return MemberInfo(
        birthday = "20-00-00",
        gender = "MALE",
        name = "a",
        nickname = "",
        persona = 0
    )
}