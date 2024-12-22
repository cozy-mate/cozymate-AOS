package umc.cozymate.data.api

import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import umc.cozymate.data.model.response.favorites.GetFavoritesMembersResponse
import umc.cozymate.data.model.response.favorites.GetFavoritesRoomsResponse
import umc.cozymate.data.model.response.favorites.SendFavoritesResponse

interface FavoritesService {

    // 방 찜하기
    @POST("/favorites/rooms/{roomId}")
    suspend fun sendFavoritesRooms(
        @Header("Authorization") accessToken: String,
        @Path("roomId") roomId: Int
    ): Response<SendFavoritesResponse>

    // 사용자 찜하기
    @POST("/favorites/members/{memberId}")
    suspend fun sendFavoritesMember(
        @Header("Authorization") accessToken: String,
        @Path("memberId") memberId: Int
    ): Response<SendFavoritesResponse>

    // 찜한 방 목록 조회
    @GET("/favorites/rooms")
    suspend fun getFavoritesRooms(
        @Header("Authorization") accessToken: String,
    ): Response<GetFavoritesRoomsResponse>

    // 찜한 사용자 조회
    @GET("/favorites/members")
    suspend fun getFavoritesMembers(
        @Header("Authorization") accessToken: String
    ) : Response<GetFavoritesMembersResponse>

    // 사용자/방 찜 삭제
    @DELETE("/favorites/{favoriteId}")
    suspend fun deleteFavoriteId(
        @Header("Authorization") accessToken: String,
        @Path("favoriteId") favoriteId: Int
    ) : Response<SendFavoritesResponse>
}