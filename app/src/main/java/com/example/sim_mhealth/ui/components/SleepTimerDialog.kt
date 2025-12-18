package com.example.sim_mhealth.ui.components

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.sim_mhealth.data.preferences.PreferencesManager
import java.text.SimpleDateFormat
import java.util.*

@RequiresApi(Build.VERSION_CODES.S)
@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SleepTimerDialog(
    context: Context,
    preferencesManager: PreferencesManager,
    onDismiss: () -> Unit,
    onTimerSet: () -> Unit
) {
    var showStartTimePicker by remember { mutableStateOf(true) }
    var selectedHour by remember { mutableIntStateOf(22) }
    var selectedMinute by remember { mutableIntStateOf(0) }

    var startTime by remember { mutableStateOf<String?>(null) }
    var endTime by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = if (showStartTimePicker) "Atur Waktu Tidur" else "Atur Waktu Bangun",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF2196F3),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Time Picker
                TimePicker(
                    state = rememberTimePickerState(
                        initialHour = selectedHour,
                        initialMinute = selectedMinute,
                        is24Hour = true
                    ),
                    colors = TimePickerDefaults.colors(
                        clockDialColor = Color(0xFFE3F2FD),
                        clockDialSelectedContentColor = Color.White,
                        clockDialUnselectedContentColor = Color.DarkGray,
                        selectorColor = Color(0xFF2196F3),
                        timeSelectorSelectedContainerColor = Color(0xFF2196F3),
                        timeSelectorUnselectedContainerColor = Color(0xFFE0E0E0),
                        timeSelectorSelectedContentColor = Color.White,
                        timeSelectorUnselectedContentColor = Color.DarkGray
                    ),
                    modifier = Modifier.padding(16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Batal", color = Color.Gray)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            val timeStr = String.format("%02d:%02d", selectedHour, selectedMinute)

                            if (showStartTimePicker) {
                                startTime = timeStr
                                showStartTimePicker = false
                                selectedHour = 6
                                selectedMinute = 0
                            } else {
                                endTime = timeStr

                                preferencesManager.saveScheduledSleepTime(startTime!!, endTime!!)

                                setSleepAlarm(context, startTime!!, endTime!!)

                                onTimerSet()
                                onDismiss()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                    ) {
                        Text(if (showStartTimePicker) "Selanjutnya" else "Simpan")
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.S)
@SuppressLint("ScheduleExactAlarm")
fun setSleepAlarm(context: Context, startTime: String, endTime: String) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val startCalendar = Calendar.getInstance().apply {
        time = timeFormat.parse(startTime)!!
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        if (timeInMillis < System.currentTimeMillis()) {
            add(Calendar.DAY_OF_YEAR, 1) // Alarm besok jika waktu sudah lewat
        }
    }

    val endCalendar = Calendar.getInstance().apply {
        time = timeFormat.parse(endTime)!!
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        if (timeInMillis < startCalendar.timeInMillis) {
            add(Calendar.DAY_OF_YEAR, 1) // Besok jika waktu bangun < waktu tidur
        }
    }

    // Set alarm mulai tidur
    val startIntent = Intent(context, SleepAlarmReceiver    ::class.java).apply {
        action = "START_SLEEP"
        putExtra("end_time", endTime)
    }
    val startPendingIntent = PendingIntent.getBroadcast(
        context, 1001, startIntent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (!alarmManager.canScheduleExactAlarms()) {
            // Redirect user ke settings
            val intent = Intent().apply {
                action = android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                data = android.net.Uri.fromParts("package", context.packageName, null)
            }
            context.startActivity(intent)
            return
        }
    }

    alarmManager.setExactAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP,
        startCalendar.timeInMillis,
        startPendingIntent
    )

    val endIntent = Intent(context, SleepAlarmReceiver::class.java).apply {
        action = "END_SLEEP"
        putExtra("start_time", startTime)
    }
    val endPendingIntent = PendingIntent.getBroadcast(
        context, 1002, endIntent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    alarmManager.setExactAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP,
        endCalendar.timeInMillis,
        endPendingIntent
    )
}