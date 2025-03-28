package umc.cozymate.data.model.entity

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

@Serializable
data class TodoData(
    @SerializedName("memberDetail")
    val memberDetail: MemberDetailInfo,
    @SerializedName("todoList")
    var todoList: List<TodoItem>
){
    @Parcelize
    @Serializable
    data class TodoItem(
        @SerializedName("todoId")
        val todoId: Int = 0,

        @SerializedName("content")
        val content: String = "",

        @SerializedName("completed")
        var completed: Boolean = false,

        @SerializedName("todoType")
        val todoType : String = "self",

        @SerializedName("mateIdList")
        val mateIdList: List<Int> = emptyList()
    ):  Parcelable
}
