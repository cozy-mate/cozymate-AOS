package umc.cozymate.data.domain

import umc.cozymate.R

enum class Preference(
    val index: Int,
    val pref: String,
    val displayName: String,
    val subText: String,
    val blueDrawable: Int,
    val redDrawable: Int,
    val grayDrawable: Int,
) {
    BIRTH_YEAR(1, "birthYear", "출생년도", "년", R.drawable.ic_birth_year_blue, R.drawable.ic_birth_year_red, R.drawable.ic_birth_year_gray),
    ADMISSION_YEAR(2, "admissionYear", "학번", "학번",  R.drawable.ic_admission_year_blue, R.drawable.ic_admission_year_red, R.drawable.ic_admission_year_gray),
    MAJOR(3, "학과", "major", "학과", R.drawable.ic_major_blue, R.drawable.ic_major_red, R.drawable.ic_major_gray),
    ACCEPTANCE(4, "acceptance", "합격여부", "", R.drawable.ic_acceptance_blue, R.drawable.ic_acceptance_red, R.drawable.ic_acceptance_gray),
    WAKE_UP_TIME(5, "wakeUpTime", "기상시간", ":00", R.drawable.ic_wake_up_time_blue, R.drawable.ic_wake_up_time_red, R.drawable.ic_wake_up_time_gray),
    SLEEPING_TIME(6, "sleepingTime", "취침시간", ":00", R.drawable.ic_sleeping_time_blue, R.drawable.ic_sleeping_time_red, R.drawable.ic_sleeping_time_gray),
    TURN_OFF_TIME(7, "turnOffTime", "소등시간", ":00", R.drawable.ic_turn_off_time_blue, R.drawable.ic_turn_off_time_red, R.drawable.ic_turn_off_time_gray),
    SMOKING(8, "smoking", "흡연여부", "", R.drawable.ic_smoking_blue, R.drawable.ic_smoking_red, R.drawable.ic_smoking_gray),
    SLEEPING_HABIT(9, "sleepingHabit", "잠버릇", "", R.drawable.ic_sleeping_habit_blue, R.drawable.ic_sleeping_habit_red, R.drawable.ic_sleeping_habit_gray),
    AIR_CONDITIONING_INTENSITY(10, "airConditioningIntensity", "에어컨", "", R.drawable.ic_air_conditioning_intensity_blue, R.drawable.ic_air_conditioning_intensity_red, R.drawable.ic_air_conditioning_intensity_gray),
    HEATING_INTENSITY(11, "heatingIntensity", "히터", "", R.drawable.ic_heating_intensity_blue, R.drawable.ic_heater_intensity_red, R.drawable.ic_heating_intensity_gray),
    LIFE_PATTERN(12, "lifePattern", "생활패턴", "", R.drawable.ic_life_pattern_blue, R.drawable.ic_life_pattern_red, R.drawable.ic_life_pattern_gray),
    INTIMACY(13, "intimacy", "친밀도", "", R.drawable.ic_intimacy_blue, R.drawable.ic_intimacy_red, R.drawable.ic_intimacy_gray),
    CAN_SHARE(14, "canShare", "물건공유", "", R.drawable.ic_can_share_blue, R.drawable.ic_can_share_red, R.drawable.ic_can_share_gray),
    IS_PLAY_GAME(15, "isPlayGame", "게임여부", "", R.drawable.ic_is_play_game_blue, R.drawable.ic_is_play_game_red, R.drawable.ic_is_play_game_gray),
    IS_PHONE_CALL(16, "isPhoneCall", "전화여부", "", R.drawable.ic_is_phone_call_blue, R.drawable.ic_is_phone_call_red, R.drawable.ic_is_phone_call_gray),
    STUDYING(17, "studying", "공부여부", "", R.drawable.ic_studying_blue, R.drawable.ic_studying_red, R.drawable.ic_studying_gray),
    INTAKE(18, "intake", "섭취여부", "", R.drawable.ic_intake_blue, R.drawable.ic_intake_red, R.drawable.ic_intake_gray),
    CLEAN_SENSITIVITY(19, "cleaningSensitivity", "청결예민도", "", R.drawable.ic_clean_sensitivity_blue, R.drawable.ic_clean_sensitivity_red, R.drawable.ic_clean_sensitivity_gray),
    NOISE_SENSITIVITY(20, "noiseSensitivity", "소음예민도", "", R.drawable.ic_noise_sensitivity_blue, R.drawable.ic_noise_sensitivity_red, R.drawable.ic_noise_sensitivity_gray),
    CLEANING_FREQUENCY(21, "cleaningFrequency", "청소빈도", "", R.drawable.ic_cleaning_frequency_blue, R.drawable.ic_cleaning_frequency_red, R.drawable.ic_cleaning_frequency_gray),
    DRINKING_FREQUENCY(22, "drinkingFrequency", "음주빈도", "", R.drawable.ic_drinking_frequency_blue, R.drawable.ic_drinking_frequency_red, R.drawable.ic_drinking_frequency_gray),
    PERSONALITY(23, "personality", "성격", "", R.drawable.ic_personality_blue, R.drawable.ic_personality_red, R.drawable.ic_personality_gray),
    MBTI(24, "mbti", "MBTI", "", R.drawable.ic_mbti_blue, R.drawable.ic_mbti_red, R.drawable.ic_mbti_gray)
}