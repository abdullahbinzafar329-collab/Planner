package com.biyas.planner

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

object NotificationHelper {
    const val CHANNEL_ID = "biyas_alarms"

    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (manager.getNotificationChannel(CHANNEL_ID) == null) {
                val channel = NotificationChannel(
                    CHANNEL_ID, "Planner alarms", NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Alerts for your Biyas Planner reminders"
                    enableVibration(true)
                    setSound(null, null)
                }
                manager.createNotificationChannel(channel)
            }
        }
    }

    fun showAlarmNotification(context: Context, id: String, label: String, timestamp: Long) {
        createChannel(context)

        val fullScreenIntent = Intent(context, AlarmRingActivity::class.java).apply {
            putExtra("id", id)
            putExtra("label", label)
            putExtra("timestamp", timestamp)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val fullScreenPendingIntent = PendingIntent.getActivity(
            context, id.hashCode(), fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Biyas Planner reminder")
            .setContentText(label)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setContentIntent(fullScreenPendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(id.hashCode(), notification)

        try {
            context.startActivity(fullScreenIntent)
        } catch (e: Exception) {
            // Rely on the notification's full-screen intent instead.
        }
    }
}
