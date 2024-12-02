package umc.cozymate.data.model.response.member


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetUniversityListResponse(
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
        @SerialName("universityList")
        val universityList: List<University>
    ) {
        @Serializable
        data class University(
            @SerialName("id")
            val id: Int,
            @SerialName("name")
            val name: String
        )
    }
}