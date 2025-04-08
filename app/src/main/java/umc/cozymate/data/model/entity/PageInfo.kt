package umc.cozymate.data.model.entity

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class PageInfo<T> (
    @SerializedName("page")
    val page : Int = 0,
    @SerializedName("hasNext")
    val hasNext : Boolean = false,
    @SerializedName("result")
    val result : T
)