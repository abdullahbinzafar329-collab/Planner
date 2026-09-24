package com.biyas.planner

import android.Manifest
import android.app.AlarmManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var emptyView: TextView
    private var reminders: MutableList<Reminder> = mutableListOf()

    private val addReminderLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { refresh() }

    private val notifPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        NotificationHelper.createChannel(this)

        listView = findViewById(R.id.list_reminders)
        emptyView = findViewById(R.id.text_empty)

        findViewById<Button>(R.id.button_add).setOnClickListener {
            addReminderLauncher.launch(Intent(this, AddReminderActivity::class.java))
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            val r = reminders[position]
            val intent = Intent(this, AddReminderActivity::class.java)
            intent.putExtra("id", r.id)
            intent.putExtra("label", r.label)
            intent.putExtra("timestamp", r.timestamp)
            addReminderLauncher.launch(intent)
        }

        requestPermissionsIfNeeded()
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    private fun requestPermissionsIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                notifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val am = getSystemService(ALARM_SERVICE) as AlarmManager
            if (!am.canScheduleExactAlarms()) {
                try {
                    startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM, Uri.parse("package:$packageName")))
                } catch (e: Exception) { }
            }
        }
    }

    private fun refresh() {
        reminders = ReminderStore.loadAll(this)
        emptyView.visibility = if (reminders.isEmpty()) View.VISIBLE else View.GONE

        val fmt = SimpleDateFormat("EEE, MMM d \u2022 h:mm a", Locale.getDefault())
        listView.adapter = object : ArrayAdapter<Reminder>(
            this, android.R.layout.simple_list_item_2, android.R.id.text1, reminders
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val r = reminders[position]
                view.findViewById<TextView>(android.R.id.text1).text = r.label
                view.findViewById<TextView>(android.R.id.text2).text = fmt.format(r.timestamp)
                return view
            }
        }
    }
}
