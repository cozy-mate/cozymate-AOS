package umc.cozymate.data.model.request

import com.google.gson.annotations.SerializedName

data class TodoRequest(
    @SerializedName("mateIdList")
    val mateIdList: List<Int>,
    @SerializedName("content")
    val content: String,
    @SerializedName("timePoint")
    val timePoint : String
)
