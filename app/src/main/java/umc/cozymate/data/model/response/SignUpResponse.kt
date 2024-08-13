package umc.cozymate.data.model.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import umc.cozymate.data.model.entity.MemberInfo

@Serializable
data class SignUpResponse(
    @SerialName("code")
    val code: String,
    @SerialName("isSuccess")
    val isSuccess: Boolean,
    @SerialName("message")
    val message: String,
    @SerialName("result")
    val result: Result
) {
    @Serializable
    data class Result(
        @SerialName("accessToken")
        val accessToken: String,
        @SerialName("memberInfoDTO")
        val memberInfoDTO: MemberInfo,
        @SerialName("message")
        val message: String,
        @SerialName("refreshToken")
        val refreshToken: String
    )
}