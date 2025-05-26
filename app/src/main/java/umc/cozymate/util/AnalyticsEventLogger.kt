package umc.cozymate.util

import android.os.Bundle
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics

object AnalyticsEventLogger {
    private val analytics by lazy {
        Firebase.analytics
    }

    fun logEvent(
        eventName: String,
        category: String? = null,
        action: String? = null,
        label: String? = null,
        duration: Long? = null
    ) {
        val params = Bundle().apply {
            category?.let { putString(AnalyticsConstants.Keys.CATEGORY, it) }
            action?.let { putString(AnalyticsConstants.Keys.ACTION, it) }
            label?.let { putString(AnalyticsConstants.Keys.LABEL, it) }
            duration?.let { putLong(AnalyticsConstants.Keys.DURATION, it) }
        }

        analytics.logEvent(eventName, params)
    }
}