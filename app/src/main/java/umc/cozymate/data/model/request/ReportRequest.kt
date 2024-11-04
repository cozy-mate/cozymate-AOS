package umc.cozymate.data.model.request

import com.google.gson.annotations.SerializedName

data class ReportRequest(
    @SerializedName("reportedMemberId")
    val reportedMemberId : Int,
    @SerializedName("reportSource")
    val reportSource: String,
    @SerializedName("reportReason")
    val reportReason : String,
    @SerializedName("content")
    val content : String?
)
