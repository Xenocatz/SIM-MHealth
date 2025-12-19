package com.example.sim_mhealth.data.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "mhealth_prefs"
        private const val KEY_TOKEN = "token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USERNAME = "username"
        private const val KEY_EMAIL = "email"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
        private const val KEY_REMEMBER_ME = "remember_me"
    }

    fun saveLoginData(token: String, userId: Int, username: String, email: String?) {
        prefs.edit {
            putString(KEY_TOKEN, token)
            putInt(KEY_USER_ID, userId)
            putString(KEY_USERNAME, username)
            putString(KEY_EMAIL, email ?: "")
            putBoolean(KEY_IS_LOGGED_IN, true)
        }
    }

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun getUserId(): Int = prefs.getInt(KEY_USER_ID, -1)

    fun getUsername(): String? = prefs.getString(KEY_USERNAME, null)

    fun clearLoginData() {
        prefs.edit {
            remove(KEY_TOKEN)
            remove(KEY_USER_ID)
            remove(KEY_USERNAME)
            remove(KEY_EMAIL)
            remove(KEY_IS_LOGGED_IN)
            remove("auth_token")
            remove("user_id")
            remove("username")
        }
    }

    fun saveRememberMe(remember: Boolean) {
        prefs.edit { putBoolean(KEY_REMEMBER_ME, remember) }
    }

    fun getRememberMe(): Boolean {
        return prefs.getBoolean(KEY_REMEMBER_ME, false)
    }

    fun saveLoginSession(token: String, userId: Int, username: String, remember: Boolean) {
        prefs.edit {
            putString("auth_token", token)
            putInt("user_id", userId)
            putString("username", username)
            putBoolean(KEY_REMEMBER_ME, remember)
            putBoolean(KEY_IS_LOGGED_IN, true)
        }
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false) && getToken() != null
    }

    fun setOnboardingCompleted(completed: Boolean) {
        prefs.edit { putBoolean(KEY_ONBOARDING_COMPLETED, completed) }
    }

    fun isOnboardingCompleted(): Boolean {
        return prefs.getBoolean(KEY_ONBOARDING_COMPLETED, false)
    }

    fun logout() {
        val rememberMe = getRememberMe()
        prefs.edit {
            if (rememberMe) {
                putBoolean(KEY_IS_LOGGED_IN, false)
            } else {
                remove(KEY_TOKEN)
                remove(KEY_USER_ID)
                remove(KEY_USERNAME)
                remove(KEY_EMAIL)
                remove(KEY_IS_LOGGED_IN)
                remove("auth_token")
                remove("user_id")
                remove("username")
            }
        }
    }

    fun saveScheduledSleepTime(startTime: String, endTime: String) {
        prefs.edit {
            putString("scheduled_sleep_start", startTime)
            putString("scheduled_sleep_end", endTime)
        }
    }

    fun getScheduledSleepStartTime(): String? = prefs.getString("scheduled_sleep_start", null)
    fun getScheduledSleepEndTime(): String? = prefs.getString("scheduled_sleep_end", null)

    fun saveSleepStartTime(startTime: String) {
        prefs.edit { putString("actual_sleep_start", startTime) }
    }

    fun saveSleepEndTime(endTime: String) {
        prefs.edit { putString("actual_sleep_end", endTime) }
    }

    fun getSleepStartTime(): String? = prefs.getString("actual_sleep_start", null)
    fun getSleepEndTime(): String? = prefs.getString("actual_sleep_end", null)

    fun saveSleepDuration(durationMinutes: Long) {
        prefs.edit { putLong("sleep_duration", durationMinutes) }
    }

    fun getSleepDuration(): Long = prefs.getLong("sleep_duration", 0)

    fun saveSleepDelay(delayMinutes: Long) {
        prefs.edit { putLong("sleep_delay", delayMinutes) }
    }

    fun getSleepDelay(): Long = prefs.getLong("sleep_delay", 0)

    fun clearSleepData() {
        prefs.edit {
            remove("scheduled_sleep_start")
            remove("scheduled_sleep_end")
            remove("actual_sleep_start")
            remove("actual_sleep_end")
            remove("sleep_duration")
            remove("sleep_delay")
        }
    }

    val stepsFlow: Flow<Int> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == "current_steps") {
                trySend(prefs.getInt("current_steps", 0))
            }
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        // Send initial value
        send(prefs.getInt("current_steps", 0))

        awaitClose {
            prefs.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }
}