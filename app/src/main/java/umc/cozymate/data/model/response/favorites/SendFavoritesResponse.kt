package umc.cozymate.data.model.response.favorites

import kotlinx.serialization.SerialName

data class SendFavoritesResponse(
    @SerialName("isSuccess")
    val isSuccess: Boolean,
    @SerialName("code")
    val code: String,
    @SerialName("message")
    val message: String,
    @SerialName("result")
    val result: String
)
