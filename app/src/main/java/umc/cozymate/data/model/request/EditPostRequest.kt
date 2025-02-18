package umc.cozymate.data.model.request

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class EditPostRequest(
    @SerializedName("roomId")
    val roomId : Int,
    @SerializedName("postId")
    val postId : Int = 0,
    @SerializedName("content")
    val content : String,
    @SerializedName("imageList")
    val imageList : List<String>
)
