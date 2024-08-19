package umc.cozymate.data.model.response.roommate

data class OtherUserInfoResponse(
    val page: Int,
    val hasNext: Boolean,
    val result: List<OtherUsers>
)

//data class Info(
//    val memberId: Int,
//    val memberName: String,
//    val memberNickName: String,
//    val memberAge: Int,
//    val memberPersona: Int,
//    val numOfRoommate: Int,
//    val equality: Int
//)
//
//data class Detail(
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
//    val options: Options
//)
//
//data class Options(
//    val mustKeep: List<String>,
//    val absolutelyNot: List<String>,
//    val canAdjust: List<String>
//)
//
//fun Info.toModel(): InfoDomain {
//    return InfoDomain(
//        memberId = this.memberId,
//        memberName = this.memberName,
//        memberNickName = this.memberNickName,
//        memberAge = this.memberAge,
//        memberPersona = this.memberPersona,
//        numOfRoommate = this.numOfRoommate,
//        equality = this.equality
//    )
//}
//
//fun Detail.toModel(): DetailDomain {
//    return DetailDomain(
//        universityId = this.universityId,
//        admissionYear = this.admissionYear,
//        major = this.major,
//        numOfRoommate = this.numOfRoommate,
//        acceptance = this.acceptance,
//        wakeUpMeridian = this.wakeUpMeridian,
//        wakeUpTime = this.wakeUpTime,
//        sleepingMeridian = this.sleepingMeridian,
//        sleepingTime = this.sleepingTime,
//        turnOffMeridian = this.turnOffMeridian,
//        turnOffTime = this.turnOffTime,
//        smokingState = this.smokingState,
//        sleepingHabit = this.sleepingHabit,
//        airConditioningIntensity = this.airConditioningIntensity,
//        heatingIntensity = this.heatingIntensity,
//        lifePattern = this.lifePattern,
//        intimacy = this.intimacy,
//        canShare = this.canShare,
//        isPlayGame = this.isPlayGame,
//        isPhoneCall = this.isPhoneCall,
//        studying = this.studying,
//        intake = this.intake,
//        cleanSensitivity = this.cleanSensitivity,
//        noiseSensitivity = this.noiseSensitivity,
//        cleaningFrequency = this.cleaningFrequency,
//        personality = this.personality,
//        mbti = this.mbti,
//        options = this.options.toModel()
//    )
//}
//
//fun Options.toModel(): OptionsDomain {
//    return OptionsDomain(
//        mustKeep = this.mustKeep,
//        absolutelyNot = this.absolutelyNot,
//        canAdjust = this.canAdjust
//    )
//}