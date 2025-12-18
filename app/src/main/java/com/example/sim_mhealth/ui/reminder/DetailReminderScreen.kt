package com.example.sim_mhealth.ui.reminder

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.sim_mhealth.data.api.PengingatDetail
import com.example.sim_mhealth.data.preferences.PreferencesManager
import com.example.sim_mhealth.data.repository.ReminderRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DetailReminderScreen(navController: NavController, reminderId: Int) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember { ReminderRepository() }
    val prefsManager = remember { PreferencesManager(context) }

    var reminderDetail by remember { mutableStateOf<PengingatDetail?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(reminderId) {
        val token = prefsManager.getToken()

        if (token != null) {
            scope.launch {
                repository.getPengingatById(token, reminderId).fold(
                    onSuccess = { response ->
                        reminderDetail = response.pengingat
                        isLoading = false
                    },
                    onFailure = { error ->
                        Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT)
                            .show()
                        isLoading = false
                    }
                )
            }
        } else {
            isLoading = false
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else if (reminderDetail != null) {
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
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Black
                    )
                }
                Text(
                    text = "Detail Jadwal",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.Center)
                )
                IconButton(
                    onClick = {
                        navController.navigate("edit_reminder_screen/$reminderId")
                    },
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = Color.Black
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(Color(0xFFE3F2FD), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "💊", fontSize = 56.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = reminderDetail!!.nama_obat,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Text(
                        text = "${reminderDetail!!.dosis_kuantitas.toInt()} ${reminderDetail!!.dosis_unit}",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        DetailInfoRow(
                            icon = Icons.Default.Schedule,
                            label = "Dosis Selanjutnya",
                            value = "${reminderDetail!!.waktu_alarm.firstOrNull() ?: "00:00"}"
                        )

                        Divider(modifier = Modifier.padding(vertical = 12.dp))

                        DetailInfoRow(
                            icon = Icons.Default.Refresh,
                            label = "Frekuensi",
                            value = "${reminderDetail!!.frekuensi}"
                        )

                        Divider(modifier = Modifier.padding(vertical = 12.dp))

                        DetailInfoRow(
                            icon = Icons.Default.DateRange,
                            label = "Periode",
                            value = formatDateRange(
                                reminderDetail!!.tanggal_mulai,
                                reminderDetail!!.tanggal_akhir
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (!reminderDetail!!.catatan.isNullOrBlank()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Text(
                                text = "Catatan",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = reminderDetail!!.catatan ?: "",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                lineHeight = 20.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                Text(
                    text = "Jadwal Hari Ini",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(12.dp))

                reminderDetail!!.waktu_alarm.forEach { time ->
                    TimeCard(
                        time = time,
                        isDone = false,
                        isSkipped = false
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        Toast.makeText(context, "Tandai sudah dikonsumsi", Toast.LENGTH_SHORT)
                            .show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Tandai Sudah Dikonsumsi",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFF44336)
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        width = 2.dp,
                        brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFF44336))
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = Color(0xFFF44336)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Hapus Pengingat",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Hapus Pengingat?") },
                text = { Text("Apakah Anda yakin ingin menghapus pengingat ini?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val token = prefsManager.getToken()
                            if (token != null) {
                                scope.launch {
                                    repository.deletePengingat(token, reminderId).fold(
                                        onSuccess = {
                                            Toast.makeText(
                                                context,
                                                "Pengingat berhasil dihapus",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            navController.popBackStack()
                                        },
                                        onFailure = { error ->
                                            Toast.makeText(
                                                context,
                                                "Error: ${error.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    )
                                }
                            }
                            showDeleteDialog = false
                        }
                    ) {
                        Text("Hapus", color = Color(0xFFF44336))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Batal")
                    }
                }
            )
        }
    }
}

@Composable
fun DetailInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF2196F3),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Composable
fun TimeCard(time: String, isDone: Boolean, isSkipped: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isDone -> Color(0xFFE8F5E9)
                isSkipped -> Color(0xFFFFEBEE)
                else -> Color.White
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = Color(0xFF2196F3),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = time,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            if (isDone) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Done",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(24.dp)
                )
            } else if (isSkipped) {
                Text(
                    text = "Terlewat",
                    fontSize = 12.sp,
                    color = Color(0xFFF44336),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

fun formatDateRange(startDate: String, endDate: String?): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))

        val start = inputFormat.parse(startDate.substringBefore("T"))
        val formattedStart = outputFormat.format(start ?: Date())

        if (endDate != null) {
            val end = inputFormat.parse(endDate.substringBefore("T"))
            val formattedEnd = outputFormat.format(end ?: Date())
            "$formattedStart - $formattedEnd"
        } else {
            formattedStart
        }
    } catch (e: Exception) {
        startDate
    }
}