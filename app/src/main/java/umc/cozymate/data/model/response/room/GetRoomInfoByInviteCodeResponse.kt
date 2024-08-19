package umc.cozymate.data.model.response.room


import com.google.gson.annotations.SerializedName
import umc.cozymate.data.model.entity.RoomInfo

data class GetRoomInfoByInviteCodeResponse(
    @SerializedName("code")
    val code: String,
    @SerializedName("isSuccess")
    val isSuccess: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("result")
    val result: RoomInfo?
)