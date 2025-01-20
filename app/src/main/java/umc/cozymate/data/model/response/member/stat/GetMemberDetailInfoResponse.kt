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
            val dormitoryName: String,
            val acceptance: String,
            val wakeUpMeridian: String,
            val wakeUpTime: Int,
            val sleepingMeridian: String,
            val sleepingTime: Int,
            val turnOffMeridian: String,
            val turnOffTime: Int,
            val smoking: String,
            val sleepingHabit: List<String>,
            val airConditioningIntensity: Int,
            val heatingIntensity: Int,
            val lifePattern:String,
            val intimacy: String,
            val canShare: String,
            val isPlayGame: String,
            val isPhoneCall: String,
            val studying: String,
            val intake: String,
            val cleanSensitivity: Int,
            val noiseSensitivity: Int,
            val cleaningFrequency: String,
            val drinkingFrequency: String,
            val personality: List<String>,
            val mbti: String,
            val selfIntroduction: String?
        ) : Parcelable
    }
}