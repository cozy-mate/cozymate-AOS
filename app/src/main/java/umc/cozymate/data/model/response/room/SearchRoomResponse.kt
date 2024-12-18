package umc.cozymate.data.model.response.room


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchRoomResponse(
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
        @SerialName("arrivalMateNum")
        val arrivalMateNum: Int,
        @SerialName("equality")
        val equality: Int,
        @SerialName("name")
        val name: String,
        @SerialName("roomId")
        val roomId: Int
    )
}