package umc.cozymate.data.entity

import com.google.gson.annotations.SerializedName

data class TodoItem(
    @SerializedName("id")
    val id: Int,

    @SerializedName("content")
    val content: String,

    @SerializedName("completed")
    var completed: Boolean
)
