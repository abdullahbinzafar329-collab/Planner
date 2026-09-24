package com.biyas.planner

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.widget.Button
import java.text.SimpleDateFormat
import java.util.Locale

class AlarmRingActivity : AppCompatActivity() {
    private var player: MediaPlayer? = null
    private var vibrator: Vibrator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            )
        }

        setContentView(R.layout.activity_alarm_ring)

        val label = intent.getStringExtra("label") ?: ""
        val timestamp = intent.getLongExtra("timestamp", System.currentTimeMillis())

        findViewById<TextView>(R.id.text_alarm_label).text = label
        findViewById<TextView>(R.id.text_alarm_time).text =
            SimpleDateFormat("h:mm a", Locale.getDefault()).format(timestamp)

        findViewById<Button>(R.id.button_dismiss).setOnClickListener {
            stopAlarm()
            finish()
        }

        startAlarm()
    }

    private fun startAlarm() {
        try {
            val uri = RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            player = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                setDataSource(this@AlarmRingActivity, uri)
                isLooping = true
                prepare()
                start()
            }
        } catch (e: Exception) {
            // No default ringtone available; ring silently with vibration only.
        }

        vibrator = getSystemService(VIBRATOR_SERVICE) as? Vibrator
        val pattern = longArrayOf(0, 500, 500)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createWaveform(pattern, 0))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(pattern, 0)
        }
    }

    private fun stopAlarm() {
        player?.stop()
        player?.release()
        player = null
        vibrator?.cancel()
    }

    override fun onDestroy() {
        stopAlarm()
        super.onDestroy()
    }
}
