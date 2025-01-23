package umc.cozymate.data.model.response.favorites

data class MemberStatPreferenceDetail(
    val memberDetail: MemberDetail,
    val equality: Int,
    val preferenceStats: PreferenceStats
)