package umc.cozymate.data.model.response.room

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateRoomResponse(
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
        @SerialName("inviteCode")
        val inviteCode: String,
        @SerialName("mateList")
        val mateList: List<Mate>,
        @SerialName("name")
        val name: String,
        @SerialName("profileImage")
        val profileImage: Int,
        @SerialName("roomId")
        val roomId: Int
    ) {
        @Serializable
        data class Mate(
            @SerialName("mateId")
            val mateId: Int,
            @SerialName("memberId")
            val memberId: Int,
            @SerialName("nickname")
            val nickname: String
        )
    }
}

//data class CreateRoomResponse<T>(
//    val code: String,
//    val isSuccess: Boolean,
//    val message: String,
//    val result: T?
//) {
//
//    data class SuccessResult(
//        val id: Int,
//        val profileImage: Int,
//        val inviteCode: String,
//    )
//
//    data class ErrorResult(
//        val maxMateNum: String,
//        val name: String,
//        val profileImage: String
//    )
//}