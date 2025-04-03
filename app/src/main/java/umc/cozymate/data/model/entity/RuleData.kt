package umc.cozymate.data.model.entity

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class RuleData(
    @SerializedName("ruleId")
    val ruleId : Int =0,
    @SerializedName("content")
    val content: String = "",
    @SerializedName("memo")
    val memo : String = ""
): Parcelable
