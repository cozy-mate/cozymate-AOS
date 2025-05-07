package umc.cozymate.data.model.response.roomlog


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationLogResponse(
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
        val result: List<LogItem>
    ) {
        @Serializable
        data class LogItem(
            @SerialName("category")
            val category: String,
            @SerialName("content")
            val content: String,
            @SerialName("createdAt")
            val createdAt: String,
            @SerialName("targetId")
            val targetId: Int
        )
    }
}