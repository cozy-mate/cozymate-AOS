package umc.cozymate.data.model.request

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class ImageRequest(
    @SerializedName("multipartFile")
    val multipartFile : String
)
