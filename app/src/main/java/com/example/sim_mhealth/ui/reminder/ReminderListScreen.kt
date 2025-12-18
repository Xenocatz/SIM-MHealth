package com.example.sim_mhealth.ui.reminder

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.sim_mhealth.data.api.PengingatItem
import com.example.sim_mhealth.data.preferences.PreferencesManager
import com.example.sim_mhealth.data.repository.ReminderRepository
import com.example.sim_mhealth.ui.theme.DarkGray900
import com.example.sim_mhealth.ui.theme.Gray200
import com.example.sim_mhealth.ui.theme.Primary50
import com.example.sim_mhealth.ui.theme.Primary500
import com.example.sim_mhealth.ui.theme.SIMMHealthTheme
import com.example.sim_mhealth.ui.theme.Succcess
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun ReminderListScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember { ReminderRepository() }
    val prefsManager = remember { PreferencesManager(context) }

    var reminderList by remember { mutableStateOf<List<PengingatItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) } // buat preview
    var selectedDate by remember {
        mutableStateOf(Calendar.getInstance())
    }
    var selectedReminder by remember { mutableStateOf<PengingatItem?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()

    val datesForMonth = remember(selectedDate) { getDatesForMonth(selectedDate) }
    val todayIndex = remember(datesForMonth) {
        val today = Calendar.getInstance()
        datesForMonth.indexOfFirst { isSameDay(it, today) }
    }

    LaunchedEffect(Unit) {
        val token = prefsManager.getToken()
        val userId = prefsManager.getUserId()

        if (token != null && userId != -1) {
            scope.launch {
                repository.getPengingatByPasien(token, userId).fold(onSuccess = { response ->
                    reminderList = response.pengingat
                    isLoading = false
                }, onFailure = { error ->
                    Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                    isLoading = false
                })
            }
        } else {
            isLoading = false
        }

        if (todayIndex >= 0) {
            kotlinx.coroutines.delay(50)
            listState.scrollToItem(todayIndex)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Black
                    )
                }
                Text(
                    text = "Pengingat",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.Center)
                )
                Row(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(onClick = { navController.navigate("notification_list") }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Color.Black
                        )
                    }
                    IconButton(onClick = {
                        navController.navigate("add_reminder_screen")
                    }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add",
                            tint = Color.Black
                        )
                    }
                }
            }

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = SimpleDateFormat(
                            "MMMM yyyy", Locale.forLanguageTag("id-ID")
                        ).format(selectedDate.time),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        state = listState,
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(datesForMonth) { date ->
                            DateChip(
                                date = date,
                                isSelected = isSameDay(date, selectedDate),
                                onClick = { selectedDate = date })
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    ReminderSection(
                        title = "Pagi",
                        icon = "🌅",
                        reminders = reminderList.filter { isPeriod(it.waktu_alarm, "pagi") },
                        onReminderClick = { reminder ->
                            selectedReminder = reminder
                            showBottomSheet = true
                        })

                    Spacer(modifier = Modifier.height(16.dp))

                    ReminderSection(
                        title = "Siang",
                        icon = "☀️",
                        reminders = reminderList.filter { isPeriod(it.waktu_alarm, "siang") },
                        onReminderClick = { reminder ->
                            selectedReminder = reminder
                            showBottomSheet = true
                        })

                    ReminderSection(
                        title = "Sore",
                        icon = "🌇",
                        reminders = reminderList.filter { isPeriod(it.waktu_alarm, "sore") },
                        onReminderClick = { reminder ->
                            selectedReminder = reminder
                            showBottomSheet = true
                        })

                    Spacer(modifier = Modifier.height(16.dp))

                    ReminderSection(
                        title = "Malam",
                        icon = "🌃",
                        reminders = reminderList.filter { isPeriod(it.waktu_alarm, "malam") },
                        onReminderClick = { reminder ->
                            selectedReminder = reminder
                            showBottomSheet = true
                        })

                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }

        if (showBottomSheet && selectedReminder != null) {
            ReminderBottomSheet(
                reminder = selectedReminder!!,
                onDismiss = { showBottomSheet = false },
                onDetail = {
                    navController.navigate("detail_reminder_screen/${selectedReminder!!.id_pengingat}")
                    showBottomSheet = false
                },
                onMarkDone = {
                    // Handle mark as done
                    showBottomSheet = false
                })
        }
    }
}

@Composable
fun DateChip(date: Calendar, isSelected: Boolean, onClick: () -> Unit) {
    val dayOfWeek =
        SimpleDateFormat("EEE", Locale.forLanguageTag("id-ID")).format(date.time).take(3)
    val dayOfMonth = date.get(Calendar.DAY_OF_MONTH)

    Card(
        modifier = Modifier
            .width(60.dp)
            .height(80.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF2196F3) else Color(0xFF424242)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = dayOfWeek,
                fontSize = 12.sp,
                color = Color.White,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = dayOfMonth.toString(),
                fontSize = 18.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ReminderSection(
    title: String,
    icon: String,
    reminders: List<PengingatItem>,
    onReminderClick: (PengingatItem) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(12.dp)
                .background(Primary50),
        ) {
            Text(text = icon, fontSize = 24.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black
            )
        }

        if (reminders.isEmpty()) {
            Text(
                text = "Tidak ada pengingat",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        } else {
            reminders.forEach { reminder ->
                ReminderCard(
                    reminder = reminder, onClick = { onReminderClick(reminder) })
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun ReminderCard(reminder: PengingatItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = CircleShape,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, DarkGray900)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = reminder.waktu_alarm.firstOrNull() ?: "00:00",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121),
                modifier = Modifier.padding(start = 8.dp, end = 16.dp)
            )

            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Gray200),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "💊", fontSize = 28.sp)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = reminder.nama_obat,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "${reminder.dosis_kuantitas.toInt()} ${reminder.dosis_unit} - ${reminder.catatan ?: "setelah makan"}",
                    fontSize = 13.sp,
                    color = Color.DarkGray
                )
            }

            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Done",
                tint = Succcess,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(28.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderBottomSheet(
    reminder: PengingatItem, onDismiss: () -> Unit, onDetail: () -> Unit, onMarkDone: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF2196F3),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "💊", fontSize = 48.sp)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = reminder.nama_obat,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = reminder.waktu_alarm.firstOrNull() ?: "00:00",
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }
                    Text(
                        text = "${reminder.dosis_kuantitas.toInt()} ${reminder.dosis_unit} - ${reminder.catatan ?: "setelah makan"}",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDetail,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        width = 1.dp, brush = androidx.compose.ui.graphics.SolidColor(Color.White)
                    ),
                    shape = CircleShape
                ) {
                    Text(
                        "Detail",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        null,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Button(
                    onClick = onMarkDone,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = CircleShape
                ) {
                    Text(
                        "Tandai Sudah Dikonsumsi",
                        color = Primary500,
                        fontWeight = FontWeight.Bold,
//                        fontSize = 16.sp,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

fun getDatesForMonth(currentDate: Calendar): List<Calendar> {
    val dates = mutableListOf<Calendar>()
    val calendar = currentDate.clone() as Calendar
    calendar.set(Calendar.DAY_OF_MONTH, 1)

    val maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    for (i in 1..maxDay) {
        val date = calendar.clone() as Calendar
        date.set(Calendar.DAY_OF_MONTH, i)
        dates.add(date)
    }
    return dates
}

fun isSameDay(date1: Calendar, date2: Calendar): Boolean {
    return date1.get(Calendar.YEAR) == date2.get(Calendar.YEAR) && date1.get(Calendar.MONTH) == date2.get(
        Calendar.MONTH
    ) && date1.get(Calendar.DAY_OF_MONTH) == date2.get(Calendar.DAY_OF_MONTH)
}

fun isPeriod(waktuAlarm: List<String>, period: String): Boolean {
    return waktuAlarm.any { time ->
        val hour = time.split(":")[0].toIntOrNull() ?: 0
        when (period) {
            "pagi" -> hour in 5..11
            "siang" -> hour in 12..17
            "malam" -> hour in 18..23 || hour in 0..4
            else -> false
        }
    }
}

@Preview(showBackground = true) // showBackground = true biar kelihatan di background putih
@Composable
fun ReminderPreview() {
    SIMMHealthTheme {
        val dummyPengingat = PengingatItem(
            id_pengingat = 1,
            id_pasien = 101,
            nama_obat = "Vitamin C",
            dosis_kuantitas = 1.0f,
            dosis_unit = "tablet",
            frekuensi = "Sehari 1x",
            tanggal_mulai = "2024-05-20",
            tanggal_akhir = null,
            catatan = "Diminum sesudah sarapan pagi.",
            waktu_alarm = listOf("08:00"),
            stok_awal = 30,
            stok_saat_ini = 25
        )
        Box(modifier = Modifier.fillMaxSize()) {
            ReminderBottomSheet(
                reminder = dummyPengingat,
                onDismiss = {},
                onDetail = {},
                onMarkDone = {})
        }
//        ReminderCard(
//            reminder = dummyPengingat,
//            onClick = {}
//        )
    }
}

