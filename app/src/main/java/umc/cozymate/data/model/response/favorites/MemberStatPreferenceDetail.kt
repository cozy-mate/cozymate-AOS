package umc.cozymate.data.model.response.favorites

data class MemberStatPreferenceDetail(
    val equality: Int,
    val memberDetail: MemberDetail,
    val preferenceStats: PreferenceStats
)