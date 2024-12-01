package umc.cozymate.data.model.response.roommate

data class OtherUsersInfoListResponse(
    val page: Int,
    val hasNext: Boolean,
    val memberList: List<MemberX>
)