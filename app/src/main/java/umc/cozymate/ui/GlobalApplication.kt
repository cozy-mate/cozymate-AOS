package umc.cozymate.ui

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // kakao SDK 초기화
        KakaoSdk.init(this, "a51292da8bc69b2160b0bb97ec00b2ad")
    }
}