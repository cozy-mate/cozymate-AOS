package umc.cozymate.data.model.request

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class FeedInfoRequest(
    @SerializedName("roomId")
    val roomId : Int,
    @SerializedName("name")
    val name : String,
    @SerializedName("description")
    val description : String
)
