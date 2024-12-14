package umc.cozymate.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import umc.cozymate.data.DefaultResponse
import umc.cozymate.data.model.request.RoleRequest
import umc.cozymate.data.model.response.ruleandrole.RoleResponse

interface RoleService {
    @POST("/role/{roomId}")
    suspend fun createRole(
        @Header("Authorization") accessToken: String,
        @Path("roomId") roomId : Int,
        @Body request : RoleRequest
    ): Response<DefaultResponse>

    @GET("/role/{roomId}")
    suspend fun getRole(
        @Header("Authorization") accessToken: String,
        @Path("roomId") roomId : Int
    ): Response<RoleResponse>

    @DELETE("/role/{roomId}")
    suspend fun deleteRole(
        @Header("Authorization") accessToken: String,
        @Path("roomId") roomId : Int,
        @Query("roleId") roleId : Int
    ): Response<DefaultResponse>

}