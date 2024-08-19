package umc.cozymate.data.model.response.room


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import umc.cozymate.data.model.response.room.GetRoomInfoByOwnerResponse.Result

@Serializable
data class JoinRoomResponse(
    @SerialName("code")
    val code: String,
    @SerialName("isSuccess")
    val isSuccess: Boolean,
    @SerialName("message")
    val message: String,
    @SerialName("result")
    val result: Result?
)