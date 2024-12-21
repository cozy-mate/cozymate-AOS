package umc.cozymate.data.model.entity

import com.google.gson.annotations.SerializedName

data class TodoData(
    @SerializedName("memberDetail")
    val memberDetail: MemberDetailInfo,
    @SerializedName("todoList")
    var todoList: List<TodoItem>
){
    data class TodoItem(
        @SerializedName("todoId")
        val todoId: Int = 0,

        @SerializedName("content")
        val content: String = "",

        @SerializedName("completed")
        var completed: Boolean = false,

        @SerializedName("todoType")
        val todoType : String = "self",

        @SerializedName("mateIdList")
        val mateIdList: List<Int>


    )
}
