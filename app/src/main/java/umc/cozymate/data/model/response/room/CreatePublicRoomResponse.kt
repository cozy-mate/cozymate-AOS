package umc.cozymate.data.model.response.room


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreatePublicRoomResponse(
    @SerialName("code")
    val code: String,
    @SerialName("isSuccess")
    val isSuccess: Boolean,
    @SerialName("message")
    val message: String,
    @SerialName("result")
    val result: Result
) {
    @Serializable
    data class Result(
        @SerialName("difference")
        val difference: Difference,
        @SerialName("equality")
        val equality: Int,
        @SerialName("hashtags")
        val hashtags: List<String>,
        @SerialName("inviteCode")
        val inviteCode: String,
        @SerialName("isRoomManager")
        val isRoomManager: Boolean,
        @SerialName("managerId")
        val managerId: Int,
        @SerialName("mateList")
        val mateList: List<Mate>,
        @SerialName("maxMateNum")
        val maxMateNum: Int,
        @SerialName("name")
        val name: String,
        @SerialName("numOfArrival")
        val numOfArrival: Int,
        @SerialName("profileImage")
        val profileImage: Int,
        @SerialName("roomId")
        val roomId: Int,
        @SerialName("roomType")
        val roomType: String
    ) {
        @Serializable
        data class Difference(
            @SerialName("blue")
            val blue: List<String>,
            @SerialName("red")
            val red: List<String>,
            @SerialName("white")
            val white: List<String>
        )

        @Serializable
        data class Mate(
            @SerialName("mateEquality")
            val mateEquality: Int,
            @SerialName("mateId")
            val mateId: Int,
            @SerialName("memberId")
            val memberId: Int,
            @SerialName("nickname")
            val nickname: String,
            @SerialName("persona")
            val persona: Int
        )
    }
}