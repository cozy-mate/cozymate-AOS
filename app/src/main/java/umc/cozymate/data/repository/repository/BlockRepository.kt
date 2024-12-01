package umc.cozymate.data.repository.repository

import retrofit2.Response
import umc.cozymate.data.DefaultResponse
import umc.cozymate.data.model.response.block.GetBlockMemberResponse

interface BlockRepository {

    suspend fun sendBlockMember(accessToken: String, memberId: Int): Response<DefaultResponse>

    suspend fun getBlockMember(accessToken: String): Response<GetBlockMemberResponse>

    suspend fun deleteBlockMember(accessToken: String, memberId: Int): Response<DefaultResponse>
}