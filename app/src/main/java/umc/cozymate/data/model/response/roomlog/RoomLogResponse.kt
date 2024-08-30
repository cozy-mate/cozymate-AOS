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
    val result: RoomLogResult
) {
    @Serializable
    data class RoomLogResult(
        @SerialName("hasNext")
        val hasNext: Boolean,
        @SerialName("page")
        val page: Int,
        @SerialName("result")
        val result: List<RoomLogItem>
    ) {
        @Serializable
        data class RoomLogItem(
            @SerialName("content")
            val content: String,
            @SerialName("createdAt")
            val createdAt: String
        )
    }
}