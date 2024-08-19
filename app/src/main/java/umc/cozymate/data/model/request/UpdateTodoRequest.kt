package umc.cozymate.data.model.request

data class UpdateTodoRequest(
    val todoId: Int,
    val completed : Boolean
)
