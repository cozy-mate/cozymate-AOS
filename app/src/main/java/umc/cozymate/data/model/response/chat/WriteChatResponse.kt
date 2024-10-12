package umc.cozymate.data.model.response.chat

import com.google.gson.annotations.SerializedName

data class WriteChatResponse(
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
        @SerializedName("chatRoomId")
        val chatRoomId : Int
    )
}
