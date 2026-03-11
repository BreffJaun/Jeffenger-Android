package com.example.jeffenger.data.notifications

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.example.jeffenger.MainActivity
import com.example.jeffenger.utils.enums.EventStatus
import com.example.jeffenger.utils.state.AppForegroundState
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

        // Android 13+ Permission Check
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionGranted =
                checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) ==
                        PackageManager.PERMISSION_GRANTED

            if (!permissionGranted) {
                Log.d("FCM", "POST_NOTIFICATIONS not granted")
                return
            }
        }

        // System Notifications disabled?
        val notificationsAllowed = NotificationManagerCompat
            .from(applicationContext)
            .areNotificationsEnabled()

        if (!notificationsAllowed) {
            Log.d("FCM", "Notifications disabled on device")
            return
        }

        val data = message.data
        val type = data["type"] ?: "CHAT"

        // CALENDAR NOTIFICATION
        if (type.startsWith("CAL_")) {

            val eventId = data["eventId"] ?: return
            val title = data["title"] ?: "Kalender"

            // STATUS ENUM MAPPING (ACCEPTED -> Bestätigt etc.)
            val status = data["status"]

            val body = if (status != null) {
                try {
                    val label = EventStatus.valueOf(status).label
                    "Status: $label"
                } catch (e: Exception) {
                    data["body"] ?: ""
                }
            } else {
                data["body"] ?: ""
            }

            val notificationId = ("CAL_" + eventId).hashCode()

            val intent = Intent(this, MainActivity::class.java).apply {

                // DELETE → nur Kalender öffnen + Snackbar anzeigen
                if (type == "CAL_EVENT_DELETED") {
                    action = "OPEN_CALENDAR"
                    putExtra("calendarMessage", body)
                } else {
                    action = "OPEN_CALENDAR_EVENT"
                    putExtra("eventId", eventId)
                }

                addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_SINGLE_TOP or
                            Intent.FLAG_ACTIVITY_NEW_TASK
                )
            }

            val pendingIntent = android.app.PendingIntent.getActivity(
                this,
                notificationId,
                intent,
                android.app.PendingIntent.FLAG_UPDATE_CURRENT or
                        android.app.PendingIntent.FLAG_IMMUTABLE
            )

            val notificationService = AppNotificationService(
                context = applicationContext,
                channelType = NotificationChannelType.CALENDAR_EVENTS
            )

            notificationService.showEventNotification(
                notificationId = notificationId,
                title = title,
                body = body,
                contentIntent = pendingIntent
            )

            return
        }

        // CHAT NOTIFICATION
        val chatId = data["chatId"] ?: return
        val companyId = data["companyId"] ?: return

        if (
            AppForegroundState.isAppInForeground &&
            AppForegroundState.currentOpenChatId == chatId
        ) {
            Log.d("FCM", "Chat already open -> skip notification")
            return
        }

        val title = data["title"] ?: return
        val body = data["body"] ?: return
        val unreadCount = data["unreadCount"]?.toIntOrNull() ?: 1

        val notificationId = chatId.hashCode()

        val intent =
            Intent(this, MainActivity::class.java).apply {
                action = "OPEN_CHAT"
                putExtra("chatId", chatId)
                putExtra("companyId", companyId)

                addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP or
                    Intent.FLAG_ACTIVITY_NEW_TASK
                )
            }

        val pendingIntent = android.app.PendingIntent.getActivity(
            this,
            notificationId,
            intent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or
                    android.app.PendingIntent.FLAG_IMMUTABLE
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
    }
}


