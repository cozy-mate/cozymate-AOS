package umc.cozymate.data.model.response.ruleandrole

import com.google.gson.annotations.SerializedName
import umc.cozymate.data.model.entity.RoleData

data class RoleResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: Result
){
    data class Result(
        @SerializedName("myRoleList")
        val myRoleList: RoleData,

        @SerializedName("otherRoleList")
        val otherRoleList: Map<String, RoleData>
    )
}
