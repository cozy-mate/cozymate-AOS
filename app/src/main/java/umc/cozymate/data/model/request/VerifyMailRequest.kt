package umc.cozymate.data.model.request


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VerifyMailRequest(
    @SerialName("code")
    val code: String,
    @SerialName("majorName")
    val majorName: String,
    @SerialName("universityId")
    val universityId: Int
)