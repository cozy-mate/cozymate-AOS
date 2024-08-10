package umc.cozymate.ui.splash

data class KakaoResponse(
    val code: String,
    val isSuccess: Boolean,
    val message: String,
    val result: ResultX
)