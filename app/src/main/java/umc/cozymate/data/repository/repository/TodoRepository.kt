package umc.cozymate.data.repository.repository

import retrofit2.Response
import umc.cozymate.data.DefaultResponse
import umc.cozymate.data.model.request.TodoRequest
import umc.cozymate.data.model.response.ruleandrole.CreateResponse
import umc.cozymate.data.model.response.ruleandrole.TodoResponse


interface TodoRepository {
    suspend fun getTodo(accessToken: String, roomId : Int, timePoint: String?) : Response<TodoResponse>
    suspend fun updateTodo(accessToken: String, roomId : Int, todoId : Int, completed : Boolean ) : Response<DefaultResponse>
    suspend fun createTodo(accessToken: String, roomId: Int, request: TodoRequest) : Response<CreateResponse>
    suspend fun editTodo(accessToken: String, roomId : Int, todoId : Int,  request: TodoRequest) : Response<DefaultResponse>
    suspend fun deleteTodo(accessToken: String, roomId : Int, todoId : Int) : Response<DefaultResponse>
}