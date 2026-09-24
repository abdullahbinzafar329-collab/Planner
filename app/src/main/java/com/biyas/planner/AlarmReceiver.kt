package com.biyas.planner

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getStringExtra("id") ?: return
        val label = intent.getStringExtra("label") ?: ""
        val timestamp = intent.getLongExtra("timestamp", System.currentTimeMillis())

        val list = ReminderStore.loadAll(context)
        val r = list.find { it.id == id }
        if (r != null) {
            r.fired = true
            ReminderStore.saveAll(context, list)
        }

        NotificationHelper.showAlarmNotification(context, id, label, timestamp)
    }
}
