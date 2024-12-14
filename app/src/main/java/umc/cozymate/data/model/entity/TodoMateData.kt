package umc.cozymate.data.model.entity

import com.google.gson.annotations.SerializedName

data class TodoMateData(
    @SerializedName("persona")
    val persona : Int,
    @SerializedName("mateTodoList")
    val mateTodoList : List<TodoItem>
){
    data class TodoItem(
        @SerializedName("id")
        val id: Int,

        @SerializedName("content")
        val content: String,

        @SerializedName("completed")
        var completed: Boolean
    )
}
