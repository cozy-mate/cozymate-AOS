package umc.cozymate.data.model.response.roommate

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Detail(
    val birthYear: Int,
    val universityId: Int,
    val admissionYear: Int,
    val major: String,
    val numOfRoommate: Int,
    val acceptance: String,
    val wakeUpMeridian: String,
    val wakeUpTime: Int,
    val sleepingMeridian: String,
    val sleepingTime: Int,
    val turnOffMeridian: String,
    val turnOffTime: Int,
    val smokingState: String,
    val sleepingHabit: String,
    val airConditioningIntensity: Int,
    val heatingIntensity: Int,
    val lifePattern: String,
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
    val personality: String,
    val mbti: String,
    val options: Map<String, List<String>> ,
): Parcelable