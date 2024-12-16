package umc.cozymate.data.model.response.roommate


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchRoommateResponse(
    @SerialName("code")
    val code: String,
    @SerialName("isSuccess")
    val isSuccess: Boolean,
    @SerialName("message")
    val message: String,
    @SerialName("result")
    val result: List<Result>
) {
    @Serializable
    data class Result(
        @SerialName("equality")
        val equality: Int,
        @SerialName("memberDetail")
        val memberDetail: MemberDetail
    ) {
        @Serializable
        data class MemberDetail(
            @SerialName("birthday")
            val birthday: String,
            @SerialName("gender")
            val gender: String,
            @SerialName("majorName")
            val majorName: String,
            @SerialName("memberId")
            val memberId: Int,
            @SerialName("nickname")
            val nickname: String,
            @SerialName("persona")
            val persona: Int,
            @SerialName("universityId")
            val universityId: Int,
            @SerialName("universityName")
            val universityName: String
        )
    }
}