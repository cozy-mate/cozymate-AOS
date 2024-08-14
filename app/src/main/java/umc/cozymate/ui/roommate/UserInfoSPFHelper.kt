package umc.cozymate.ui.roommate

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import umc.cozymate.ui.roommate.data_class.UserInfo

//class UserInfoSPFHelper(context: Context) {
//
//    private val spf: SharedPreferences =
//        context.getSharedPreferences("userInfo", Context.MODE_PRIVATE)
//
//    fun saveUserInfo(info: UserInfo) {
//        with(spf.edit()) {
//            putBoolean("login", info.login)
//            putString("school", info.school)
//
//            putString("name", info.name)
//            putString("birth", info.birth)
//            putString("admissionYear", info.admissionYear)
//            putString("major", info.major)
//            putInt("numOfRoommate", info.numOfRoommate)
//            putString("acceptance", info.acceptance)
//
//            putString("wakeAmPm", info.wakeAmPm)
//            putInt("wakeUpTime", info.wakeUpTime)
//            putString("sleepAmPm", info.sleepAmPm)
//            putInt("sleepTime", info.sleepTime)
//            putString("lightOffAmPm", info.lightOffAmPm)
//            putInt("lightOffTime", info.lightOffTime)
//
//            putString("smokingState", info.smokingState)
//            putString("sleepingHabit", info.sleepingHabit)
//            putString("airConditioningIntensity", info.airConditioningIntensity)
//            putString("heatingIntensity", info.heatingIntensity)
//            putString("lifePattern", info.lifePattern)
//            putString("intimacy", info.intimacy)
//            putString("canShare", info.canShare)
//            putString("isPlayGame", info.isPlayGame)
//            putString("isPhoneCall", info.isPhoneCall)
//            putString("sleepAmPm", info.studying)
//            putString("intake", info.intake)
//            putString("cleanSensitivity", info.cleanSensitivity)
//            putString("noiseSensitivity", info.noiseSensitivity)
//            putString("cleaningFrequency", info.cleaningFrequency)
//            putString("personality", info.personality)
//            putString("mbti", info.mbti)
//
//
//
//            commit()
//            Log.d("SPFHelper", "User Info: $info")
//        }
//    }
//
//    fun loadUserInfo(): UserInfo {
//        val login = spf.getBoolean("login", false) ?: false
//        val school = spf.getString("school", "") ?: ""
//        val name = spf.getString("name", "") ?: ""
//        val birth = spf.getString("birth", "") ?: ""
//        val admissionYear = spf.getString("admissionYear", "") ?: ""
//        val major = spf.getString("major", "") ?: ""
//        val numOnRoommate = spf.getInt("numOnRoommate", 0) ?: 0
//        val acceptance = spf.getString("acceptance", "") ?: ""
//
//        val wakeAmPm = spf.getString("wakeAmPm", "") ?: ""
//        val wakeUpTime = spf.getInt("wakeUpTime", 0) ?: 0
//        val sleepAmPm = spf.getString("sleepAmPm", "") ?: ""
//        val sleepTime = spf.getInt("sleepTime", 0)
//        val lightOffAmPm = spf.getString("lightOffAmPm", "") ?: ""
//        val lightOffTime = spf.getInt("lightOffTime", 0)
//
//        val smokingState = spf.getString("smokingState", "") ?: ""
//        val sleepingHabit = spf.getString("sleepingHabit", "") ?: ""
//
//        val airConditioningIntensity = spf.getString("airConditioningIntensity", "") ?: ""
//        val heatingIntensity = spf.getString("heatingIntensity", "") ?: ""
//
//        val lifePattern = spf.getString("lifePattern", "") ?: ""
//        val intimacy = spf.getString("intimacy", "") ?: ""
//        val canShare = spf.getString("canShare", "") ?: ""
//        val isPlayGame = spf.getString("isPlayGame", "") ?: ""
//        val isPhoneCall = spf.getString("isPhoneCall", "") ?: ""
//        val studying = spf.getString("studying", "") ?: ""
//        val intake = spf.getString("intake", "") ?: ""
//        val cleanSensitivity = spf.getString("cleanSensitivity", "") ?: ""
//        val noiseSensitivity = spf.getString("noiseSensitivity", "") ?: ""
//        val cleaningFrequency = spf.getString("cleaningFrequency", "") ?: ""
//        val personality = spf.getString("personality", "") ?: ""
//        val mbti = spf.getString("mbti", "") ?: ""
//
//
//        val info = UserInfo(
//            login,
//            school,
//
//            name,
//            birth,
//            admissionYear,
//            major,
//            numOnRoommate,
//            acceptance,
//
//            wakeAmPm,
//            wakeUpTime,
//            sleepAmPm,
//            sleepTime,
//            lightOffAmPm,
//            lightOffTime,
//
//            smokingState,
//            sleepingHabit,
//            airConditioningIntensity,
//            heatingIntensity,
//            lifePattern,
//            intimacy,
//            canShare,
//            isPlayGame,
//            isPhoneCall,
//            studying,
//            intake,
//            cleanSensitivity,
//            noiseSensitivity,
//            cleaningFrequency,
//            personality,
//            mbti
//        )
//        Log.d("SPFHelper Load", "Loaded UserInfo: $info")
//        return info
//    }
//}
class UserInfoSPFHelper(context: Context) {

    private val spf: SharedPreferences =
        context.getSharedPreferences("userInfo", Context.MODE_PRIVATE)

    fun saveUserInfo(info: UserInfo) {
        with(spf.edit()) {
            putBoolean("login", info.login)
            putInt("school", info.school)

            putString("name", info.name)
            putString("birth", info.birth)
            putString("admissionYear", info.admissionYear)
            putString("major", info.major)
            putInt("numOfRoommate", info.numOfRoommate)
            putString("acceptance", info.acceptance)

            putString("wakeAmPm", info.wakeAmPm)
            putInt("wakeUpTime", info.wakeUpTime)
            putString("sleepAmPm", info.sleepAmPm)
            putInt("sleepTime", info.sleepTime)
            putString("lightOffAmPm", info.lightOffAmPm)
            putInt("lightOffTime", info.lightOffTime)

            putString("smokingState", info.smokingState)
            putString("sleepingHabit", info.sleepingHabit)
            putString("airConditioningIntensity", info.airConditioningIntensity)
            putString("heatingIntensity", info.heatingIntensity)
            putString("lifePattern", info.lifePattern)
            putString("intimacy", info.intimacy)
            putString("canShare", info.canShare)
            putString("isPlayGame", info.isPlayGame)
            putString("isPhoneCall", info.isPhoneCall)
            putString("studying", info.studying)
            putString("intake", info.intake)
            putString("cleanSensitivity", info.cleanSensitivity)
            putString("noiseSensitivity", info.noiseSensitivity)
            putString("cleaningFrequency", info.cleaningFrequency)
            putString("personality", info.personality)
            putString("mbti", info.mbti)

            commit()
            Log.d("SPFHelper", "User Info: $info")
        }
    }

    fun loadUserInfo(): UserInfo {
        val login = spf.getBoolean("login", false) ?: false
        val school = spf.getInt("school", 1) ?: 0
        val name = spf.getString("name", "") ?: ""
        val birth = spf.getString("birth", "") ?: ""
        val admissionYear = spf.getString("admissionYear", "") ?: ""
        val major = spf.getString("major", "") ?: ""
        val numOfRoommate = spf.getInt("numOfRoommate", 0) ?: 0
        val acceptance = spf.getString("acceptance", "") ?: ""

        val wakeAmPm = spf.getString("wakeAmPm", "") ?: ""
        val wakeUpTime = spf.getInt("wakeUpTime", 0) ?: 0
        val sleepAmPm = spf.getString("sleepAmPm", "") ?: ""
        val sleepTime = spf.getInt("sleepTime", 0)
        val lightOffAmPm = spf.getString("lightOffAmPm", "") ?: ""
        val lightOffTime = spf.getInt("lightOffTime", 0)

        val smokingState = spf.getString("smokingState", "") ?: ""
        val sleepingHabit = spf.getString("sleepingHabit", "") ?: ""
        val airConditioningIntensity = spf.getString("airConditioningIntensity", "") ?: ""
        val heatingIntensity = spf.getString("heatingIntensity", "") ?: ""
        val lifePattern = spf.getString("lifePattern", "") ?: ""
        val intimacy = spf.getString("intimacy", "") ?: ""
        val canShare = spf.getString("canShare", "") ?: ""
        val isPlayGame = spf.getString("isPlayGame", "") ?: ""
        val isPhoneCall = spf.getString("isPhoneCall", "") ?: ""
        val studying = spf.getString("studying", "") ?: ""
        val intake = spf.getString("intake", "") ?: ""
        val cleanSensitivity = spf.getString("cleanSensitivity", "") ?: ""
        val noiseSensitivity = spf.getString("noiseSensitivity", "") ?: ""
        val cleaningFrequency = spf.getString("cleaningFrequency", "") ?: ""
        val personality = spf.getString("personality", "") ?: ""
        val mbti = spf.getString("mbti", "") ?: ""

        val info = UserInfo(
            login,
            school,
            name,
            birth,
            admissionYear,
            major,
            numOfRoommate,
            acceptance,
            wakeAmPm,
            wakeUpTime,
            sleepAmPm,
            sleepTime,
            lightOffAmPm,
            lightOffTime,
            smokingState,
            sleepingHabit,
            airConditioningIntensity,
            heatingIntensity,
            lifePattern,
            intimacy,
            canShare,
            isPlayGame,
            isPhoneCall,
            studying,
            intake,
            cleanSensitivity,
            noiseSensitivity,
            cleaningFrequency,
            personality,
            mbti
        )
        Log.d("SPFHelper Load", "Loaded UserInfo: $info")
        return info
    }
}
