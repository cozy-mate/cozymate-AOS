package umc.cozymate.data.model.request


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateRoomInfoRequest(
    @SerialName("hashtagList")
    val hashtagList: List<String>?,
    @SerialName("name")
    val name: String,
    @SerialName("persona")
    val persona: Int
)