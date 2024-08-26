package umc.cozymate.ui.roommate.data_class

import umc.cozymate.data.model.request.UserInfoRequest

data class UserInfo(

    val login: Boolean = false,
    val school: Int = 1,

    val name: String = "",
    val birth: String = "",
    val admissionYear: String = "",
    val major: String = "",
    val numOfRoommate: Int = 0,
    val acceptance: String = "",

    val wakeAmPm: String = "",
    val wakeUpTime: Int = 0,
    val sleepAmPm: String = "",
    val sleepTime: Int = 0,
    val lightOffAmPm: String = "",
    val lightOffTime: Int = 0,

    val smokingState: String = "",
    val sleepingHabit: String = "",

    val airConditioningIntensity: String = "",
    val heatingIntensity: String = "",
    // api 상에선 Int로 넘겨줘야 함

    val lifePattern: String = "",
    val intimacy: String = "",
    val canShare: String = "",
    // api 상에선 Boolean으로 넘겨야 함
    val isPlayGame: String = "",
    val isPhoneCall: String = "",
    val studying: String = "",
    val intake: String = "",
    val cleanSensitivity: String = "",
    val noiseSensitivity: String = "",
    val cleaningFrequency: String = "",
    val personality: String = "",
    val mbti: String = ""
) {
    fun toRequest(): UserInfoRequest {
        return UserInfoRequest(
            universityId = school,  // 예시로 변환, 실제 구현에 맞게 변경 필요
            admissionYear = admissionYear,
            major = major,
            numOfRoommate = numOfRoommate,
            acceptance = acceptance,
            wakeUpMeridian = wakeAmPm,
            wakeUpTime = wakeUpTime,
            sleepingMeridian = sleepAmPm,
            sleepingTime = sleepTime,
            turnOffMeridian = lightOffAmPm,
            turnOffTime = lightOffTime,
            smokingState = smokingState,
            sleepingHabit = sleepingHabit,
            airConditioningIntensity = convertACToInt(airConditioningIntensity),
            heatingIntensity = convertHeaterToInt(heatingIntensity),
            lifePattern = lifePattern,
            intimacy = intimacy,
            canShare = convertCanShareToInt(canShare),
            isPlayGame = convertIsPlayGameToInt(isPlayGame),
            isPhoneCall = convertIsPhoneCallToInt(isPhoneCall),
            studying = studying,
            intake = intake,
            cleanSensitivity = convertCleanToInt(cleanSensitivity),
            noiseSensitivity = convertNoiseToInt(noiseSensitivity),
            cleaningFrequency = cleaningFrequency,
            personality = personality,
            mbti = mbti,
            options = mapOf(
                "additionalProp1" to listOf("String1", "String2"),
                "additionalProp2" to listOf("String3"),
                "additionalProp3" to listOf("String4", "String5")
            )
        )
    }
}

private fun convertCanShareToInt(canShare: String): Boolean {
    return when (canShare) {
        "O" -> true
        "X" -> false
        else -> false
    }
}

private fun convertIsPlayGameToInt(canShare: String): Boolean {
    return when (canShare) {
        "O" -> true
        "X" -> false
        else -> false
    }
}
private fun convertIsPhoneCallToInt(canShare: String): Boolean {
    return when (canShare) {
        "O" -> true
        "X" -> false
        else -> false
    }
}
private fun convertACToInt(canShare: String): Int {
    return when (canShare) {
        "세게 틀어요" -> 3
        "적당하게 틀어요" -> 2
        "약하게 틀어요" -> 1
        "아예 틀지 않아요" -> 0
        else -> 1
    }
}
private fun convertHeaterToInt(canShare: String): Int {
    return when (canShare) {
        "세게 틀어요" -> 3
        "적당하게 틀어요" -> 2
        "약하게 틀어요" -> 1
        "아예 틀지 않아요" -> 0
        else -> 1
    }
}
private fun convertCleanToInt(canShare: String): Int {
    return when (canShare) {
        "매우 예민해요" -> 5
        "예민해요" -> 4
        "보통이에요" -> 3
        "예민하지 않아요" -> 2
        "매우 예민하지 않아요" -> 1
        else -> 1
    }
}
private fun convertNoiseToInt(canShare: String): Int {
    return when (canShare) {
        "매우 예민해요" -> 5
        "예민해요" -> 4
        "보통이에요" -> 3
        "예민하지 않아요" -> 2
        "매우 예민하지 않아요" -> 1
        else -> 1
    }
}