package umc.cozymate.data.model.response.chat

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import umc.cozymate.data.model.entity.ChatContentData
import umc.cozymate.data.model.entity.ChatRoomData
import umc.cozymate.data.model.entity.PageInfo

@Serializable
data class ChatRoomResponse(
    @SerializedName("isSuccess")
    val isSuccess: Boolean,

    @SerializedName("code")
    val code: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("result")
    val result: PageInfo<List<ChatRoomData>>
)

