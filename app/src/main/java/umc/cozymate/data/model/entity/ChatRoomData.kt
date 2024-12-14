package umc.cozymate.data.model.entity

import com.google.gson.annotations.SerializedName

data class ChatRoomData(
    @SerializedName("persona")
    val persona : Int,
    @SerializedName("nickName")
    val nickName : String,
    @SerializedName("lastContent")
    val lastContent : String,
    @SerializedName("chatRoomId")
    val chatRoomId : Int
)
