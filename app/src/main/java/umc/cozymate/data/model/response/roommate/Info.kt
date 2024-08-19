package umc.cozymate.data.model.response.roommate

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Info(
    val memberId: Int,
    val memberName: String,
    val memberNickName: String,
    val memberAge: Int,
    val memberPersona: Int,
    val numOfRoommate: Int,
    val equality: Int,
): Parcelable