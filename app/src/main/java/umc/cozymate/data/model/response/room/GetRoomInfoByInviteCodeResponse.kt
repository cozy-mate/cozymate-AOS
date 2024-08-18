package umc.cozymate.data.model.response.room


import com.google.gson.annotations.SerializedName

data class GetRoomInfoByInviteCodeResponse(
    @SerializedName("code")
    val code: String,
    @SerializedName("isSuccess")
    val isSuccess: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("result")
    val result: Result?
) {
    data class Result(
        @SerializedName("managerName")
        val managerName: String,
        @SerializedName("maxMateNum")
        val maxMateNum: Int,
        @SerializedName("name")
        val name: String,
        @SerializedName("roomId")
        val roomId: Int
    )
}