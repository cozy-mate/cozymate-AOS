package umc.cozymate.data.model.response.member


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VerifyMailResponse(
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
        @SerialName("tokenResponseDTO")
        val tokenResponseDTO: TokenResponseDTO
    ) {
        @Serializable
        data class TokenResponseDTO(
            @SerialName("accessToken")
            val accessToken: String,
            @SerialName("message")
            val message: String,
            @SerialName("refreshToken")
            val refreshToken: String
        )
    }
}