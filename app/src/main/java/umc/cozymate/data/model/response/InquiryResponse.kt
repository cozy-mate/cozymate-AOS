package umc.cozymate.data.model.response

import com.google.gson.annotations.SerializedName

data class InquiryResponse(
    @SerializedName("isSuccess")
    val isSuccess: Boolean,

    @SerializedName("code")
    val code: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("result")
    val result: List<InquiryData>
){
    data class InquiryData(
        @SerializedName("inquiryId")
        val inquiryId : Int,
        @SerializedName("persona")
        val persona : Int,
        @SerializedName("nickname")
        val nickname : String,
        @SerializedName("content")
        val content : String,
        @SerializedName("datetime")
        val datetime : String,
        @SerializedName("status")
        val status : String
    )
}

