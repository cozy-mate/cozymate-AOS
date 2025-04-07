package umc.cozymate.data.model.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MemberDetail(
    @SerialName("birthday")
    var birthday: String,
    @SerialName("gender")
    var gender: String,
    @SerialName("nickname")
    var nickname: String,
    @SerialName("persona")
    var persona: Int,
    @SerialName("memberStatPreferenceDto")
    val memberStatPreferenceDto: PreferenceList,
)