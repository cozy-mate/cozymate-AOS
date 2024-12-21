package umc.cozymate.data.model.request

import com.google.gson.annotations.SerializedName

data class RoleRequest(
    @SerializedName("mateIdList")
    val mateIdList: List<Int>,
    @SerializedName("title")
    val title : String,
    @SerializedName("repeatDayList")
    val repeatDayList: List<String>,
)
