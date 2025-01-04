package umc.cozymate.data.repository.repository

import retrofit2.Response
import umc.cozymate.data.model.response.favorites.GetFavoritesMembersResponse
import umc.cozymate.data.model.response.favorites.GetFavoritesRoomsResponse
import umc.cozymate.data.model.response.favorites.SendFavoritesResponse

interface FavoritesRepository {
    suspend fun sendFavoritesRooms(accessToken: String, roomId: Int) : Response<SendFavoritesResponse>

    suspend fun sendFavoritesMember(accessToken: String, memberId: Int) : Response<SendFavoritesResponse>

    suspend fun getFavoritesRooms(accessToken: String) : Response<GetFavoritesRoomsResponse>

    suspend fun getFavoritesMembers(accessToken: String) : Response<GetFavoritesMembersResponse>

    suspend fun deleteFavoritesRoomMember(accessToken: String, id: Int): Response<SendFavoritesResponse>
}