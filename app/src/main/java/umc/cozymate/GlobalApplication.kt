package umc.cozymate

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // kakao SDK 초기화
        KakaoSdk.init(this, KAKAO_APP_KEY)
    }
}