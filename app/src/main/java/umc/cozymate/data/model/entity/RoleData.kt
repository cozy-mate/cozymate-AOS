package umc.cozymate.data.model.entity

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class RoleData(
    @SerializedName("roleId")
    val roleId : Int =0,
    @SerializedName("mateList")
    val mateList : List<MateInfo> = emptyList(),
    @SerializedName("content")
    val content: String = "",
    @SerializedName("repeatDayList")
    val repeatDayList : List<String> = emptyList(),
    @SerializedName("allDays")
    val allDays : Boolean = false
): Parcelable
