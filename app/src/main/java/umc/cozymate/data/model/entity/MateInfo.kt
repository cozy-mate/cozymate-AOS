package umc.cozymate.data.model.entity

import com.google.gson.annotations.SerializedName

data class MateInfo(
    @SerializedName("mateId")
    val mateId : Int,
    @SerializedName("nickname")
    val nickname: String
)
