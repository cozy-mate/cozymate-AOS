package umc.cozymate.data.model.response.room

data class GetRoomPendingMemberResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: Boolean
)
