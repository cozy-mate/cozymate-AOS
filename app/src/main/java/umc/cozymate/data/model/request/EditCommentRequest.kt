package umc.cozymate.data.model.request

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class EditCommentRequest(
    @SerializedName("roomId")
    val roomId : Int,
    @SerializedName("postId")
    val postId : Int,
    @SerializedName("commentId")
    val commentId : Int = 0,
    @SerializedName("content")
    val content : String,
)
