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
        @SerialName("memberList")
        val memberList: List<Member>
    ) {
        @Serializable
        data class Member(
            @SerialName("equality")
            val equality: Int,
            @SerialName("memberDetail")
            val memberDetail: MemberDetailInfo,
            @SerialName("preferenceStats")
            val preferenceStats: PreferenceStats
        ) {
            @Serializable
            data class PreferenceStats(
                @SerialName("additionalProp1")
                val additionalProp1: AdditionalProp1,
                @SerialName("additionalProp2")
                val additionalProp2: AdditionalProp2,
                @SerialName("additionalProp3")
                val additionalProp3: AdditionalProp3
            ) {
                @Serializable
                class AdditionalProp1

                @Serializable
                class AdditionalProp2

                @Serializable
                class AdditionalProp3
            }
        }
    }
}