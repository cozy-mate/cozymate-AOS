package umc.cozymate.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import umc.cozymate.data.model.response.favorites.SendFavoritesResponse
import umc.cozymate.data.model.response.favorites.GetFavoritesMembersResponse
import umc.cozymate.data.model.response.favorites.GetFavoritesRoomsResponse

interface FavoritesService {

    @POST("/favorites/rooms/{roomId}")
    suspend fun sendFavoritesRooms(
        @Header("Authorization") accessToken: String,
        @Path("roomId") roomId: Int
    ): Response<SendFavoritesResponse>

    @POST("/favorites/members/{memberId}")
    suspend fun sendFavoritesMember(
        @Header("Authorization") accessToken: String,
        @Path("memberId") memberId: Int
    ): Response<SendFavoritesResponse>

    @GET("/favorites/rooms")
    suspend fun getFavoritesRooms(
        @Header("Authorization") accessToken: String,
    ): Response<GetFavoritesRoomsResponse>

    @GET("/favorites/members")
    suspend fun getFavoritesMembers(
        @Header("Authorization") accessToken: String
    ) : Response<GetFavoritesMembersResponse>
}