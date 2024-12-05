package umc.cozymate.data.model.request

import com.google.gson.annotations.SerializedName

data class CreatePublicRoomRequest(
    @SerializedName("name")
    val name: String,
    @SerializedName("persona")
    val persona: Int,
    @SerializedName("maxMateNum")
    val maxMateNum: Int,
    @SerializedName("hashtagList")
    val hashtagList: List<String>,
)