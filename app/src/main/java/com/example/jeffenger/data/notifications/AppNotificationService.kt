package com.example.jeffenger.data.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.jeffenger.R

class AppNotificationService(
    private val context: Context,
    private val channelType: NotificationChannelType
) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val existingChannel =
                notificationManager.getNotificationChannel(channelType.channelId)

            if (existingChannel == null) {
                val channel = NotificationChannel(
                    channelType.channelId,
                    channelType.channelName,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    setShowBadge(true)
                }
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showChatNotification(
        notificationId: Int,
        title: String,
        body: String,
        unreadCount: Int,
        contentIntent: android.app.PendingIntent
    ) {
        val notification = NotificationCompat.Builder(
            context,
            channelType.channelId
        )
            .setSmallIcon(R.drawable.jeffenger_splash_icon)
//            .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setNumber(unreadCount)
            .setContentIntent(contentIntent)
            .build()

//        NotificationManagerCompat.from(context)
//            .notify(System.currentTimeMillis().toInt(), notification)
        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showEventNotification(
        notificationId: Int,
        title: String,
        body: String,
        contentIntent: android.app.PendingIntent
    ) {
        val notification = NotificationCompat.Builder(context, channelType.channelId)
            .setSmallIcon(R.drawable.jeffenger_splash_icon)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(contentIntent)
            .build()

        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }
}