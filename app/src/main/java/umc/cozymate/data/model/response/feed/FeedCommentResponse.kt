package umc.cozymate.data.model.response.feed

import kotlinx.serialization.SerialName
import umc.cozymate.data.model.entity.FeedCommentData

data class FeedCommentResponse(
    @SerialName("code")
    val code: String,
    @SerialName("isSuccess")
    val isSuccess: Boolean,
    @SerialName("message")
    val message: String,
    @SerialName("result")
    val result: List<FeedCommentData>
)
