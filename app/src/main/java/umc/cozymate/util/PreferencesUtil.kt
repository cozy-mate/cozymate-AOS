package umc.cozymate.util

import android.content.SharedPreferences
import android.content.Context
object PreferencesUtil {
    const val PREFS_NAME = "app_prefs"

    // 저장할 때 사용할 키들을 상수로 정의
    const val KEY_USER_NICKNAME = "user_nickname"
    const val KEY_USER_BIRTHDAY = "user_birthday"
    const val KEY_USER_UNIVERSITY_NAME = "user_university_name"
    const val KEY_USER_MAJOR_NAME = "user_major_name"
    const val KEY_USER_PERSONA = "user_persona"
    const val KEY_USER_ADMISSION_YEAR = "user_admissionYear"
    const val KEY_USER_NUM_OF_ROOMMATE = "user_numOfRoommate"
    const val KEY_USER_ACCEPTANCE = "user_acceptance"
    const val KEY_USER_WAKE_UP_MERIDIAN = "user_wakeUpMeridian"
    const val KEY_USER_WAKE_UP_TIME = "user_wakeUpTime"
    const val KEY_USER_SLEEPING_MERIDIAN = "user_sleepingMeridian"
    const val KEY_USER_SLEEPING_TIME = "user_sleepingTime"
    const val KEY_USER_TURN_OFF_MERIDIAN = "user_turnOffMeridian"
    const val KEY_USER_TURN_OFF_TIME = "user_turnOffTime"
    const val KEY_USER_SMOKING = "user_smoking"
    const val KEY_USER_SLEEPING_HABIT = "user_sleepingHabit"
    const val KEY_USER_AIR_CONDITIONING_INTENSITY = "user_airConditioningIntensity"
    const val KEY_USER_HEATING_INTENSITY = "user_heatingIntensity"
    const val KEY_USER_LIFE_PATTERN = "user_lifePattern"
    const val KEY_USER_INTIMACY = "user_intimacy"
    const val KEY_USER_CAN_SHARE = "user_canShare"
    const val KEY_USER_STUDYING = "user_studying"
    const val KEY_USER_INTAKE = "user_intake"
    const val KEY_USER_IS_PLAY_GAME = "user_isPlayGame"
    const val KEY_USER_IS_PHONE_CALL = "user_isPhoneCall"
    const val KEY_USER_CLEAN_SENSITIVITY = "user_cleanSensitivity"
    const val KEY_USER_NOISE_SENSITIVITY = "user_noiseSensitivity"
    const val KEY_USER_CLEANING_FREQUENCY = "user_cleaningFrequency"
    const val KEY_USER_DRINKING_FREQUENCY = "user_drinkingFrequency"
    const val KEY_USER_PERSONALITY = "user_personality"
    const val KEY_USER_MBTI = "user_mbti"
    const val KEY_USER_SELF_INTRODUCTION = "user_selfIntroduction"
    const val KEY_IS_LIFESTYLE_EXIST = "is_lifestyle_exist"

    // SharedPreferences 인스턴스 반환
    private fun getPreferences(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // 문자열 저장 함수
    fun putString(context: Context, key: String, value: String) {
        getPreferences(context).edit().putString(key, value).apply()
    }

    // 문자열 불러오기 함수
    fun getString(context: Context, key: String, default: String = ""): String? =
        getPreferences(context).getString(key, default)

    // 정수 저장 함수
    fun putInt(context: Context, key: String, value: Int) {
        getPreferences(context).edit().putInt(key, value).apply()
    }

    // 정수 불러오기 함수
    fun getInt(context: Context, key: String, default: Int = 0): Int =
        getPreferences(context).getInt(key, default)

    // 불린값 저장 함수
    fun putBoolean(context: Context, key: String, value: Boolean) {
        getPreferences(context).edit().putBoolean(key, value).apply()
    }

    // 불린값 불러오기 함수
    fun getBoolean(context: Context, key: String, default: Boolean = false): Boolean =
        getPreferences(context).getBoolean(key, default)

    fun remove(context: Context, key: String) {
        getPreferences(context).edit().remove(key).apply()
    }

    // 전체 데이터 초기화 함수
    fun clear(context: Context) {
        getPreferences(context).edit().clear().apply()
    }
}