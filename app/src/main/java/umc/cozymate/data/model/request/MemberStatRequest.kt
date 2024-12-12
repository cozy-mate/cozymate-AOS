package umc.cozymate.data.model.request


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MemberStatRequest(
    @SerialName("additionalProp1")
    val additionalProp1: List<AdditionalProp1>,
    @SerialName("additionalProp2")
    val additionalProp2: List<AdditionalProp2>,
    @SerialName("additionalProp3")
    val additionalProp3: List<AdditionalProp3>
) {
    @Serializable
    class AdditionalProp1

    @Serializable
    class AdditionalProp2

    @Serializable
    class AdditionalProp3
}