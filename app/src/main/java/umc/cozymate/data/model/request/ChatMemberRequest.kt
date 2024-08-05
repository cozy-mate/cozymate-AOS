package umc.cozymate.data.model.request

data class ChatMemberRequest(
    val content: String,
    val senderId: Int
)