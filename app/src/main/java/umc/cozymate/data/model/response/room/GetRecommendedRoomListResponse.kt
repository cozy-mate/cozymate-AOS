package umc.cozymate.data.model.response.room


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetRecommendedRoomListResponse(
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
        @SerialName("page")
        val page: Int,
        @SerialName("result")
        val result: List<Result>
    ) {
        @Serializable
        data class Result(
            @SerialName("equalMemberStatNum")
            val equalMemberStatNum: Map<String, Int>,
            @SerialName("equality")
            val equality: Int,
            @SerialName("hashtags")
            val hashtags: List<String>,
            @SerialName("maxMateNum")
            val maxMateNum: Int,
            @SerialName("name")
            val name: String,
            @SerialName("numOfArrival")
            val numOfArrival: Int,
            @SerialName("roomId")
            val roomId: Int
        )
    }
}