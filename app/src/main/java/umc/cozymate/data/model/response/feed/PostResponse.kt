package umc.cozymate.data.model.response.feed

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import umc.cozymate.data.model.entity.FeedContentData
@Serializable
data class PostResponse(
    @SerialName("code")
    val code: String,
    @SerialName("isSuccess")
    val isSuccess: Boolean,
    @SerialName("message")
    val message: String,
    @SerialName("result")
    val result: FeedContentData
)
