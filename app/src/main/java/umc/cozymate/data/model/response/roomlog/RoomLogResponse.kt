package umc.cozymate.data.model.response.roomlog


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RoomLogResponse(
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
            @SerialName("content")
            val content: String,
            @SerialName("createdAt")
            val createdAt: String
        )
    }
}