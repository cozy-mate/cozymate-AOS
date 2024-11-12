package umc.cozymate.data.model.response.member


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import umc.cozymate.data.model.entity.MemberDetailInfo

@Serializable
data class MemberInfoResponse(
    @SerialName("code")
    val code: String,
    @SerialName("isSuccess")
    val isSuccess: Boolean,
    @SerialName("message")
    val message: String,
    @SerialName("result")
    val result: MemberDetailInfo?,
)