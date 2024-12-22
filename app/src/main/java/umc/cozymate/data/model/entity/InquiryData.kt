package umc.cozymate.data.model.entity

import com.google.gson.annotations.SerializedName

data class InquiryData(
    @SerializedName("inquiryId")
    val inquiryId: Int,
    @SerializedName ("persona")
    val persona: Int,
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("datetime")
    val datetime: String,
    @SerializedName("status")
    val status: String
)
