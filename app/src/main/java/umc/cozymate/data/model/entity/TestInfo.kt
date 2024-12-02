package umc.cozymate.data.model.entity

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName

data class TestInfo(

    @SerialName("memberId")
    val memberId: Int = 0,
    @SerialName("nickname")
    val nickname: String = "",
    @SerialName("gender")
    val gender: String= "",
    @SerialName("birthday")
    val birthday: String= "",
    @SerializedName("universityName")
    val universityName : String= "",
    @SerializedName("majorName")
    val majorName : String= "",
    @SerialName("persona")
    val persona: Int = 0
)
