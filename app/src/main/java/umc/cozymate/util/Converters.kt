package umc.cozymate.data.model.entity

import androidx.room.TypeConverter
import com.google.gson.Gson
import umc.cozymate.data.model.response.room.GetRoomInfoResponse

class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>): String = value.joinToString(",")

    @TypeConverter
    fun toStringList(value: String): List<String> = value.split(",")

    @TypeConverter
    fun fromDifference(value: GetRoomInfoResponse.Result.Difference): String = Gson().toJson(value)

    @TypeConverter
    fun toDifference(value: String): GetRoomInfoResponse.Result.Difference = Gson().fromJson(value, GetRoomInfoResponse.Result.Difference::class.java)
}