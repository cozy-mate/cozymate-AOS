package umc.cozymate.data.model.entity

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class ChatRoomData(
    @SerializedName("persona")
    val persona : Int,
    @SerializedName("nickname")
    val nickname : String,
    @SerializedName("lastContent")
    val lastContent : String,
    @SerializedName("chatRoomId")
    val chatRoomId : Int,
    @SerializedName("memberId")
    val memberId : Int,
    @SerializedName("hasNewChat")
    val hasNewChat : Boolean
) : Parcelable
