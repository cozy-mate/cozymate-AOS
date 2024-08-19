package umc.cozymate.data.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SignInRequest(
    @SerialName("clientId")
    val clientId: String,
    @SerialName("socialType")
    val socialType: String
)

