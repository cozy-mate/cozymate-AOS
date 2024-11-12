package umc.cozymate.data.model.request

import com.google.gson.annotations.SerializedName

data class CreateTodoRequest(
    @SerializedName("mateIdList")
    val mateIdList: List<Int>,
    @SerializedName("content")
    val content: String,
    @SerializedName("timePoint")
    val timePoint : String
)
