package umc.cozymate.data.model.response.room

data class GetInvitedMembersResponse(
    val code: String,
    val isSuccess: Boolean,
    val message: String,
    val result: List<Result>
) {
    data class Result(
        val memberId: Int,
        val mateId: Int,
        val nickname: String,
        val persona: Int,
        val mateEquality: Int
    )
}