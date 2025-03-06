package umc.cozymate.data.model.response.feed

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class ImageResponse(
    @SerializedName("imgUrlList")
    val imgList : List<String>
)
