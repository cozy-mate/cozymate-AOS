package umc.cozymate.data.model.response

data class CreateRoomResponse<T>(
    val code: String,
    val isSuccess: Boolean,
    val message: String,
    val result: T?
) {

    data class SuccessResult(
        val id: Int,
        val profileImage: Int,
        val inviteCode: String,
    )

    data class ErrorResult(
        val maxMateNum: String,
        val name: String,
        val profileImage: String
    )
}