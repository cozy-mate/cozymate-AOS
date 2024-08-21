package umc.cozymate.data.repository.repositoryImpl

import retrofit2.Response
import umc.cozymate.data.DefaultResponse
import umc.cozymate.data.api.RoleService
import umc.cozymate.data.model.request.RoleRequest
import umc.cozymate.data.model.response.RoleResponse
import umc.cozymate.data.repository.repository.RoleRepository
import javax.inject.Inject

class RoleRepositoryImpl @Inject constructor(
    private val api : RoleService
) : RoleRepository {

    override suspend fun createRole(
        accessToken: String,
        roomId: Int,
        request: RoleRequest
    ): Response<DefaultResponse> {
        return api.createRole(accessToken, roomId, request)
    }

    override suspend fun getRole(
        accessToken: String,
        roomId: Int
    ): Response<RoleResponse> {
        return api.getRole(accessToken, roomId)
    }

    override suspend fun deleteRole(
        accessToken: String,
        roomId: Int,
        roleId: Int
    ): Response<DefaultResponse> {
        return api.deleteRole(accessToken, roomId, roleId)
    }

}