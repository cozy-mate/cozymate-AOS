package umc.cozymate.firebase

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import umc.cozymate.R
import umc.cozymate.ui.MainActivity

class FCMService : FirebaseMessagingService() {
    private val TAG = "FirebaseService"
    private val channelId: String = "Cozymate"
    private val channelName = "cozymate Channel"
    private val channelDescription = "cozymate를 위한 채널"

    private val serviceScope = CoroutineScope(Dispatchers.IO)

    object PushUtils {
        private var mWakeLock: PowerManager.WakeLock? = null

        @SuppressLint("InvalidWakeLockTag")
        fun acquireWakeLock(context: Context) {
            val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            mWakeLock = pm.newWakeLock(
                PowerManager.FULL_WAKE_LOCK or
                        PowerManager.ACQUIRE_CAUSES_WAKEUP or
                        PowerManager.ON_AFTER_RELEASE, "WAKEUP"
            )
            mWakeLock!!.acquire(3000)
        }

        fun releaseWakeLock() {
            if (mWakeLock != null) {
                mWakeLock!!.release()
                mWakeLock = null
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        PushUtils.acquireWakeLock(this)
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "new Token: $token")

        val pref = this.getSharedPreferences("FCMtoken", Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putString("FCMtoken", token).apply()
        editor.commit()
        Log.i(TAG, "토큰 저장")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        PushUtils.acquireWakeLock(this)

        Log.d(TAG, "From: ${remoteMessage.from}")
        Log.d(TAG, "Message data : ${remoteMessage.data}")
        Log.d(TAG, "Message noti : ${remoteMessage.notification}")

        if (remoteMessage.notification != null) {
            showNotification(remoteMessage.notification!!, remoteMessage.data)
        } else if (remoteMessage.data.isNotEmpty()) {
            sendNotification(remoteMessage)
            Log.d(TAG, "title: ${remoteMessage.data["title"].toString()}")
            Log.d(TAG, "body : ${remoteMessage.data["body"].toString()}")
        } else {
            Log.e(TAG, "data가 비어있습니다. 메시지를 수신하지 못했습니다.")
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = channelDescription
                enableLights(true)
                lightColor = Color.RED
                enableVibration(true)
                setSound(
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(notification: RemoteMessage.Notification, data: Map<String, String>) {
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val safeAreaName = data ?: "알림1"
        val time = data ?: "알림2"
        val contentText = "알림1: $safeAreaName\n알림2: $time"
        // 알림 내용에 따라 수정 필요!!!!

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.app_icon_png)
            .setContentTitle(notification.title ?: "Cozymate")
            .setContentText(contentText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(contentText))
            .setContentIntent(pendingIntent)
            .setGroupSummary(true)
            .setAutoCancel(true)
            .setShowWhen(true)
            .setSound(soundUri)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setColor(resources.getColor(R.color.main_blue, theme))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setFullScreenIntent(pendingIntent, true)
        }

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(101, builder.build())
    }

    private fun sendNotification(remoteMessage: RemoteMessage) {
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                setSound(soundUri, audioAttributes)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val uniId: Int = (System.currentTimeMillis() / 7).toInt()
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        for (key in remoteMessage.data.keys) {
            intent.putExtra(key, remoteMessage.data[key])
        }
        val pendingIntent = PendingIntent.getActivity(this, uniId, intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_MUTABLE)

        val safeAreaName = remoteMessage.data ?: "알림1"
        val time = remoteMessage.data ?: "알림2"
        val contentText = "알림1: $safeAreaName\n알림2: $time"
        // 알림 내용에 따라 수정 필요!!

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(R.drawable.app_icon_png)
            .setContentTitle(remoteMessage.data["title"] ?: "Cozymate")
            .setContentText(contentText)
            .setGroupSummary(true)
            .setDefaults(Notification.DEFAULT_ALL)
            .setAutoCancel(true)
            .setSound(soundUri)
            .setContentIntent(pendingIntent)
            .setShowWhen(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder.setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_ALL)
                .setFullScreenIntent(pendingIntent, true)
        }

        notificationManager.notify(uniId, notificationBuilder.build())
    }

    fun getFirebaseToken() {
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            Log.d(TAG, "token=${it}")
        }
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.d(TAG, "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }
            val deviceToken = task.result
            Log.d(TAG, "token=${deviceToken}")
        }
    }
}
