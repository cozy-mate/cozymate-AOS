package umc.cozymate.data.model.response.roommate

data class MemberX(
    val memberDetail: MemberDetail,
    val equality: String,
    val preferenceStats: List<PreferenceStatsX>
)