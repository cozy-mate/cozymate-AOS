package umc.cozymate.data.model.request

data class CreateRoomRequest(
    val name: String,
    val profileImage: Int,
    val maxMateNum: Int,
    val creatorId: Int, // 방장
)