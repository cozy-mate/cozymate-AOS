package umc.cozymate.data.repository.repositoryImpl

import retrofit2.Response
import umc.cozymate.data.DefaultResponse
import umc.cozymate.data.api.TodoService
import umc.cozymate.data.dto.TodoResponse
import umc.cozymate.data.model.request.TodoInfoRequest
import umc.cozymate.data.model.request.UpdateTodoRequest
import umc.cozymate.data.repository.repository.TodoRepository
import javax.inject.Inject

class TodoRepositoryImpl @Inject constructor(
    private val api: TodoService
) : TodoRepository {
    override suspend fun getTodo(
        roomId: Int,
        timePoint: String?
    ): Response<TodoResponse> {
        return api.getTodo(roomId,timePoint)
    }


    override suspend fun updateTodo(
        request: UpdateTodoRequest
    ):  Response<DefaultResponse> {
        return api.updateTodo(request)
    }

    override suspend fun createTodo(
        roomId: Int,
        request: TodoInfoRequest
    ):  Response<DefaultResponse> {
        return api.createTodo(roomId,request)
    }

}