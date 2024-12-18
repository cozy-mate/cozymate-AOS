package umc.cozymate.data.model.response

import com.google.gson.annotations.SerializedName
import umc.cozymate.data.model.entity.InquiryData

data class InquiryResponse(
    @SerializedName("isSuccess")
    val isSuccess: Boolean,

    @SerializedName("code")
    val code: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("result")
    val result: List<InquiryData>
)

