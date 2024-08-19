package umc.cozymate.data.model.entity

import com.google.gson.annotations.SerializedName

class RoomInfo (
    @SerializedName("managerName")
    val managerName: String,
    @SerializedName("maxMateNum")
    val maxMateNum: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("roomId")
    val roomId: Int
)