package com.example.sim_mhealth.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.sim_mhealth.data.preferences.PreferencesManager
import com.example.sim_mhealth.data.repository.StepsRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SaveStepsWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val prefsManager = PreferencesManager(applicationContext)
            val repository = StepsRepository()

            val token = prefsManager.getToken() ?: return Result.failure()
            val userId = prefsManager.getUserId()
            if (userId == -1) return Result.failure()

            // Get yesterday's data from SharedPreferences
            val stepsPrefs = applicationContext.getSharedPreferences("steps_prefs", Context.MODE_PRIVATE)
            val currentSteps = stepsPrefs.getInt("current_steps", 0)
            val savedDate = stepsPrefs.getString("date", getCurrentDate())

            // Only save if there are steps to save
            if (currentSteps > 0 && savedDate != null) {
                repository.addLangkah(
                    token = token,
                    idPasien = userId,
                    jumlahLangkah = currentSteps,
                    tanggal = savedDate,
                    catatan = null
                )

                // Reset for new day
                stepsPrefs.edit().apply {
                    putInt("initial_steps", 0)
                    putInt("current_steps", 0)
                    putString("date", getCurrentDate())
                    apply()
                }
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }
}