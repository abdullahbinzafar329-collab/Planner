package com.biyas.planner

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class AddReminderActivity : AppCompatActivity() {

    private lateinit var labelInput: EditText
    private lateinit var dateButton: Button
    private lateinit var hourPicker: NumberPicker
    private lateinit var minutePicker: NumberPicker
    private lateinit var periodGroup: RadioGroup
    private lateinit var deleteButton: Button

    private var selectedCal = Calendar.getInstance()
    private var editingId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_reminder)

        labelInput = findViewById(R.id.input_label)
        dateButton = findViewById(R.id.button_date)
        hourPicker = findViewById(R.id.picker_hour)
        minutePicker = findViewById(R.id.picker_minute)
        periodGroup = findViewById(R.id.group_period)
        deleteButton = findViewById(R.id.button_delete)

        hourPicker.minValue = 1
        hourPicker.maxValue = 12
        minutePicker.minValue = 0
        minutePicker.maxValue = 59
        minutePicker.setFormatter { String.format(Locale.getDefault(), "%02d", it) }

        editingId = intent.getStringExtra("id")
        if (editingId != null) {
            labelInput.setText(intent.getStringExtra("label"))
            selectedCal.timeInMillis = intent.getLongExtra("timestamp", System.currentTimeMillis())
            deleteButton.visibility = android.view.View.VISIBLE
        } else {
            selectedCal.add(Calendar.MINUTE, 5)
        }

        val h = selectedCal.get(Calendar.HOUR)
        hourPicker.value = if (h == 0) 12 else h
        minutePicker.value = selectedCal.get(Calendar.MINUTE)
        val isPm = selectedCal.get(Calendar.AM_PM) == Calendar.PM
        periodGroup.check(if (isPm) R.id.radio_pm else R.id.radio_am)

        updateDateButton()
        dateButton.setOnClickListener { showDatePicker() }

        deleteButton.setOnClickListener {
            editingId?.let { id ->
                val r = ReminderStore.loadAll(this).find { it.id == id }
                if (r != null) AlarmScheduler.cancel(this, r)
                ReminderStore.delete(this, id)
            }
            setResult(RESULT_OK)
            finish()
        }

        findViewById<Button>(R.id.button_save).setOnClickListener { save() }
        findViewById<Button>(R.id.button_cancel).setOnClickListener { finish() }
    }

    private fun updateDateButton() {
        val fmt = SimpleDateFormat("EEE, MMM d yyyy", Locale.getDefault())
        dateButton.text = fmt.format(selectedCal.time)
    }

    private fun showDatePicker() {
        DatePickerDialog(
            this,
            { _, year, month, day ->
                selectedCal.set(Calendar.YEAR, year)
                selectedCal.set(Calendar.MONTH, month)
                selectedCal.set(Calendar.DAY_OF_MONTH, day)
                updateDateButton()
            },
            selectedCal.get(Calendar.YEAR),
            selectedCal.get(Calendar.MONTH),
            selectedCal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun save() {
        val label = labelInput.text.toString().trim()
        if (label.isEmpty()) {
            labelInput.error = "Add what's planned"
            return
        }
        var hour24 = hourPicker.value % 12
        if (periodGroup.checkedRadioButtonId == R.id.radio_pm) hour24 += 12
        selectedCal.set(Calendar.HOUR_OF_DAY, hour24)
        selectedCal.set(Calendar.MINUTE, minutePicker.value)
        selectedCal.set(Calendar.SECOND, 0)
        selectedCal.set(Calendar.MILLISECOND, 0)

        val id = editingId ?: UUID.randomUUID().toString()
        val reminder = Reminder(id, label, selectedCal.timeInMillis, false)
        ReminderStore.upsert(this, reminder)
        AlarmScheduler.schedule(this, reminder)

        setResult(RESULT_OK)
        finish()
    }
}
