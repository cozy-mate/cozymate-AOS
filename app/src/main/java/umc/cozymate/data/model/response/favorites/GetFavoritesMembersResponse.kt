package umc.cozymate.data.model.response.favorites

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class GetFavoritesMembersResponse(
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
        @SerialName("page")
        val page: Int,
        @SerialName("result")
        val result: List<FavoriteMateItem>
    ) {
        @Serializable
        data class FavoriteMateItem(
            @SerialName("memberFavoriteId")
            val favoriteId: Int,
            @SerialName("memberStatPreferenceDetail")
            val memberStatPreferenceDetail: MemberStatPreferenceDetail
        ) {
            @Serializable
            data class MemberStatPreferenceDetail(
                @SerialName("equality")
                val equality: Int,
                @SerialName("memberDetail")
                val memberDetail: MemberDetail,
                @SerialName("preferenceStats")
                val preferenceStats: List<PreferenceStat>
            ) {
                @Serializable
                data class MemberDetail(
                    @SerialName("birthday")
                    val birthday: String,
                    @SerialName("gender")
                    val gender: String,
                    @SerialName("majorName")
                    val majorName: String,
                    @SerialName("memberId")
                    val memberId: Int,
                    @SerialName("nickname")
                    val nickname: String,
                    @SerialName("persona")
                    val persona: Int,
                    @SerialName("universityId")
                    val universityId: Int,
                    @SerialName("universityName")
                    val universityName: String
                )

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
}