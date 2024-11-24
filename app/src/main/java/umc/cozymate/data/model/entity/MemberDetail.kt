package umc.cozymate.data.model.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// nickname, gender, birthday, persona, *universityId

@Serializable
data class MemberDetail(
    @SerialName("birthday")
    var birthday: String,
    @SerialName("gender")
    var gender: String,
    @SerialName("nickname")
    var nickname: String,
    @SerialName("persona")
    var persona: Int = 1,
    @SerialName("universityId")
    var universityId: Int = 0,
)