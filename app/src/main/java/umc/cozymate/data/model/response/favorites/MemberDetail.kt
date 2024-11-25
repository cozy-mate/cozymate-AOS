package umc.cozymate.data.model.response.favorites

data class MemberDetail(
    val birthday: String,
    val gender: String,
    val majorName: String,
    val memberId: Int,
    val nickname: String,
    val persona: Int,
    val universityId: Int,
    val universityName: String
)