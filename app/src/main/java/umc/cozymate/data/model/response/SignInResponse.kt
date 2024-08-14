package umc.cozymate.data.model.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
        @SerialName("memberInfoDTO")
        val memberInfoDTO: MemberInfoDTO?,
        @SerialName("tokenResponseDTO")
        val tokenResponseDTO: TokenResponseDTO
    ) {
        @Serializable
        data class MemberInfoDTO(
            @SerialName("birthday")
            val birthday: String,
            @SerialName("gender")
            val gender: String,
            @SerialName("name")
            val name: String,
            @SerialName("nickname")
            val nickname: String,
            @SerialName("persona")
            val persona: Int
        )

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