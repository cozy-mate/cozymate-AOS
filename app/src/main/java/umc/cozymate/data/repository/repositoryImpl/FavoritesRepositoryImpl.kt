package umc.cozymate.data.repository.repositoryImpl

import retrofit2.Response
import umc.cozymate.data.api.FavoritesService
import umc.cozymate.data.model.response.favorites.GetFavoritesMembersResponse
import umc.cozymate.data.model.response.favorites.GetFavoritesRoomsResponse
import umc.cozymate.data.model.response.favorites.SendFavoritesResponse
import umc.cozymate.data.repository.repository.FavoritesRepository
import javax.inject.Inject

class FavoritesRepositoryImpl @Inject constructor(
    private val api: FavoritesService
) : FavoritesRepository{

    override suspend fun sendFavoritesRooms(
        accessToken: String,
        roomId: Int
    ): Response<SendFavoritesResponse> {
        return api.sendFavoritesRooms(accessToken, roomId)
    }

    override suspend fun sendFavoritesMember(
        accessToken: String,
        memberId: Int
    ): Response<SendFavoritesResponse> {
        return api.sendFavoritesMember(accessToken, memberId)
    }

    override suspend fun getFavoritesRooms(accessToken: String): Response<GetFavoritesRoomsResponse> {
        return api.getFavoritesRooms(accessToken)
    }

    override suspend fun getFavoritesMembers(accessToken: String): Response<GetFavoritesMembersResponse> {
        return api.getFavoritesMembers(accessToken)
    }
}