package umc.cozymate.data.model.response.room


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetPendingMemberListResponse(
    @SerialName("code")
    val code: String,
    @SerialName("isSuccess")
    val isSuccess: Boolean,
    @SerialName("message")
    val message: String,
    @SerialName("result")
    val result: List<Result>
) {
    @Serializable
    data class Result(
        @SerialName("mateEquality")
        val mateEquality: Int,
        @SerialName("mateId")
        val mateId: Int,
        @SerialName("memberId")
        val memberId: Int,
        @SerialName("nickname")
        val nickname: String,
        @SerialName("persona")
        val persona: Int
    )
}