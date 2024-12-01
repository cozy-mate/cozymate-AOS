package umc.cozymate.data.repository.repositoryImpl

import retrofit2.Response
import umc.cozymate.data.DefaultResponse
import umc.cozymate.data.api.BlockService
import umc.cozymate.data.model.response.block.GetBlockMemberResponse
import umc.cozymate.data.repository.repository.BlockRepository
import javax.inject.Inject

class BlockRepositoryImpl @Inject constructor(
    private val api: BlockService
) : BlockRepository {

    override suspend fun sendBlockMember(
        accessToken: String,
        memberId: Int
    ): Response<DefaultResponse> {
        return api.sendBlockMember(accessToken, memberId)
    }

    override suspend fun getBlockMember(accessToken: String): Response<GetBlockMemberResponse> {
        return api.getBlockMember(accessToken)
    }

    override suspend fun deleteBlockMember(accessToken: String, memberId: Int): Response<DefaultResponse> {
        return api.deleteBlockMember(accessToken, memberId)
    }
}
