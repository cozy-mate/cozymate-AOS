package umc.cozymate.data.repository.repository

import retrofit2.Response
import umc.cozymate.data.DefaultResponse
import umc.cozymate.data.model.request.TodoInfoRequest
import umc.cozymate.data.model.request.UpdateTodoRequest
import umc.cozymate.data.model.response.ruleandrole.TodoResponse


interface TodoRepository {
    suspend fun getTodo(accessToken: String, roomId : Int, timePoint: String?) : Response<TodoResponse>
    suspend fun updateTodo(accessToken: String, request: UpdateTodoRequest) : Response<DefaultResponse>
    suspend fun createTodo(accessToken: String, roomId: Int, request: TodoInfoRequest) : Response<DefaultResponse>

}