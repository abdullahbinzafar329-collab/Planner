package com.biyas.planner

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val now = System.currentTimeMillis()
            val reminders = ReminderStore.loadAll(context)
            reminders.filter { !it.fired && it.timestamp > now }
                .forEach { AlarmScheduler.schedule(context, it) }
        }
    }
}
