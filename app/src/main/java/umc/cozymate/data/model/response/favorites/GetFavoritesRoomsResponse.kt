package umc.cozymate.data.model.response.favorites

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class GetFavoritesRoomsResponse(
    @SerialName("code")
    val code: String,
    @SerialName("isSuccess")
    val isSuccess: Boolean,
    @SerialName("message")
    val message: String,
    @SerialName("result")
    val result: Result
) {
    @Serializable
    data class Result(
        @SerialName("hasNext")
        val hasNext: Boolean,
        @SerialName("page")
        val page: Int,
        @SerialName("result")
        val result: List<FavoriteRoomItem>
    ) {
        @Serializable
        data class FavoriteRoomItem(
            @SerialName("currentMateNum")
            val currentMateNum: Int,
            @SerialName("equality")
            val equality: Int,
            @SerialName("roomFavoriteId")
            val favoriteId: Int,
            @SerialName("hashtags")
            val hashtagList: List<String>?,
            @SerialName("maxMateNum")
            val maxMateNum: Int,
            @SerialName("name")
            val name: String,
            @SerialName("preferenceMatchCountList")
            val preferenceMatchCountList: List<PreferenceMatchCount>,
            @SerialName("roomId")
            val roomId: Int
        ) {
            @Serializable
            data class PreferenceMatchCount(
                @SerialName("count")
                val count: Int,
                @SerialName("preferenceName")
                val preferenceName: String
            )
        }
    }
}