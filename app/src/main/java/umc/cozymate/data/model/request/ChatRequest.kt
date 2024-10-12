package umc.cozymate.data.model.request

import com.google.gson.annotations.SerializedName

data class ChatRequest(
    @SerializedName("content")
    val content: String
)
