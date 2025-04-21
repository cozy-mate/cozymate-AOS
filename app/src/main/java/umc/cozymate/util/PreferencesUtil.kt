package umc.cozymate.util

import android.content.SharedPreferences
import android.content.Context
import android.util.Log

object PreferencesUtil {
    const val PREFS_NAME = "app_prefs"

    // 저장할 때 사용할 키들을 상수로 정의
    const val KEY_USER_MEMBER_ID = "user_member_id"
    const val KEY_USER_NICKNAME = "user_nickname"
    const val KEY_USER_BIRTHDAY = "user_birthday"
    const val KEY_USER_UNIVERSITY_NAME = "user_university_name"
    const val KEY_USER_UNIVERSITY_ID = "user_university_id"
    const val KEY_USER_MAJOR_NAME = "user_major_name"
    const val KEY_USER_PERSONA = "user_persona"
    const val KEY_USER_ADMISSION_YEAR = "user_admissionYear"
    const val KEY_USER_NUM_OF_ROOMMATE = "user_numOfRoommate"
    const val KEY_USER_DORM_JOINING_STATUS = "user_dormJoiningStatus"
    const val KEY_USER_WAKE_UP_TIME = "user_wakeUpTime"
    const val KEY_USER_SLEEPING_TIME = "user_sleepingTime"
    const val KEY_USER_TURN_OFF_TIME = "user_turnOffTime"
    const val KEY_USER_SMOKING_STATUS = "user_smokingStatus"
    const val KEY_USER_SLEEPING_HABITS = "user_sleepingHabits"
    const val KEY_USER_COOLING_INTENSITY = "user_coolingIntensity"
    const val KEY_USER_HEATING_INTENSITY = "user_heatingIntensity"
    const val KEY_USER_LIFE_PATTERN = "user_lifePattern"
    const val KEY_USER_INTIMACY = "user_intimacy"
    const val KEY_USER_SHARING_STATUS = "user_sharingStatus"
    const val KEY_USER_STUDYING_STATUS = "user_studyingStatus"
    const val KEY_USER_EATING_STATUS = "user_eatingStatus"
    const val KEY_USER_GAMING_STATUS = "user_gamingStatus"
    const val KEY_USER_CALLING_STATUS = "user_callingStatus"
    const val KEY_USER_CLEANNESS_SENSITIVITY = "user_cleannessSensitivity"
    const val KEY_USER_NOISE_SENSITIVITY = "user_noiseSensitivity"
    const val KEY_USER_CLEANING_FREQUENCY = "user_cleaningFrequency"
    const val KEY_USER_DRINKING_FREQUENCY = "user_drinkingFrequency"
    const val KEY_USER_PERSONALITIES = "user_personalities"
    const val KEY_USER_MBTI = "user_mbti"
    const val KEY_USER_SELF_INTRODUCTION = "user_selfIntroduction"
    const val KEY_IS_LIFESTYLE_EXIST = "is_lifestyle_exist"
    const val KEY_ROOM_ID = "room_id"
    const val KEY_ROOM_NAME = "room_name"

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
        Log.d(context.toString(), "SharedPreferences 데이터가 삭제되었습니다.")
    }
}