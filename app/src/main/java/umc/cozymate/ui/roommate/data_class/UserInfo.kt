package umc.cozymate.ui.roommate.data_class

data class UserInfo(

    val login: Boolean = false,
    val school: String = "",

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

)
