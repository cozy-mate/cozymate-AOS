package umc.cozymate.data.model.response.member


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import umc.cozymate.data.model.entity.MemberDetail
import umc.cozymate.data.model.entity.TokenInfo

@Serializable
data class SignInResponse(
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
        @SerialName("memberDetailDTO")
        val memberDetailDTO: MemberDetail?,
        @SerialName("tokenResponseDTO")
        val tokenResponseDTO: TokenInfo
    )
}