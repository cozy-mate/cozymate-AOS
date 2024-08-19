package umc.cozymate.data.model.response.member


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MemberInfoResponse(
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
}