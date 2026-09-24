package com.biyas.planner

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build

object AlarmScheduler {
    fun schedule(context: Context, reminder: Reminder) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("id", reminder.id)
            putExtra("label", reminder.label)
            putExtra("timestamp", reminder.timestamp)
        }
        val pi = PendingIntent.getBroadcast(
            context, reminder.id.hashCode(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !am.canScheduleExactAlarms()) {
            return
        }
        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, reminder.timestamp, pi)
    }

    fun cancel(context: Context, reminder: Reminder) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pi = PendingIntent.getBroadcast(
            context, reminder.id.hashCode(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        am.cancel(pi)
    }
}
