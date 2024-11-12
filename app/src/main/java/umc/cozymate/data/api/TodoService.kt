package umc.cozymate.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import umc.cozymate.data.DefaultResponse
import umc.cozymate.data.model.request.CreateTodoRequest
import umc.cozymate.data.model.request.UpdateTodoRequest
import umc.cozymate.data.model.response.ruleandrole.CreateTodoResponse
import umc.cozymate.data.model.response.ruleandrole.TodoResponse

interface TodoService {
    @GET("/rooms/{roomId}/todos")
    suspend fun getTodo(
        @Header("Authorization") accessToken: String,
        @Path("roomId") roomId : Int,
        @Query("timePoint") timePoint : String?
    ) : Response<TodoResponse>

    @PATCH("/rooms/{roomId}/todos/{todoId}/state")
    suspend fun updateTodo(
        @Header("Authorization") accessToken: String,
        @Body request: UpdateTodoRequest
    ) : Response<DefaultResponse>

    @POST("/rooms/{roomId}/todos")
    suspend fun createTodo(
        @Header("Authorization") accessToken: String,
        @Path( "roomId") roomId : Int,
        @Body request: CreateTodoRequest
    ): Response<CreateTodoResponse>
}