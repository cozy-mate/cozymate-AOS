package umc.cozymate.data.model.response.member.stat


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import umc.cozymate.data.model.entity.MemberDetailInfo

@Serializable
data class GetRecommendedRoommateResponse(
    @SerialName("code")
    val code: String,
    @SerialName("isSuccess")
    val isSuccess: Boolean,
    @SerialName("message")
    val message: String,
    @SerialName("result")
    val result: Result
) {
    @Serializable
    data class Result(
        @SerialName("hasNext")
        val hasNext: Boolean,
        @SerialName("memberList")
        val memberList: List<Member>,
        @SerialName("page")
        val page: Int
    ) {
        @Serializable
        data class Member(
            @SerialName("equality")
            val equality: Int,
            @SerialName("memberDetail")
            val memberDetail: MemberDetailInfo,
            @SerialName("preferenceStats")
            val preferenceStats: List<PreferenceStat>
        ) {
            @Serializable
            data class PreferenceStat(
                @SerialName("color")
                val color: String,
                @SerialName("stat")
                val stat: String,
                @SerialName("value")
                val value: String
            )
        }
    }
}