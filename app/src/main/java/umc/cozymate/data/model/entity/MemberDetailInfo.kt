package umc.cozymate.data.model.entity


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MemberDetailInfo(
    @SerialName("birthday")
    var birthday: String,
    @SerialName("gender")
    var gender: String,
    @SerialName("majorName")
    var majorName: String,
    @SerialName("memberId")
    var memberId: Int,
    @SerialName("nickname")
    var nickname: String,
    @SerialName("persona")
    var persona: Int,
    @SerialName("universityName")
    var universityName: String
)