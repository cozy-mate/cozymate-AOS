package umc.cozymate.data.model.entity

import com.google.gson.annotations.SerializedName

data class RuleData(
    @SerializedName("id")
    val id : Int,
    @SerializedName("content")
    val content: String,
    @SerializedName("memo")
    val memo : String
)
