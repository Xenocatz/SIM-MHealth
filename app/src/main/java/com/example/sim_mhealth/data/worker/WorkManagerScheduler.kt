package com.example.sim_mhealth.data.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

object WorkManagerScheduler {

    private const val SAVE_STEPS_WORK_NAME = "save_steps_daily"

    fun scheduleDailySave(context: Context) {
        val currentDate = Calendar.getInstance()
        val dueDate = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 0)

            // If time has passed today, schedule for tomorrow
            if (before(currentDate)) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        val timeDiff = dueDate.timeInMillis - currentDate.timeInMillis

        val dailyWorkRequest = PeriodicWorkRequestBuilder<SaveStepsWorker>(
            1, TimeUnit.DAYS
        )
            .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            SAVE_STEPS_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            dailyWorkRequest
        )
    }

    fun cancelDailySave(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(SAVE_STEPS_WORK_NAME)
    }
}