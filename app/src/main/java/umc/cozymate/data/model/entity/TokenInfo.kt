package umc.cozymate.data.model.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TokenInfo(
    @SerialName("accessToken")
    val accessToken: String,
    @SerialName("message")
    val message: String,
    @SerialName("refreshToken")
    val refreshToken: String?
)
