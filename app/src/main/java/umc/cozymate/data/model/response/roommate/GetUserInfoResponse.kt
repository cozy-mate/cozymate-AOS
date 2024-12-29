package umc.cozymate.data.model.response.roommate

data class GetUserInfoResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: Result
) {
    data class Result(
        val memberDetail: MemberDetail,
        val memberStatDetail: MemberStatDetail
    ) {
        data class MemberDetail(
            val memberId: Int,
            val nickname: String,
            val gender: String,
            val birthday: String,
            val universityName: String,
            val universityId: Int,
            val majorName: String,
            val persona: Int
        )

        data class MemberStatDetail(
            val admissionYear: String,
            val numOfRoommate: Int,
            val dormitoryName: String,
            val acceptance: String,
            val wakeUpMeridian: String,
            val wakeUpTime: Int,
            val sleepingMeridian: String,
            val sleepingTime: Int,
            val turnOffMeridian: String,
            val turnOfTime: Int,
            val smoking: String,
            val sleepingHabit: List<String>,
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
            val personality: List<String>,
            val mbti: String,
            val selfIntroduction: String
            )
    }
}
