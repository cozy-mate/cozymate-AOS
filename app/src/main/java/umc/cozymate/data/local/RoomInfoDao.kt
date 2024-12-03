package umc.cozymate.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RoomInfoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoomInfo(roomInfo: RoomInfoEntity)

    @Query("SELECT * FROM room_info WHERE roomId = :roomId")
    suspend fun getRoomInfoById(roomId: Int): RoomInfoEntity?

    @Delete
    suspend fun deleteRoomInfo(roomInfo: RoomInfoEntity)
}
