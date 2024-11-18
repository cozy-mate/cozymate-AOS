package umc.cozymate.data.model.entity

import com.google.gson.annotations.SerializedName

data class TodoData(
    @SerializedName("memberDetail")
    val memberDetail : TestInfo,
    @SerializedName("todoList")
    val todoList : List<TodoItem>
){
    data class TodoItem(
        @SerializedName("todoId")
        val todoId: Int,

        @SerializedName("content")
        val content: String,

        @SerializedName("completed")
        var completed: Boolean,

        @SerializedName("todoType")
        val todoType : String,

        @SerializedName("mateIdList")
        val mateIdList: List<Int>


    )
}
