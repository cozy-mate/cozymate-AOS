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
        var birthday: String,
        @SerialName("gender")
        var gender: String,
        @SerialName("name")
        var name: String,
        @SerialName("nickname")
        var nickname: String,
        @SerialName("persona")
        var persona: Int
    )
}