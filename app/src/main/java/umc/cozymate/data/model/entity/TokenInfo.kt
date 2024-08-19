package umc.cozymate.data.model.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TokenInfo(
    @SerialName("accessToken")
    var accessToken: String,
    @SerialName("message")
    var message: String,
    @SerialName("refreshToken")
    var refreshToken: String?
)
