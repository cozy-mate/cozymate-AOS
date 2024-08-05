package umc.cozymate.data

data class DefaultResponse(
    val code: String,
    val isSuccess: Boolean,
    val message: String,
    val result: String
)