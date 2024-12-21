package umc.cozymate.data.model.entity

import com.google.gson.annotations.SerializedName

data class ChatContentData(
    @SerializedName("nickname")
    val nickname : String,
    @SerializedName("content")
    val content : String,
    @SerializedName("datetime")
    val datetime : String
)
