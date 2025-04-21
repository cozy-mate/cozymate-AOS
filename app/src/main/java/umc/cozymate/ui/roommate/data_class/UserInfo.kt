package umc.cozymate.ui.roommate.data_class

import umc.cozymate.data.model.request.UserInfoRequest

data class UserInfo(

    val login: Boolean = false,
    val school: Int = 1,
    val dormitoryName : String = "",

    val name: String = "",
    val birth: String = "",
    val admissionYear: String = "",
    val major: String = "",
    val numOfRoommate: String = "0",
    val dormJoiningStatus: String = "",

    val wakeUpTime: Int = 0,
    val sleepTime: Int = 0,
    val turnOffTime: Int = 0,

    val smokingStatus: String = "",
    val sleepingHabits: List<String> = emptyList(),

    val coolingIntensity: String = "",
    val heatingIntensity: String = "",

    val lifePattern: String = "",
    val intimacy: String = "",
    val sharingStatus: String = "",
    val gamingStatus: String = "",
    val callingStatus: String = "",
    val studyingStatus: String = "",
    val eatingStatus: String = "",
    val cleannessSensitivity: String = "",
    val noiseSensitivity: String = "",
    val cleaningFrequency: String = "",
    val drinkingFrequency: String = "",
    val personalities: List<String> = emptyList(),
    val mbti: String = "",
    val selfIntroduction: String = ""
) {
    fun toRequest(): UserInfoRequest {
        return UserInfoRequest(
              // 예시로 변환, 실제 구현에 맞게 변경 필요
            dormName = dormitoryName,
            admissionYear = admissionYear,
            numOfRoommate = numOfRoommate,
            dormJoiningStatus = dormJoiningStatus,
            wakeUpTime = wakeUpTime,
            sleepingTime = sleepTime,
            turnOffTime = turnOffTime,
            smokingStatus = smokingStatus,
            sleepingHabits = sleepingHabits,
            coolingIntensity = coolingIntensity,
            heatingIntensity = heatingIntensity,
            lifePattern = lifePattern,
            intimacy = intimacy,
            sharingStatus = sharingStatus,
            gamingStatus = gamingStatus,
            callingStatus = callingStatus,
            studyingStatus = studyingStatus,
            eatingStatus = eatingStatus,
            cleannessSensitivity = cleannessSensitivity,
            noiseSensitivity = noiseSensitivity,
            cleaningFrequency = cleaningFrequency,
            drinkingFrequency = drinkingFrequency,
            personalities = personalities,
            mbti = mbti,
            selfIntroduction = selfIntroduction
        )
    }
}