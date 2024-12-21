package umc.cozymate.data.model.request

import com.google.gson.annotations.SerializedName
import umc.cozymate.data.model.entity.MateInfo

data class RoleRequest(
    @SerializedName("mateIdNameList")
    val mateIdList: List<MateInfo>,
    @SerializedName("content")
    val content : String,
    @SerializedName("repeatDayList")
    val repeatDayList: List<String>,
)
