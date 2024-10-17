package umc.cozymate.data.model.request

data class UserInfoRequest(
    val universityId: Int,
    val admissionYear: String,
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
    val canShare: Boolean,
    val isPlayGame: Boolean,
    val isPhoneCall: Boolean,
    val studying: String,
    val intake: String,
    val cleanSensitivity: Int,
    val noiseSensitivity: Int,
    val cleaningFrequency: String,
    val drinkingFrequency: String,
    val personality: String,
    val mbti: String,
    val options: Map<String, List<String>> // 이 부분이 다양한 값을 담을 수 있게 해줍니다.

)