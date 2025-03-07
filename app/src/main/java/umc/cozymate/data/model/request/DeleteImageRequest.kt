package umc.cozymate.data.model.request

import com.google.gson.annotations.SerializedName

data class DeleteImageRequest(
    @SerializedName("fileNames")
    val  fileNames : List<String>
)
