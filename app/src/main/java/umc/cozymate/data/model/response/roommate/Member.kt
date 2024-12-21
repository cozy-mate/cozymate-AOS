package umc.cozymate.data.model.response.roommate

data class Member(
    val equality: Any,
    val memberDetail: MemberDetail,
    val preferenceStats: List<PreferenceStats>
)