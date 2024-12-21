package umc.cozymate.data.model.response.favorites

data class Result(
    val currentMateNum: Int,
    val equality: String,
    val favoriteId: Int,
    val hashtagList: List<String>,
    val maxMateNum: Int,
    val name: String,
    val preferenceMatchCountList: List<PreferenceMatchCount>,
    val roomId: Int
)