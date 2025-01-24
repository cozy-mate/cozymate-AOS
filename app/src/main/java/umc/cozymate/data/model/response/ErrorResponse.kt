package umc.cozymate.data.model.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    @SerialName("code")
    val code: String,
    @SerialName("isSuccess")
    val isSuccess: Boolean,
    @SerialName("message")
    var message: String,
    @SerialName("result")
    var result: String?
)