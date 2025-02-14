package umc.cozymate.data.model.request

import com.google.gson.annotations.SerializedName

data class ReportRequest(
    @SerializedName("memberId")
    val reportedMemberId : Int,
    @SerializedName("source")
    val reportSource: String,
    @SerializedName("reason")
    val reportReason : String,
    @SerializedName("content")
    val content : String
)
