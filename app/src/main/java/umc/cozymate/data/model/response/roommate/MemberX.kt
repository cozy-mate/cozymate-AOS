package umc.cozymate.data.model.response.roommate

data class MemberX(
    val memberDetail: MemberDetail,
    val equality: Int,
    val preferenceStats: PreferenceStatsX
)