package umc.cozymate.data.model.response.room


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IsRoomExistResponse(
    @SerialName("code")
    val code: String,
    @SerialName("isSuccess")
    val isSuccess: Boolean,
    @SerialName("message")
    val message: String,
    @SerialName("result")
    val result: Result?
) {
    @Serializable
    data class Result(
        @SerialName("roomId")
        val roomId: Int
    )
}