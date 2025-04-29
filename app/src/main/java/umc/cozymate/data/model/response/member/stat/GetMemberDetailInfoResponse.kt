package umc.cozymate.data.model.response.member.stat

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class GetMemberDetailInfoResponse(
    @SerialName("code")
    val code: String,
    @SerialName("isSuccess")
    val isSuccess: Boolean,
    @SerialName("message")
    val message: String,
    @SerialName("result")
    val result: Result
) : Parcelable {
    @Parcelize
    @Serializable
    data class Result(
        val memberDetail: MemberDetail,
        val memberStatDetail: MemberStatDetail,
        val equality: Int,
        val roomId: Int,
        val hasRequestedRoomEntry: Boolean,
        val favoriteId: Int
    ) : Parcelable {
        @Parcelize
        @Serializable
        data class MemberDetail(
            val memberId: Int,
            val nickname: String,
            val gender: String,
            val birthday: String,
            val universityName: String,
            val universityId: Int,
            val majorName: String,
            val persona: Int
        ) : Parcelable
        @Parcelize
        @Serializable
        data class MemberStatDetail(
            val admissionYear: String,
            val numOfRoommate: Int,
            val dormName: String,
            val dormJoiningStatus: String,
            val wakeUpTime: Int,
            val sleepingTime: Int,
            val turnOffTime: Int,
            val smokingStatus: String,
            val sleepingHabits: List<String>,
            val coolingIntensity: String,
            val heatingIntensity: String,
            val lifePattern:String,
            val intimacy: String,
            val sharingStatus: String,
            val gamingStatus: String,
            val callingStatus: String,
            val studyingStatus: String,
            val eatingStatus: String,
            val cleannessSensitivity: String,
            val noiseSensitivity: String,
            val cleaningFrequency: String,
            val drinkingFrequency: String,
            val personalities: List<String>,
            val mbti: String,
            val selfIntroduction: String?
        ) : Parcelable
    }
}