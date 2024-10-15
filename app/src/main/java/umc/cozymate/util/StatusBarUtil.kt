package umc.cozymate.util

import android.app.Activity
import android.graphics.Color
import android.view.View
import android.view.Window

object StatusBarUtil {
    // 배경이 밝은지 어두운지 판단하는 함수
    private fun isColorLight(color: Int): Boolean {
        val darkness = 1 - (0.299* Color.red(color) + 0.587*Color.green(color) + 0.114*Color.blue(color)) / 255
        return darkness < 0.5 // 0.5보다 작으면 밝은 색
    }

    // 상태바 텍스트 색상 설정하는 함수
    private fun setStatusBarTextColor(window: Window, isBackgroundLight: Boolean) {
        if (isBackgroundLight) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            window.decorView.systemUiVisibility = 0
        }
    }

    // 상태바 배경색과 텍스트 색상을 동적으로 설정하는 함수
    fun updateStatusBarColor(activity: Activity, color: Int) {
        val window = activity.window
        window.statusBarColor = color
        val isBackgroundLight = isColorLight(color)
        setStatusBarTextColor(window, isBackgroundLight)
    }
}