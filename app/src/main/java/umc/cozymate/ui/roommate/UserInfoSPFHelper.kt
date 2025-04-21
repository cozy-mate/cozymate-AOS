package umc.cozymate.ui.roommate

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import umc.cozymate.ui.roommate.data_class.UserInfo

class UserInfoSPFHelper(context: Context) {

    private val spf: SharedPreferences =
        context.getSharedPreferences("userInfo", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveUserInfo(info: UserInfo) {
        with(spf.edit()) {
            putBoolean("login", info.login)
            putInt("school", info.school)

            putString("dormitoryName", info.dormitoryName)

            putString("name", info.name)
            putString("birth", info.birth)
            putString("admissionYear", info.admissionYear)
            putString("major", info.major)
            putString("numOfRoommate", info.numOfRoommate)
            putString("acceptance", info.dormJoiningStatus)

            putInt("wakeUpTime", info.wakeUpTime)
            putInt("sleepTime", info.sleepTime)
            putInt("lightOffTime", info.turnOffTime)

            putString("smokingState", info.smokingStatus)
//            putString("sleepingHabit", info.sleepingHabit)
            putString("sleepingHabit", gson.toJson(info.sleepingHabits))
            putString("airConditioningIntensity", info.coolingIntensity)
            putString("heatingIntensity", info.heatingIntensity)
            putString("lifePattern", info.lifePattern)
            putString("intimacy", info.intimacy)
            putString("canShare", info.sharingStatus)
            putString("isPlayGame", info.gamingStatus)
            putString("isPhoneCall", info.callingStatus)
            putString("studying", info.studyingStatus)
            putString("intake", info.eatingStatus)
            putString("cleanSensitivity", info.cleannessSensitivity)
            putString("noiseSensitivity", info.noiseSensitivity)
            putString("cleaningFrequency", info.cleaningFrequency)
            putString("drinkingFrequency", info.drinkingFrequency)
//            putString("personality", info.personality)
            putString("personality", gson.toJson(info.personalities))
            putString("mbti", info.mbti)
            putString("selfIntroduction", info.selfIntroduction)

//            commit()
            apply()

            Log.d("SPFHelper", "User Info: $info")
        }
    }

    fun loadUserInfo(): UserInfo {
        val login = spf.getBoolean("login", false) ?: false
        val school = spf.getInt("school", 1) ?: 0
        val dormitoryName = spf.getString("dormitoryName", "") ?: ""
        val name = spf.getString("name", "") ?: ""
        val birth = spf.getString("birth", "") ?: ""
        val admissionYear = spf.getString("admissionYear", "") ?: ""
        val major = spf.getString("major", "") ?: ""
        val numOfRoommate = spf.getString("numOfRoommate", "0") ?: ""
        val acceptance = spf.getString("acceptance", "") ?: ""

        val wakeUpTime = spf.getInt("wakeUpTime", 0) ?: 0
        val sleepTime = spf.getInt("sleepTime", 0)
        val turnOffTime = spf.getInt("turnOffTime", 0)

        val smokingStatus = spf.getString("smokingState", "") ?: ""

        val sleepingHabitJson = spf.getString("sleepingHabit", "[]") ?: "[]"
        val sleepingHabits: List<String> = gson.fromJson(sleepingHabitJson, object : TypeToken<List<String>>() {}.type)

        val coolingIntensity = spf.getString("airConditioningIntensity", "") ?: ""
        val heatingIntensity = spf.getString("heatingIntensity", "") ?: ""

        val lifePattern = spf.getString("lifePattern", "") ?: ""
        val intimacy = spf.getString("intimacy", "") ?: ""
        val sharingStatus = spf.getString("canShare", "") ?: ""
        val gamingStatus = spf.getString("isPlayGame", "") ?: ""
        val callingStatus = spf.getString("isPhoneCall", "") ?: ""
        val studyingStatus= spf.getString("studying", "") ?: ""
        val eatingStatus = spf.getString("intake", "") ?: ""
        val cleannessSensitivity = spf.getString("cleanSensitivity", "") ?: ""
        val noiseSensitivity = spf.getString("noiseSensitivity", "") ?: ""
        val cleaningFrequency = spf.getString("cleaningFrequency", "") ?: ""
        val drinkingFrequency = spf.getString("drinkingFrequency", "") ?: ""

//        val personality = spf.getString("personality", "") ?: ""
        val personalityJson = spf.getString("personality", "[]") ?: "[]"
        val personalities: List<String> = gson.fromJson(personalityJson, object : TypeToken<List<String>>() {}.type)

        val mbti = spf.getString("mbti", "") ?: ""
        val selfIntroduction = spf.getString("selfIntroduction", "") ?: ""

        return UserInfo(
            login,
            school,
            dormitoryName,
            name,
            birth,
            admissionYear,
            major,
            numOfRoommate,
            acceptance,
            wakeUpTime,
            sleepTime,
            turnOffTime,
            smokingStatus,
            sleepingHabits,
            coolingIntensity,
            heatingIntensity,
            lifePattern,
            intimacy,
            sharingStatus,
            gamingStatus,
            callingStatus,
            studyingStatus,
            eatingStatus,
            cleannessSensitivity,
            noiseSensitivity,
            cleaningFrequency,
            drinkingFrequency,
            personalities,
            mbti,
            selfIntroduction
        ).also {
            Log.d("SPFHelper Load", "Loaded UserInfo: $it")
        }
//        Log.d("SPFHelper Load", "Loaded UserInfo: $info")
//        return info
    }
}
