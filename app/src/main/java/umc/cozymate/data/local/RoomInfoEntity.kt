package umc.cozymate.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import umc.cozymate.data.model.response.room.GetRoomInfoResponse

@Entity(tableName = "room_info")
data class RoomInfoEntity(
    @PrimaryKey val roomId: Int,
    val name: String,
    val inviteCode: String,
    val persona: Int,
    val managerMemberId: Int,
    val managerNickname: String,
    val isRoomManager: Boolean,
    val favoriteId: Int,
    val maxMateNum: Int,
    val arrivalMateNum: Int,
    val dormitoryName: String,
    val roomType: String,
    val equality: Int,
    val hashtagList: List<String>,
    val difference: GetRoomInfoResponse.Result.Difference
)