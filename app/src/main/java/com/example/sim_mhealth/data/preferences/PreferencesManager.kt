package com.example.sim_mhealth.data.preferences

import android.content.Context
import android.content.SharedPreferences

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
    }

    fun saveLoginData(token: String, userId: Int, username: String, email: String?) {
        prefs.edit().apply {
            putString(KEY_TOKEN, token)
            putInt(KEY_USER_ID, userId)
            putString(KEY_USERNAME, username)
            putString(KEY_EMAIL, email ?: "")
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun getUserId(): Int = prefs.getInt(KEY_USER_ID, -1)

    fun getUsername(): String? = prefs.getString(KEY_USERNAME, null)

    fun clearLoginData() {
        prefs.edit().clear().apply()
    }

    fun saveRememberMe(remember: Boolean) {
        prefs.edit().putBoolean("remember_me", remember).apply()
    }

    fun getRememberMe(): Boolean {
        return prefs.getBoolean("remember_me", false)
    }

    fun saveLoginSession(token: String, userId: Int, username: String, remember: Boolean) {
        prefs.edit().apply {
            putString("auth_token", token)
            putInt("user_id", userId)
            putString("username", username)
            putBoolean("remember_me", remember)
            putBoolean("is_logged_in", true)
            apply()
        }
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean("is_logged_in", false) && getToken() != null
    }

    fun logout() {
        val rememberMe = getRememberMe()
        val editor = prefs.edit()

        if (rememberMe) {
            editor.putBoolean("is_logged_in", false)
        } else {
            editor.clear()
        }

        editor.apply()
    }
}