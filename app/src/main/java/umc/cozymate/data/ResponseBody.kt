package umc.cozymate.data

data class ResponseBody<T>(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: T
)