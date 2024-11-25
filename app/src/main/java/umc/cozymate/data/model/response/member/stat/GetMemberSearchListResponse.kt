package umc.cozymate.data.model.response.member.stat


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import umc.cozymate.data.model.entity.MemberDetailInfo

@Serializable
data class GetMemberSearchListResponse(
    @SerialName("code")
    val code: String,
    @SerialName("isSuccess")
    val isSuccess: Boolean,
    @SerialName("message")
    val message: String,
    @SerialName("result")
    val result: List<Result>
) {
    @Serializable
    data class Result(
        @SerialName("equality")
        val equality: Int,
        @SerialName("memberDetail")
        val memberDetail: MemberDetailInfo
    )
}