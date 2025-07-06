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
            val numOfRoommate: String,
            val dormitoryName: String,
            val dormJoiningStatus: String,
            val wakeUpTime: Int,
            val sleepingTime: Int,
            val turnOffTime: Int,
            val smokingStatus: String,
            val sleepingHabits: List<String>,
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
            val personalities: List<String>,
            val mbti: String,
            val selfIntroduction: String
            )
    }
}
