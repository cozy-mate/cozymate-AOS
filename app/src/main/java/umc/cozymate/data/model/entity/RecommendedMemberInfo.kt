package umc.cozymate.data.model.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecommendedMemberInfo(
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
