package umc.cozymate.data.model.request


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateInfoRequest(
    @SerialName("birthday")
    val birthday: String,
    @SerialName("majorName")
    val majorName: String,
    @SerialName("nickname")
    val nickname: String,
    @SerialName("persona")
    val persona: Int
)