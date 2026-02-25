package com.example.jeffenger.data.notifications

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class JeffengerMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "New token: $token")
        FcmTokenManager.saveToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val permissionGranted =
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED

        if (!permissionGranted) {
            Log.d("FCM", "POST_NOTIFICATIONS not granted")
            return
        }
    }

        // Systemseitig: sind Notifications für die App erlaubt?
        val notificationsAllowed = NotificationManagerCompat
            .from(applicationContext)
            .areNotificationsEnabled()

        if (!notificationsAllowed) {
            Log.d("FCM", "Notifications disabled on device -> skip showing notification")
            return
        }

        val chatId = message.data["chatId"] ?: return
        val title = message.data["title"] ?: return
        val body = message.data["body"] ?: return
        val unreadCount = message.data["unreadCount"]?.toIntOrNull() ?: 1

        val notificationId = chatId.hashCode()

        val intent =
            Intent(this, com.example.jeffenger.MainActivity::class.java).apply {
                action = "OPEN_CHAT"
                putExtra("chatId", chatId)
                addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP or
                    Intent.FLAG_ACTIVITY_NEW_TASK
                )
            }

        val pendingIntent = android.app.PendingIntent.getActivity(
            this,
            notificationId, // requestCode stabil pro Chat
            intent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
        )


        val notificationService = AppNotificationService(
            context = applicationContext,
            channelType = NotificationChannelType.CHAT_MESSAGES
        )

        notificationService.showChatNotification(
            notificationId = notificationId,
            title = title,
            body = body,
            unreadCount = unreadCount,
            contentIntent = pendingIntent
        )
//        notificationService.showChatNotification(title, body, /*unreadCount*/)
    }
}