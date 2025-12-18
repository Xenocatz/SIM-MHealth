package com.example.sim_mhealth.ui.components

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.sim_mhealth.MainActivity
import com.example.sim_mhealth.data.preferences.PreferencesManager
import java.text.SimpleDateFormat
import java.util.*

class SleepAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val prefsManager = PreferencesManager(context)
        val action = intent.action

        when (action) {
            "START_SLEEP" -> {
                prefsManager.saveSleepStartTime(getCurrentTime())
                showSleepNotification(context, "Waktunya Tidur! 😴", "Alarm tidur telah dimulai.", 2001)
            }
            "END_SLEEP" -> {
                prefsManager.saveSleepEndTime(getCurrentTime())
                calculateSleepData(context, prefsManager)
                showSleepNotification(context, "Waktunya Bangun! 🌅", "Alarm tidur telah berakhir.", 2002)
            }
        }
    }

    private fun calculateSleepData(context: Context, prefsManager: PreferencesManager) {
        val startTime = prefsManager.getSleepStartTime()
        val endTime = prefsManager.getSleepEndTime()

        if (startTime != null && endTime != null) {
            val format = SimpleDateFormat("HH:mm", Locale.getDefault())
            val start = format.parse(startTime)!!
            val end = format.parse(endTime)!!

            // Durasi tidur dalam menit
            val durationMinutes = (end.time - start.time) / (1000 * 60)
            prefsManager.saveSleepDuration(durationMinutes)

            // Hitung keterlambatan
            val plannedEnd = format.parse(endTime)!!
            val actualEnd = format.parse(getCurrentTime())!!
            val delayMinutes = if (actualEnd.after(plannedEnd)) {
                (actualEnd.time - plannedEnd.time) / (1000 * 60)
            } else 0

            prefsManager.saveSleepDelay(delayMinutes)
        }
    }

    private fun getCurrentTime(): String {
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        return format.format(Date())
    }

    private fun showSleepNotification(
        context: Context,
        title: String,
        message: String,
        notificationId: Int
    ) {
        val channelId = "sleep_alarm_channel"
        val channelName = "Sleep Alarm"

        // Buat notification channel untuk Android O+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifikasi alarm tidur"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 1000, 500, 1000)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Intent untuk buka app saat notifikasi diklik
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Build notification
        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm) // Icon alarm
            .setContentIntent(pendingIntent)
            .setAutoCancel(true) // Hilang saat diklik
            .setVibrate(longArrayOf(0, 1000, 500, 1000))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        // Tampilkan notifikasi
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notification)
    }
}