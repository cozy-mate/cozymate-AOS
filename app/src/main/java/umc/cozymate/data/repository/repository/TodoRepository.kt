package umc.cozymate.data.repository.repository

import retrofit2.Response
import umc.cozymate.data.DefaultResponse
import umc.cozymate.data.dto.TodoResponse
import umc.cozymate.data.model.request.TodoInfoRequest
import umc.cozymate.data.model.request.UpdateTodoRequest


interface TodoRepository {
    suspend fun getTodo(roomId : Int, timePoint: String?) : Response<TodoResponse>
    suspend fun updateTodo(request: UpdateTodoRequest) : Response<DefaultResponse>
    suspend fun createTodo(roomId: Int, request: TodoInfoRequest) : Response<DefaultResponse>

}