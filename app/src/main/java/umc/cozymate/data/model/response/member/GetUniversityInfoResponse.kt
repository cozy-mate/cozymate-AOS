package umc.cozymate.data.model.response.member


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetUniversityInfoResponse(
    @SerialName("code")
    val code: String,
    @SerialName("isSuccess")
    val isSuccess: Boolean,
    @SerialName("message")
    val message: String,
    @SerialName("result")
    val result: Result
) {
    @Serializable
    data class Result(
        @SerialName("departments")
        val departments: List<String>,
        @SerialName("dormitoryNames")
        val dormitoryNames: List<String>,
        @SerialName("id")
        val id: Int,
        @SerialName("mailPattern")
        val mailPattern: String,
        @SerialName("name")
        val name: String
    )
}