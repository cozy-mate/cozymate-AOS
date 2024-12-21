package umc.cozymate.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RoomInfoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoomInfo(roomInfo: RoomInfoEntity)

    @Query("SELECT * FROM room_info WHERE roomId = :roomId")
    suspend fun getRoomInfoById(roomId: Int): RoomInfoEntity?

    @Query("DELETE FROM room_info WHERE roomId = :roomId")
    suspend fun deleteRoomInfo(roomId: Int)
}