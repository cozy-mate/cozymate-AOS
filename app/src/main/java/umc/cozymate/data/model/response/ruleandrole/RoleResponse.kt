package umc.cozymate.data.model.response.ruleandrole

import umc.cozymate.data.model.entity.RoleData

data class RoleResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: List<RoleData>
)
//{
//    data class Result(
//        @SerializedName("roleList")
//        val roleList : List<RoleData>
//    )
//
//}
