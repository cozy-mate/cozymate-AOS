package umc.cozymate.data.model.response.roommate

data class OtherMemberDetailInfoResponse(
    val equality: Int,
    val favoriteId: Int,
    val hasRequestedRoomEntry: Boolean,
    val memberDetail: MemberDetail,
    val memberStatDetail: MemberStatDetail,
    val roomId: Int
)