package umc.cozymate.data.model.response.favorites

data class GetFavoritesMembersResponse(
    val code: String,
    val isSuccess: Boolean,
    val message: String,
    val result: List<ResultX>
)