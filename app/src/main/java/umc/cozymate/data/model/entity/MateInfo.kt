package umc.cozymate.data.model.entity

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class MateInfo(
    @SerializedName("mateId")
    val mateId : Int,
    @SerializedName("nickname")
    val nickname: String
): Parcelable
