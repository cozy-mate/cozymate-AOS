package umc.cozymate.data.model.response.chat

import com.google.gson.annotations.SerializedName
import umc.cozymate.data.model.entity.ChatContentData

data class ChatContentsResponse(
    @SerializedName("isSuccess")
    val isSuccess: Boolean,

    @SerializedName("code")
    val code: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("result")
    val result: Result
){
    data class Result(
        @SerializedName("recipientId")
        val recipientId : Int,
        @SerializedName("chatContents")
        var chatContents : List<ChatContentData>
    )
}
