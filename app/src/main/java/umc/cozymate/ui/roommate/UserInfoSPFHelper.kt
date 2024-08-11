package umc.cozymate.ui.roommate

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import umc.cozymate.ui.roommate.data_class.UserInfo

class UserInfoSPFHelper(context: Context) {

    private val spf: SharedPreferences = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE)

    fun saveUserInfo(info: UserInfo) {
        with(spf.edit()) {
            putBoolean("login", info.login)
            putString("school", info.school)

            putString("name", info.name)
            putString("birth", info.birth)
            putString("admissionYear", info.admissionYear)
            putString("major", info.major)
            putInt("numOfRoommate", info.numOfRoommate)
            putString("acceptance", info.acceptance)

            putString("wakeAmPm", info.wakeAmPm)
            putInt("wakeUpTime", info.wakeUpTime)
            putString("sleepAmPm", info.sleepAmPm)


            commit()
            Log.d("SPFHelper", "User Info: $info")
        }
    }

    fun loadUserInfo() : UserInfo{
        val login = spf.getBoolean("login",false) ?: false
        val school = spf.getString("school", "") ?: ""
        val name = spf.getString("name", "") ?: ""
        val birth = spf.getString("birth", "") ?: ""
        val admissionYear = spf.getString("admissionYear", "") ?: ""
        val major = spf.getString("major", "") ?: ""
        val numOnRoommate = spf.getInt("numOnRoommate", 0) ?: 0
        val acceptance = spf.getString("acceptance", "") ?: ""

        val wakeAmPm = spf.getString("wakeAmPm", "") ?: ""
        val wakeUpTime = spf.getInt("wakeUpTime", 0) ?: 0
        val sleepAmPm = spf.getString("sleepAmPm", "") ?: ""


        val info = UserInfo(
            login,
            school,
            name,
            birth,
            admissionYear,
            major,
            numOnRoommate,
            acceptance,

            wakeAmPm,
            wakeUpTime,
            sleepAmPm
        )
        Log.d("SPFHelper Load", "Loaded UserInfo: $info")
        return info
    }
}