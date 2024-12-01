package umc.cozymate.data.api

import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import umc.cozymate.data.DefaultResponse
import umc.cozymate.data.model.response.block.GetBlockMemberResponse

interface BlockService {

    // 멤버 차단
    @POST("/block/members/{memberId}")
    suspend fun sendBlockMember(
        @Header("Authorization") accessToken: String,
        @Path("memberId") memberId: Int
    ) : Response<DefaultResponse>

    // 멤버 차단 목록 조회
    @GET("/block/members")
    suspend fun getBlockMember(
        @Header("Authorization") accessToken: String,
    ) : Response<GetBlockMemberResponse>

    // 멤버 차단 해제
    @DELETE("/block/members/{memberId}")
    suspend fun deleteBlockMember(
        @Header("Authorization") accessToken: String,
        @Path("memberId") memberId: Int
    ) : Response<DefaultResponse>
}