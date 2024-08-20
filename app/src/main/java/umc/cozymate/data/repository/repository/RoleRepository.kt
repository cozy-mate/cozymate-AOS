package umc.cozymate.data.repository.repository

import retrofit2.Response
import umc.cozymate.data.DefaultResponse
import umc.cozymate.data.model.request.RoleRequest
import umc.cozymate.data.model.response.RoleResponse

interface RoleRepository {
    suspend fun createRole( accessToken: String, roomId : Int, request : RoleRequest): Response<DefaultResponse>

    suspend fun getRole(accessToken: String, roomId : Int ): Response<RoleResponse>

    suspend fun deleteRole(accessToken: String, roomId : Int, roleId : Int ): Response<DefaultResponse>
}