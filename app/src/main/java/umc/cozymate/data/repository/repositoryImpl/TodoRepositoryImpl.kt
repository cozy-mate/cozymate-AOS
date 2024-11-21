package umc.cozymate.data.repository.repositoryImpl

import retrofit2.Response
import umc.cozymate.data.DefaultResponse
import umc.cozymate.data.api.TodoService
import umc.cozymate.data.model.request.TodoRequest
import umc.cozymate.data.model.response.ruleandrole.CreateResponse
import umc.cozymate.data.model.response.ruleandrole.TodoResponse
import umc.cozymate.data.repository.repository.TodoRepository
import javax.inject.Inject

class TodoRepositoryImpl @Inject constructor(
    private val api: TodoService
) : TodoRepository {
    override suspend fun getTodo(
        accessToken: String,
        roomId: Int,
        timePoint: String?
    ): Response<TodoResponse> {
        return api.getTodo( accessToken, roomId,timePoint)
    }

    override suspend fun updateTodo(
        accessToken: String,
        roomId: Int,
        todoId: Int,
        completed: Boolean
    ): Response<DefaultResponse> {
        return api.updateTodo(accessToken, roomId, todoId, completed)
    }


    override suspend fun createTodo(
        accessToken: String,
        roomId: Int,
        request: TodoRequest
    ): Response<CreateResponse> {
        return api.createTodo(accessToken, roomId,request)
    }

    override suspend fun editTodo(
        accessToken: String,
        roomId: Int,
        todoId: Int,
        request: TodoRequest
    ): Response<DefaultResponse> {
        return api.editTodo(accessToken, roomId, todoId, request)
    }


}