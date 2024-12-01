package umc.cozymate.data.model.response.roommate

data class MemberDetail(
    val memberId: Int,
    val nickname: String,
    val gender: String,
    val birthday: String,
    val universityName: String,
    val universityId: Int,
    val majorName: Any,
    val persona: Int,
)