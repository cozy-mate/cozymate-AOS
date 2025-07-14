package umc.cozymate.data.model.response.chat

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import umc.cozymate.data.model.entity.ChatContentData
import umc.cozymate.data.model.entity.PageInfo

@Serializable
data class ChatContentsResponse(
    @SerializedName("isSuccess")
    val isSuccess: Boolean,

    @SerializedName("code")
    val code: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("result")
    val result: PageInfo<ChatRoomInfo>
){
    @Serializable
    data class ChatRoomInfo(
        @SerializedName("memberId")
        val memberId : Int?,
        @SerializedName("content")
        var content : List<ChatContentData>
    )
}
