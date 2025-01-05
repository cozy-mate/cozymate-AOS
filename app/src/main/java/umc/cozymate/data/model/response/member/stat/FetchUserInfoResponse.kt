package umc.cozymate.data.model.response.member.stat

data class FetchUserInfoResponse(
    val code: String,
    val isSuccess: Boolean,
    val message: String,
    val result: Int
)
