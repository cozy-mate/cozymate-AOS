package umc.cozymate.data.model.request


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SendMailRequest(
    @SerialName("mailAddress")
    val mailAddress: String,
    @SerialName("universityId")
    val universityId: Int
)