package umc.cozymate.data.model.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MemberDetail(
    @SerialName("birthday")
    val birthday: String,
    @SerialName("gender")
    val gender: String,
    @SerialName("name")
    val name: String,
    @SerialName("nickname")
    val nickname: String,
    @SerialName("persona")
    val persona: Int = 1,
    @SerialName("universityId")
    val universityId: Int = 0,
)