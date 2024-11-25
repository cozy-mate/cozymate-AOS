package umc.cozymate.data.model.response.favorites

data class GetFavoritesRoomsResponse(
    val code: String,
    val isSuccess: Boolean,
    val message: String,
    val result: List<Result>
)