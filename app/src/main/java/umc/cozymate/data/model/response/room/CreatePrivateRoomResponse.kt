package umc.cozymate.data.model.response.room

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class CreatePrivateRoomResponse(
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
        @SerialName("arrivalMateNum")
        val arrivalMateNum: Int,
        @SerialName("difference")
        val difference: Difference,
        @SerialName("dormitoryName")
        val dormitoryName: String,
        @SerialName("equality")
        val equality: Int,
        @SerialName("favoriteId")
        val favoriteId: Int,
        @SerialName("hashtagList")
        val hashtagList: List<String>,
        @SerialName("inviteCode")
        val inviteCode: String,
        @SerialName("isRoomManager")
        val isRoomManager: Boolean,
        @SerialName("managerMemberId")
        val managerMemberId: Int,
        @SerialName("managerNickname")
        val managerNickname: String,
        @SerialName("mateDetailList")
        val mateDetailList: List<MateDetail>,
        @SerialName("maxMateNum")
        val maxMateNum: Int,
        @SerialName("name")
        val name: String,
        @SerialName("persona")
        val persona: Int,
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
        data class MateDetail(
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