package umc.cozymate.data.model.response.chat

import com.google.gson.annotations.SerializedName
import umc.cozymate.data.model.entity.ChatRoomData

data class ChatRoomResponse(
    @SerializedName("isSuccess")
    val isSuccess: Boolean,

    @SerializedName("code")
    val code: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("result")
    val result: List<ChatRoomData>
)
//{
//    data class Result(
//        @SerializedName("persona")
//        val persona : Int,
//        @SerializedName("nickName")
//        val nickName: String,
//        @SerializedName("lastContent")
//        val lastContent : String,
//        @SerializedName("chatRoomId")
//        val chatRoomId : Int
//    )
//}
