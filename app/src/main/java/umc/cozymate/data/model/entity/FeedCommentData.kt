package umc.cozymate.data.model.entity

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class FeedCommentData(
    @SerializedName("id")
    val commentId: Int,
    @SerializedName("writerId")
    val userId : Int,
    @SerializedName("content")
    val content : String,
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("persona")
    val persona: Int,
    @SerializedName("createdAt")
    val time : String,
)
