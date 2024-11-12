package umc.cozymate.data.repository.repository

import retrofit2.Response
import umc.cozymate.data.DefaultResponse
import umc.cozymate.data.model.request.CreateTodoRequest
import umc.cozymate.data.model.request.UpdateTodoRequest
import umc.cozymate.data.model.response.ruleandrole.CreateTodoResponse
import umc.cozymate.data.model.response.ruleandrole.TodoResponse


interface TodoRepository {
    suspend fun getTodo(accessToken: String, roomId : Int, timePoint: String?) : Response<TodoResponse>
    suspend fun updateTodo(accessToken: String, request: UpdateTodoRequest) : Response<DefaultResponse>
    suspend fun createTodo(accessToken: String, roomId: Int, request: CreateTodoRequest) : Response<CreateTodoResponse>

}