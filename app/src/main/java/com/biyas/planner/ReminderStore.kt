package com.biyas.planner

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class Reminder(
    val id: String,
    val label: String,
    val timestamp: Long,
    var fired: Boolean = false
)

object ReminderStore {
    private const val PREFS = "biyas_planner_prefs"
    private const val KEY = "reminders"

    fun loadAll(context: Context): MutableList<Reminder> {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val raw = prefs.getString(KEY, "[]") ?: "[]"
        val arr = JSONArray(raw)
        val list = mutableListOf<Reminder>()
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            list.add(
                Reminder(
                    id = o.getString("id"),
                    label = o.getString("label"),
                    timestamp = o.getLong("timestamp"),
                    fired = o.optBoolean("fired", false)
                )
            )
        }
        list.sortBy { it.timestamp }
        return list
    }

    fun saveAll(context: Context, list: List<Reminder>) {
        val arr = JSONArray()
        for (r in list) {
            val o = JSONObject()
            o.put("id", r.id)
            o.put("label", r.label)
            o.put("timestamp", r.timestamp)
            o.put("fired", r.fired)
            arr.put(o)
        }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putString(KEY, arr.toString()).apply()
    }

    fun upsert(context: Context, reminder: Reminder) {
        val list = loadAll(context)
        val idx = list.indexOfFirst { it.id == reminder.id }
        if (idx >= 0) list[idx] = reminder else list.add(reminder)
        saveAll(context, list)
    }

    fun delete(context: Context, id: String) {
        val list = loadAll(context).filter { it.id != id }
        saveAll(context, list)
    }
}
