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
    val dormJoiningStatus: String,
    val wakeUpTime: Int,
    val sleepingTime: Int,
    val turnOffTime: Int,
    val smokingStatus: String,
    val sleepingHabits: String,
    val coolingIntensity: String,
    val heatingIntensity: String,
    val lifePattern: String,
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
    val personalities: String,
    val mbti: String,
    val options: Map<String, List<String>> ,
): Parcelable