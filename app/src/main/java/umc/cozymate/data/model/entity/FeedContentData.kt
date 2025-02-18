package umc.cozymate.data.model.entity

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class FeedContentData(
    @SerializedName("id")
    val postId: Int,
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
    @SerializedName("imageList")
    val imageList : List<String>,
    @SerializedName("commentCount")
    val commentCount : Int,
    @SerializedName("commentList")
    val commentList : List<FeedCommentData> = emptyList()
)
