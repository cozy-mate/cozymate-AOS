package umc.cozymate.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import umc.cozymate.data.DefaultResponse
import umc.cozymate.data.model.request.RoleRequest
import umc.cozymate.data.model.response.ruleandrole.CreateResponse
import umc.cozymate.data.model.response.ruleandrole.RoleResponse

interface RoleService {
    @POST("/rooms/{roomId}/roles")
    suspend fun createRole(
        @Header("Authorization") accessToken: String,
        @Path("roomId") roomId : Int,
        @Body request : RoleRequest
    ):  Response<CreateResponse>

    @GET("/rooms/{roomId}/roles")
    suspend fun getRole(
        @Header("Authorization") accessToken: String,
        @Path("roomId") roomId : Int
    ): Response<RoleResponse>

    @DELETE("/rooms/{roomId}/roles/{roleId}")
    suspend fun deleteRole(
        @Header("Authorization") accessToken: String,
        @Path("roomId") roomId : Int,
        @Path("roleId") roleId : Int
    ): Response<DefaultResponse>

    @PUT("/rooms/{roomId}/roles/{roleId}")
    suspend fun editRole(
        @Header("Authorization") accessToken: String,
        @Path("roomId") roomId : Int,
        @Path("roleId") roleId : Int,
        @Body request: RoleRequest
    ): Response<DefaultResponse>

}