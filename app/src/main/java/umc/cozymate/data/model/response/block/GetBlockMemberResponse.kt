package umc.cozymate.data.model.response.block

data class GetBlockMemberResponse(
    val code: String,
    val isSuccess: Boolean,
    val message: String,
    val result: List<Result>
)