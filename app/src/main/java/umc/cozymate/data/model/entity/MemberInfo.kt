package umc.cozymate.data.model.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MemberInfo(
    @SerialName("birthday")
    val birthday: String,
    @SerialName("gender")
    val gender: String,
    @SerialName("name")
    val name: String,
    @SerialName("nickName")
    val nickName: String,
    @SerialName("persona")
    val persona: Int = 0
)