package umc.cozymate.data.model.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class MemberStatDetail(
    val admissionYear: String,
    val numOfRoommate: String,
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
