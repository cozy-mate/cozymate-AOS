package umc.cozymate.data.model.response.member.stat


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetRoommateListByEqualityResponse(
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
        @SerialName("hasNext")
        val hasNext: Boolean,
        @SerialName("memberList")
        val memberList: List<GetRecommendedRoommateResponse.Result.Member>,
        @SerialName("page")
        val page: Int
    )
}