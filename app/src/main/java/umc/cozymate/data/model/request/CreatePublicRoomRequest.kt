package umc.cozymate.data.model.request

data class CreatePublicRoomRequest(
    val name: String,
    val profileImage: Int,
    val maxMateNum: Int,
    val hashtags: List<String>,
)