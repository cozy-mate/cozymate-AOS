package umc.cozymate.data.model.response.room

data class GetRoomMemberStatResponse(
    val code: String,
    val isSuccess: Boolean,
    val message: String,
    val result: Result
) {
    data class Result(
        val memberList: List<Member>,
        val color: String
    ) {
        data class Member(
            val memberDetail: MemberDetail,
            val memberStat: Map<String, String>
        ) {
            data class MemberDetail(
                val memberId: Int,
                val nickname: String,
                val gender: String,
                val birthday: String,
                val universityName: String,
                val universityId: Int,
                val majorName: String,
                val persona: Int
            )
        }
    }
}
