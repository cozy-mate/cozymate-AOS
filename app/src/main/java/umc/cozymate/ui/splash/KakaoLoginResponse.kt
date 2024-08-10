package umc.cozymate.ui.splash

data class KakaoLoginResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: Result
)

data class Result(
    val message: String,
    val refreshToken: String,
    val memberInfoDTO: MemberInfoDTO
)

data class MemberInfoDTO(
    val name: String,
    val nickname: String,
    val gender: String,
    val birthday: String,
    val persona: Int
)