package umc.cozymate.data.model.response

data class OtherUserInfoResponse(
    val memberId: Int,
    val memberName: String,
    val memberNickName: String,
    val memberAge: Int,
    val memberPersona: Int,
    val numOfRoommate: Int,
    val equality: Int
)
