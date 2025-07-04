package umc.cozymate.util

import umc.cozymate.R

data class ChipEventInfo(
    val eventName: String,
    val label: String,
    val action: String = AnalyticsConstants.Action.BUTTON_CLICK,
)

object AnalyticsChipMapper {

    val chipEventMap = mapOf(
        R.id.chip_birth_year to ChipEventInfo(AnalyticsConstants.Event.BUTTON_CLICK_BIRTH, AnalyticsConstants.Label.BIRTH),
        R.id.chip_admission_year to ChipEventInfo(AnalyticsConstants.Event.BUTTON_CLICK_STUDENT_ID, AnalyticsConstants.Label.STUDENT_ID),
        R.id.chip_major to ChipEventInfo(AnalyticsConstants.Event.BUTTON_CLICK_MAJOR, AnalyticsConstants.Label.MAJOR),
        R.id.chip_acceptance to ChipEventInfo(AnalyticsConstants.Event.BUTTON_CLICK_DORM_PASS,  AnalyticsConstants.Label.DORM_PASS),
        R.id.chip_mbti to ChipEventInfo(AnalyticsConstants.Event.BUTTON_CLICK_MBTI,  AnalyticsConstants.Label.MBTI),
        R.id.chip_intake to ChipEventInfo(AnalyticsConstants.Event.BUTTON_CLICK_EAT,  AnalyticsConstants.Label.EAT),
        R.id.chip_wakeup_time to ChipEventInfo(AnalyticsConstants.Event.BUTTON_CLICK_MORNING, AnalyticsConstants.Label.MORNING),
        R.id.chip_sleeping_time to ChipEventInfo(AnalyticsConstants.Event.BUTTON_CLICK_NIGHT, AnalyticsConstants.Label.NIGHT),
        R.id.chip_turn_off_time to ChipEventInfo(AnalyticsConstants.Event.BUTTON_CLICK_LIGHT_OFF,  AnalyticsConstants.Label.LIGHT_OFF),
        R.id.chip_smoking to ChipEventInfo(AnalyticsConstants.Event.BUTTON_CLICK_SMOKE, AnalyticsConstants.Label.SMOKE),
        R.id.chip_sleeping_habit to ChipEventInfo(AnalyticsConstants.Event.BUTTON_CLICK_SLEEP_HABIT, AnalyticsConstants.Label.SLEEP_HABIT),
        R.id.chip_air_conditioning_intensity to ChipEventInfo(AnalyticsConstants.Event.BUTTON_CLICK_AIRCON, AnalyticsConstants.Label.AIRCON),
        R.id.chip_heating_intensity to ChipEventInfo(AnalyticsConstants.Event.BUTTON_CLICK_HEATER, AnalyticsConstants.Label.HEATER),
        R.id.chip_life_pattern to ChipEventInfo(AnalyticsConstants.Event.BUTTON_CLICK_LIFESTYLE, AnalyticsConstants.Label.LIFESTYLE),
        R.id.chip_intimacy to ChipEventInfo(AnalyticsConstants.Event.BUTTON_CLICK_CLOSENESS, AnalyticsConstants.Label.CLOSENESS),
        R.id.chip_can_share to ChipEventInfo(AnalyticsConstants.Event.BUTTON_CLICK_ITEM_SHARING, AnalyticsConstants.Label.ITEM_SHARING),
        R.id.chip_is_play_game to ChipEventInfo(AnalyticsConstants.Event.BUTTON_CLICK_GAME, AnalyticsConstants.Label.GAME),
        R.id.chip_is_phone_call to ChipEventInfo(AnalyticsConstants.Event.BUTTON_CLICK_PHONE_CALL, AnalyticsConstants.Label.PHONE_CALL),
        R.id.chip_studying to ChipEventInfo(AnalyticsConstants.Event.BUTTON_CLICK_STUDY, AnalyticsConstants.Label.STUDY),
        R.id.chip_clean_sensitivity to ChipEventInfo(AnalyticsConstants.Event.BUTTON_CLICK_CLEAN_SENSITIVITY, AnalyticsConstants.Label.CLEAN_SENSITIVITY),
        R.id.chip_cleaning_frequency to ChipEventInfo(AnalyticsConstants.Event.BUTTON_CLICK_CLEAN_FREQ, AnalyticsConstants.Label.CLEAN_FREQ),
        R.id.chip_noise_sensitivity to ChipEventInfo(AnalyticsConstants.Event.BUTTON_CLICK_NOISE, AnalyticsConstants.Label.NOISE),
        R.id.chip_drinking_frequency to ChipEventInfo(AnalyticsConstants.Event.BUTTON_CLICK_DRINKING_FREQ, AnalyticsConstants.Label.DRINKING_FREQ),
        R.id.chip_personality to ChipEventInfo(AnalyticsConstants.Event.BUTTON_CLICK_PERSONALITY, AnalyticsConstants.Label.PERSONALITY)
    )

    val chipTextMap = mapOf(
        "출생년도" to ChipEventInfo(AnalyticsConstants.Event.BUTTON_CLICK_CHIP_BIRTH, AnalyticsConstants.Label.CHIP_BIRTH),
        "학번" to ChipEventInfo(AnalyticsConstants.Event.BUTTON_CLICK_CHIP_STUDENT_ID, AnalyticsConstants.Label.CHIP_STUDENT_ID),
        "학과" to ChipEventInfo(AnalyticsConstants.Event.BUTTON_CLICK_CHIP_MAJOR, AnalyticsConstants.Label.CHIP_MAJOR),
        "합격여부" to ChipEventInfo(AnalyticsConstants.Event.BUTTON_CLICK_CHIP_PASS,  AnalyticsConstants.Label.CHIP_PASS),
        "기상시간" to ChipEventInfo(AnalyticsConstants.Event.BUTTON_CLICK_CHIP_MORNING, AnalyticsConstants.Label.CHIP_MORNING),
        "취침시간" to ChipEventInfo(AnalyticsConstants.Event.BUTTON_CLICK_CHIP_NIGHT, AnalyticsConstants.Label.CHIP_NIGHT),
        "소등시간" to ChipEventInfo(AnalyticsConstants.Event.BUTTON_CLICK_CHIP_LIGHT_OFF,  AnalyticsConstants.Label.CHIP_LIGHT_OFF),
        "흡연여부" to ChipEventInfo(AnalyticsConstants.Event.BUTTON_CLICK_CHIP_SMOKE, AnalyticsConstants.Label.CHIP_SMOKE),
        "잠버릇" to ChipEventInfo(AnalyticsConstants.Event.BUTTON_CLICK_CHIP_SLEEP_HABIT, AnalyticsConstants.Label.CHIP_SLEEP_HABIT),
        "에어컨" to ChipEventInfo(AnalyticsConstants.Event.BUTTON_CLICK_CHIP_AIRCON, AnalyticsConstants.Label.CHIP_AIRCON),
        "히터" to ChipEventInfo(AnalyticsConstants.Event.BUTTON_CLICK_CHIP_HEATER, AnalyticsConstants.Label.CHIP_HEATER),
        "생활패턴" to ChipEventInfo(AnalyticsConstants.Event.BUTTON_CLICK_CHIP_LIFESTYLE, AnalyticsConstants.Label.CHIP_LIFESTYLE),
        "친밀도" to ChipEventInfo(AnalyticsConstants.Event.BUTTON_CLICK_CHIP_CLOSENESS, AnalyticsConstants.Label.CHIP_CLOSENESS),
        "물건공유" to ChipEventInfo(AnalyticsConstants.Event.BUTTON_CLICK_CHIP_ITEM_SHARING, AnalyticsConstants.Label.CHIP_ITEM_SHARING),
        "게임여부" to ChipEventInfo(AnalyticsConstants.Event.BUTTON_CLICK_CHIP_GAME, AnalyticsConstants.Label.CHIP_GAME),
        "전화여부" to ChipEventInfo(AnalyticsConstants.Event.BUTTON_CLICK_CHIP_PHONE_CALL, AnalyticsConstants.Label.CHIP_PHONE_CALL),
        "공부여부" to ChipEventInfo(AnalyticsConstants.Event.BUTTON_CLICK_CHIP_STUDY, AnalyticsConstants.Label.CHIP_STUDY),
        "섭취여부" to ChipEventInfo(AnalyticsConstants.Event.BUTTON_CLICK_CHIP_EAT,  AnalyticsConstants.Label.CHIP_EAT),
        "청결예민도" to ChipEventInfo(AnalyticsConstants.Event.BUTTON_CLICK_CHIP_CLEAN_SENSITIVITY, AnalyticsConstants.Label.CHIP_CLEAN_SENSITIVITY),
        "소음예민도" to ChipEventInfo(AnalyticsConstants.Event.BUTTON_CLICK_CHIP_NOISE, AnalyticsConstants.Label.CHIP_NOISE),
        "청소빈도" to ChipEventInfo(AnalyticsConstants.Event.BUTTON_CLICK_CHIP_CLEAN_FREQ, AnalyticsConstants.Label.CHIP_CLEAN_FREQ),
        "음주빈도" to ChipEventInfo(AnalyticsConstants.Event.BUTTON_CLICK_CHIP_DRINKING_FREQ, AnalyticsConstants.Label.CHIP_DRINKING_FREQ),
        "성격" to ChipEventInfo(AnalyticsConstants.Event.BUTTON_CLICK_CHIP_PERSONALITY, AnalyticsConstants.Label.CHIP_PERSONALITY),
        "MBTI" to ChipEventInfo(AnalyticsConstants.Event.BUTTON_CLICK_CHIP_MBTI,  AnalyticsConstants.Label.CHIP_MBTI)
    )
}