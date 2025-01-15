package umc.cozymate.util

fun PreferenceNameToId(name: String): String {
    return when (name) {
        "출생년도" -> "birthYear"
        "학번" -> "admissionYear"
        "학과" -> "majorName"
        "합격여부" -> "acceptance"
        "기상시간" -> "wakeUpTime"
        "취침시간" -> "sleepingTime"
        "소등시간" -> "turnOffTime"
        "흡연여부" -> "smoking"
        "잠버릇" -> "sleepingHabit"
        "에어컨" -> "airConditioningIntensity"
        "히터" -> "heatingIntensity"
        "생활패턴" -> "lifePattern"
        "친밀도" -> "intimacy"
        "물건공유" -> "canShare"
        "게임여부" -> "isPlayGame"
        "전화여부" -> "isPhoneCall"
        "공부여부" -> "studying"
        "섭취여부" -> "intake"
        "청결예민도" -> "cleanSensitivity"
        "소음예민도" -> "noiseSensitivity"
        "청소빈도" -> "cleaningFrequency"
        "음주빈도" -> "drinkingFrequency"
        "성격" -> "personality"
        "MBTI" -> "mbti"
        else -> throw IllegalArgumentException("Invalid preference name: $name")
    }
}

