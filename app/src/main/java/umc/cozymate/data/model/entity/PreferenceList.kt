package umc.cozymate.data.model.entity


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PreferenceList(
    @SerialName("preferenceList")
    val preferenceList: List<String>
)