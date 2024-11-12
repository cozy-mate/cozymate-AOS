package umc.cozymate.data.model.response.ruleandrole

import com.google.gson.annotations.SerializedName

data class CreateTodoResponse (
    @SerializedName("isSuccess")
    val isSuccess: Boolean,

    @SerializedName("code")
    val code: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("result")
    val result: TodoResponse.Result
){
    data class Result(
        @SerializedName("todoId")
        val todoId : Int
    )
}