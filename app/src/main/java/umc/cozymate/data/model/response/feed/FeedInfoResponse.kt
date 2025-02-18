package umc.cozymate.data.model.response.feed

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FeedInfoResponse(
    @SerialName("code")
    val code: String,
    @SerialName("isSuccess")
    val isSuccess: Boolean,
    @SerialName("message")
    val message: String,
    @SerialName("result")
    val result: FeedInfo
){
    @Serializable
    class FeedInfo(
        @SerializedName("name")
        val name : String,
        @SerializedName("description")
        val description : String
    )
}
