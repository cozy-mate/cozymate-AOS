package umc.cozymate.data.model.request

import com.google.gson.annotations.SerializedName

data class RuleRequest(
    @SerializedName("content")
    val content: String,
    @SerializedName("memo")
    val memo : String
)
