package umc.cozymate.data.model.entity

import com.google.gson.annotations.SerializedName

data class RoleData(
    @SerializedName("roleId")
    val roleId : Int,
    @SerializedName("mateList")
    val mateList : List<MateInfo>,
    @SerializedName("content")
    val content: String,
    @SerializedName("repeatDayList")
    val repeatDayList : List<String>,
    @SerializedName("allDays")
    val allDays : Boolean
)
