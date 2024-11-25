package umc.cozymate.data.model.response.ruleandrole

import com.google.gson.annotations.SerializedName

data class CreateResponse (
    @SerializedName("isSuccess")
    val isSuccess: Boolean,

    @SerializedName("code")
    val code: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("result")
    val result: Result
){
    data class Result(
        val Id : Int
    )
}