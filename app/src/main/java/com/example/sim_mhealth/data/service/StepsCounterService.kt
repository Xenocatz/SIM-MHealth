package com.example.sim_mhealth.data.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.example.sim_mhealth.MainActivity
import com.example.sim_mhealth.data.preferences.PreferencesManager
import com.example.sim_mhealth.data.repository.StepsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.sim_mhealth.data.worker.WorkManagerScheduler

class StepsCounterService : Service(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var stepSensor: Sensor? = null
    private lateinit var prefsManager: PreferencesManager
    private lateinit var repository: StepsRepository
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

//    private val sharedPrefs by lazy {
//        getSharedPreferences("steps_prefs", Context.MODE_PRIVATE)
//    }

    private fun getUserSpecificPrefs(): SharedPreferences {
        val userId = prefsManager.getUserId()
        val prefsName = if (userId != -1) "steps_prefs_user_$userId" else "steps_prefs_default"
        return getSharedPreferences(prefsName, Context.MODE_PRIVATE)
    }

    private var initialSteps = 0
    private var currentSteps = 0
    private var todayDate = getCurrentDate()

    companion object {
        const val CHANNEL_ID = "steps_counter_channel"
        const val NOTIFICATION_ID = 1
        private const val KEY_INITIAL_STEPS = "initial_steps"
        private const val KEY_CURRENT_STEPS = "current_steps"
        private const val KEY_DATE = "date"
    }

    override fun onCreate() {
        super.onCreate()
        prefsManager = PreferencesManager(this)
        repository = StepsRepository()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        createNotificationChannel()
        loadTodaySteps()

        WorkManagerScheduler.scheduleDailySave(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, createNotification(0))

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            stepSensor?.let {
                sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
            }
        }

        return START_STICKY
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            val totalSteps = event.values[0].toInt()

            val currentDate = getCurrentDate()
            if (currentDate != todayDate) {
                // New day, save yesterday's data and reset
                saveStepsToServer()
                resetDailySteps(totalSteps)
                todayDate = currentDate
            } else {
                if (initialSteps == 0) {
                    initialSteps = totalSteps
                    saveToPrefs()
                }
                currentSteps = totalSteps - initialSteps
                saveToPrefs()
            }

            updateNotification(currentSteps)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun loadTodaySteps() {
        val prefs = getUserSpecificPrefs()
        val savedDate = prefs.getString(KEY_DATE, "")
        val currentDate = getCurrentDate()

        if (savedDate == currentDate) {
            initialSteps = prefs.getInt(KEY_INITIAL_STEPS, 0)
            currentSteps = prefs.getInt(KEY_CURRENT_STEPS, 0)
        } else {
            resetDailySteps(0)
        }
    }

    private fun resetDailySteps(newInitial: Int) {
        initialSteps = newInitial
        currentSteps = 0
        saveToPrefs()
    }

    private fun saveToPrefs() {
        val prefs = getUserSpecificPrefs()
        prefs.edit().apply {
            putInt(KEY_INITIAL_STEPS, initialSteps)
            putInt(KEY_CURRENT_STEPS, currentSteps)
            putString(KEY_DATE, todayDate)
            apply()
        }
    }

    private fun saveStepsToServer() {
        val token = prefsManager.getToken() ?: return
        val userId = prefsManager.getUserId()
        if (userId == -1 || currentSteps == 0) return

        serviceScope.launch {
            repository.addLangkah(
                token = token,
                idPasien = userId,
                jumlahLangkah = currentSteps,
                tanggal = todayDate,
                catatan = null
            )
        }
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Steps Counter",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows your daily step count"
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(steps: Int): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Steps Today")
            .setContentText("$steps steps")
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    private fun updateNotification(steps: Int) {
        val notification = createNotification(steps)
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
        saveStepsToServer()
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}