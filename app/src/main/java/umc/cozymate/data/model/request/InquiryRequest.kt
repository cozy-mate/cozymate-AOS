package umc.cozymate.data.model.request

import com.google.gson.annotations.SerializedName

data class InquiryRequest(
    @SerializedName("content")
    val content: String,
    @SerializedName("email")
    val email : String
)
