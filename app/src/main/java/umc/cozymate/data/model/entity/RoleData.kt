package umc.cozymate.data.model.entity

import com.google.gson.annotations.SerializedName

data class RoleData(
    @SerializedName("persona")
    val persona : Int,
    @SerializedName("mateRoleList")
    val mateRoleList : List<RoleItem>
){
    data class RoleItem(
        @SerializedName("id")
        val id : Int,
        @SerializedName("content")
        val content: String,
        @SerializedName("repeatDayList")
        val repeatDayList : List<String>,
        @SerializedName("allDays")
        val allDays : Boolean
    )
}
