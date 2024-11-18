package umc.cozymate.data.model.request

import com.google.gson.annotations.SerializedName
import umc.cozymate.data.model.entity.RoleData

data class RoleRequest(
    @SerializedName("mateIdNameList")
    val mateIdList: List<RoleData.mateInfo>,
    @SerializedName("content")
    val content : String,
    @SerializedName("repeatDayList")
    val repeatDayList: List<String>,
)
