package umc.cozymate.data.model.response.roommate

import umc.cozymate.data.domain.OtherUserInfo

// 도메인 모델 구조
data class OtherUsers(
    val info: Info,
    val detail: Detail
){
    fun toModel(info: Info, detail: Detail) = OtherUserInfo(
        info,
        detail
    )
}
//
//data class InfoDomain(
//    val memberId: Int,
//    val memberName: String,
//    val memberNickName: String,
//    val memberAge: Int,
//    val memberPersona: Int,
//    val numOfRoommate: Int,
//    val equality: Int
//)
//
//data class DetailDomain(
//    val universityId: Int,
//    val admissionYear: Int,
//    val major: String,
//    val numOfRoommate: Int,
//    val acceptance: String,
//    val wakeUpMeridian: String,
//    val wakeUpTime: Int,
//    val sleepingMeridian: String,
//    val sleepingTime: Int,
//    val turnOffMeridian: String,
//    val turnOffTime: Int,
//    val smokingState: String,
//    val sleepingHabit: String,
//    val airConditioningIntensity: Int,
//    val heatingIntensity: Int,
//    val lifePattern: String,
//    val intimacy: String,
//    val canShare: Boolean,
//    val isPlayGame: Boolean,
//    val isPhoneCall: Boolean,
//    val studying: String,
//    val intake: String,
//    val cleanSensitivity: Int,
//    val noiseSensitivity: Int,
//    val cleaningFrequency: String,
//    val personality: String,
//    val mbti: String,
//    val options: OptionsDomain
//)
//
//data class OptionsDomain(
//    val mustKeep: List<String>,
//    val absolutelyNot: List<String>,
//    val canAdjust: List<String>
//)
