package com.example.sim_mhealth.utils

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

object DateUtils {

    /**
     * Convert date from DD/MM/YYYY or DD-MM-YYYY format to ISO 8601 (YYYY-MM-DD)
     * @param dateString Input date in format DD/MM/YYYY or DD-MM-YYYY
     * @return Date in ISO 8601 format (YYYY-MM-DD) or null if invalid
     */
    fun convertToISO8601(dateString: String): String? {
        if (dateString.isBlank()) return null

        return try {
            val normalized = dateString.replace("-", "/")
            val parts = normalized.split("/")

            if (parts.size != 3) return null

            val day = parts[0].padStart(2, '0')
            val month = parts[1].padStart(2, '0')
            val year = parts[2]

            val dayInt = day.toIntOrNull() ?: return null
            val monthInt = month.toIntOrNull() ?: return null
            val yearInt = year.toIntOrNull() ?: return null

            if (dayInt !in 1..31 || monthInt !in 1..12 || yearInt < 1900) {
                return null
            }

            "$year-$month-$day"
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Convert date from DD/MM/YYYY to ISO 8601 with time (YYYY-MM-DDTHH:mm:ss.sssZ)
     * @param dateString Input date in format DD/MM/YYYY
     * @param setToStartOfDay If true, time will be 00:00:00, otherwise current time
     * @return Date in ISO 8601 format with timezone or null if invalid
     */
    fun convertToISO8601WithTime(
        dateString: String,
        setToStartOfDay: Boolean = true
    ): String? {
        val isoDate = convertToISO8601(dateString) ?: return null

        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            outputFormat.timeZone = TimeZone.getTimeZone("UTC")

            val date = inputFormat.parse(isoDate) ?: return null

            if (setToStartOfDay) {
                val calendar = java.util.Calendar.getInstance()
                calendar.time = date
                calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
                calendar.set(java.util.Calendar.MINUTE, 0)
                calendar.set(java.util.Calendar.SECOND, 0)
                calendar.set(java.util.Calendar.MILLISECOND, 0)
                outputFormat.format(calendar.time)
            } else {
                outputFormat.format(date)
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Convert MM/YYYY or MM-YYYY format to YYYY-MM format
     * @param dateString Input date in format MM/YYYY or MM-YYYY
     * @return Date in YYYY-MM format or null if invalid
     */
    fun convertMonthYearToISO(dateString: String): String? {
        if (dateString.isBlank()) return null

        return try {
            val normalized = dateString.replace("-", "/")
            val parts = normalized.split("/")

            if (parts.size != 2) return null

            val month = parts[0].padStart(2, '0')
            val year = parts[1]

            val monthInt = month.toIntOrNull() ?: return null
            val yearInt = year.toIntOrNull() ?: return null

            if (monthInt !in 1..12 || yearInt < 1900) {
                return null
            }

            "$year-$month"
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Convert ISO 8601 (YYYY-MM-DD) back to DD/MM/YYYY
     * @param isoDate Input date in ISO 8601 format
     * @return Date in DD/MM/YYYY format or null if invalid
     */
    fun convertFromISO8601(isoDate: String): String? {
        if (isoDate.isBlank()) return null

        return try {
            val parts = isoDate.split("-")

            if (parts.size != 3) return null

            val year = parts[0]
            val month = parts[1]
            val day = parts[2]

            "$day/$month/$year"
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Validate date format DD/MM/YYYY or DD-MM-YYYY
     * @param dateString Input date string
     * @return true if valid, false otherwise
     */
    fun isValidDateFormat(dateString: String): Boolean {
        return convertToISO8601(dateString) != null
    }

    /**
     * Format ISO 8601 date (with or without time) to DD-MM-YYYY for display
     * @param dateString Input date in ISO 8601 format (YYYY-MM-DD or YYYY-MM-DDTHH:mm:ss.sssZ)
     * @return Date in DD-MM-YYYY format or empty string if invalid
     */
    fun formatDateForDisplay(dateString: String?): String {
        if (dateString.isNullOrBlank()) return ""

        return try {
            if (dateString.contains("T")) {
                val inputFormat =
                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                inputFormat.timeZone = TimeZone.getTimeZone("UTC")
                val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

                val date = inputFormat.parse(dateString)
                date?.let { outputFormat.format(it) } ?: dateString.substringBefore("T")
            } else {
                val parts = dateString.split("-")
                if (parts.size == 3) {
                    val year = parts[0]
                    val month = parts[1]
                    val day = parts[2]
                    "$day-$month-$year"
                } else {
                    dateString
                }
            }
        } catch (e: Exception) {
            // Fallback: just extract date part and return
            dateString.substringBefore("T")
        }
    }

    /**
     * Format ISO 8601 date to DD/MM/YYYY (with slash separator)
     * @param dateString Input date in ISO 8601 format
     * @return Date in DD/MM/YYYY format or empty string if invalid
     */
    fun formatDateForDisplayWithSlash(dateString: String?): String {
        return formatDateForDisplay(dateString).replace("-", "/")
    }
}

fun String.toISO8601(): String? = DateUtils.convertToISO8601(this)
fun String.toISO8601WithTime(setToStartOfDay: Boolean = true): String? =
    DateUtils.convertToISO8601WithTime(this, setToStartOfDay)

fun String.fromISO8601(): String? = DateUtils.convertFromISO8601(this)