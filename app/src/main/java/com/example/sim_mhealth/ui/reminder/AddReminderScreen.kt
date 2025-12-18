package com.example.sim_mhealth.ui.reminder

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.sim_mhealth.data.api.CreatePengingatRequest
import com.example.sim_mhealth.data.preferences.PreferencesManager
import com.example.sim_mhealth.data.repository.ReminderRepository
import com.example.sim_mhealth.ui.theme.DateInputWithCalendarPicker
import com.example.sim_mhealth.ui.theme.hindMadurai
import com.example.sim_mhealth.ui.theme.martel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReminderScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember { ReminderRepository() }
    val prefsManager = remember { PreferencesManager(context) }

    var isSaving by remember { mutableStateOf(false) }

    var namaObat by remember { mutableStateOf("") }
    var dosisKuantitas by remember { mutableStateOf("") }
    var dosisUnit by remember { mutableStateOf("tablet") }
    var tanggalMulai by remember { mutableStateOf("") }
    var tanggalAkhir by remember { mutableStateOf("") }
    var frekuensi by remember { mutableStateOf("3x Sehari") }
    val waktuAlarm = remember { mutableStateListOf("08:00") }
    var catatan by remember { mutableStateOf("") }
    var stokAwal by remember { mutableStateOf("") }

    var expandedUnit by remember { mutableStateOf(false) }
    var expandedFrekuensi by remember { mutableStateOf(false) }

    var showTimePicker by remember { mutableStateOf(false) }
    var selectedTimeIndex by remember { mutableStateOf(-1) }
    var initialHour by remember { mutableStateOf(8) }
    var initialMinute by remember { mutableStateOf(0) }
    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true
    )

    val dosisUnitOptions = listOf("tablet", "kapsul", "ml", "mg", "tetes")
    val frekuensiOptions =
        listOf("1x Sehari", "2x Sehari", "3x Sehari", "4x Sehari", "Sesuai kebutuhan")

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
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.Black
                )
            }
            Text(
                text = "Tambah Jadwal",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.align(Alignment.Center)
            )
            TextButton(
                onClick = {
                    when {
                        namaObat.isBlank() -> {
                            Toast.makeText(context, "Nama obat harus diisi", Toast.LENGTH_SHORT)
                                .show()
                            return@TextButton
                        }

                        dosisKuantitas.isBlank() -> {
                            Toast.makeText(context, "Dosis harus diisi", Toast.LENGTH_SHORT).show()
                            return@TextButton
                        }

                        tanggalMulai.isBlank() -> {
                            Toast.makeText(context, "Tanggal mulai harus diisi", Toast.LENGTH_SHORT)
                                .show()
                            return@TextButton
                        }

                        waktuAlarm.isEmpty() -> {
                            Toast.makeText(context, "Waktu minimal 1", Toast.LENGTH_SHORT).show()
                            return@TextButton
                        }
                    }

                    isSaving = true
                    val token = prefsManager.getToken()
                    val userId = prefsManager.getUserId()

                    if (token != null && userId != -1) {
                        scope.launch {
                            val request = CreatePengingatRequest(
                                id_pasien = userId,
                                nama_obat = namaObat,
                                dosis_kuantitas = dosisKuantitas.toFloatOrNull() ?: 1f,
                                dosis_unit = dosisUnit,
                                frekuensi = frekuensi,
                                tanggal_mulai = tanggalMulai,
                                tanggal_akhir = tanggalAkhir.ifBlank { null },
                                catatan = catatan.ifBlank { null },
                                waktu_alarm = waktuAlarm.filter { it.isNotBlank() },
                                stok_awal = stokAwal.toIntOrNull()
                            )

                            repository.createPengingat(token, request).fold(
                                onSuccess = {
                                    Toast.makeText(
                                        context,
                                        "Pengingat berhasil ditambahkan",
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
                                    isSaving = false
                                }
                            )
                        }
                    }
                },
                modifier = Modifier.align(Alignment.CenterEnd),
                enabled = !isSaving
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color(0xFF2196F3)
                    )
                } else {
                    Text(
                        text = "Simpan",
                        color = Color(0xFF2196F3),
                        fontFamily = hindMadurai,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color(0xFFE0E0E0), CircleShape)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = Color(0xFF9E9E9E),
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = "+",
                        fontSize = 24.sp,
                        color = Color(0xFF2196F3),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Nama Obat*",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = namaObat,
                onValueChange = { namaObat = it },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(color = Color.DarkGray),
                placeholder = { Text("cth, paracetamol...") },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF2196F3),
                    unfocusedBorderColor = Color(0xFFE0E0E0)
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Dosis*",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = dosisKuantitas,
                    onValueChange = { dosisKuantitas = it },
                    modifier = Modifier.weight(1f),
                    textStyle = TextStyle(color = Color.DarkGray),
                    placeholder = { Text("cth, 12...") },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF2196F3),
                        unfocusedBorderColor = Color(0xFFE0E0E0)
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )

                ExposedDropdownMenuBox(
                    expanded = expandedUnit,
                    onExpandedChange = { expandedUnit = !expandedUnit },
                    modifier = Modifier.width(120.dp)
                ) {
                    OutlinedTextField(
                        value = dosisUnit,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        textStyle = TextStyle(color = Color.DarkGray),
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedUnit)
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2196F3),
                            unfocusedBorderColor = Color(0xFFE0E0E0)
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expandedUnit,
                        onDismissRequest = { expandedUnit = false }
                    ) {
                        dosisUnitOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    dosisUnit = option
                                    expandedUnit = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Frekuensi*",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            ExposedDropdownMenuBox(
                expanded = expandedFrekuensi,
                onExpandedChange = { expandedFrekuensi = !expandedFrekuensi }
            ) {
                OutlinedTextField(
                    value = frekuensi,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    textStyle = TextStyle(color = Color.DarkGray),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedFrekuensi)
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF2196F3),
                        unfocusedBorderColor = Color(0xFFE0E0E0)
                    )
                )
                ExposedDropdownMenu(
                    expanded = expandedFrekuensi,
                    onDismissRequest = { expandedFrekuensi = false }
                ) {
                    frekuensiOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                frekuensi = option
                                expandedFrekuensi = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Periode*",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DateInputWithCalendarPicker(
                    selectedDate = tanggalMulai,
                    onDateSelected = { newDate ->
                        tanggalMulai = newDate
                    },
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .size(20.dp),
                    tint = Color.Gray
                )

                DateInputWithCalendarPicker(
                    selectedDate = tanggalAkhir,
                    onDateSelected = { newDate ->
                        tanggalAkhir = newDate
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Waktu*",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )

                IconButton(
                    onClick = {
                        waktuAlarm.add("00:00")
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Time",
                        tint = Color(0xFF2196F3),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            waktuAlarm.forEachIndexed { index, time ->
                key(index) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = time,
                            onValueChange = { },
                            modifier = Modifier
                                .weight(1f)
                                .clickable() {
                                    selectedTimeIndex = index
                                    val parts = time.split(":")
                                    if (parts.size == 2) {
                                        initialHour = parts[0].toIntOrNull() ?: 8
                                        initialMinute = parts[1].toIntOrNull() ?: 0
                                    }
                                    showTimePicker = true
                                },
                            enabled = false,
                            textStyle = TextStyle(
                                color = Color.DarkGray,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            placeholder = { Text("--:--") },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledBorderColor = Color(0xFFE0E0E0),
                                disabledTextColor = Color.DarkGray,
                                disabledPlaceholderColor = Color.Gray,
                                disabledTrailingIconColor = Color(0xFF2196F3),
                                focusedBorderColor = Color(0xFF2196F3),
                                unfocusedBorderColor = Color(0xFFE0E0E0)
                            ),
                            singleLine = true,
                            readOnly = true,
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.AccessTime,
                                    contentDescription = "Pilih Waktu",
                                    tint = Color(0xFF2196F3)
                                )
                            }
                        )

                        if (waktuAlarm.size > 1) {
                            IconButton(
                                onClick = {
                                    waktuAlarm.removeAt(index)
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Hapus Waktu",
                                    tint = Color(0xFFE57373)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Catatan",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = catatan,
                onValueChange = { catatan = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                textStyle = TextStyle(color = Color.DarkGray),
                placeholder = { Text("Tambahkan catatan (opsional)") },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF2196F3),
                    unfocusedBorderColor = Color(0xFFE0E0E0)
                ),
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(100.dp))
        }
    }

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            containerColor = Color.White,
            title = {
                Text(
                    text = "Pilih Waktu Alarm",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    TimePicker(
                        state = timePickerState,
                        colors = TimePickerDefaults.colors(
                            clockDialColor = Color(0xFFE3F2FD),
                            clockDialSelectedContentColor = Color.White,
                            clockDialUnselectedContentColor = Color.DarkGray,
                            selectorColor = Color(0xFF2196F3),
                            timeSelectorSelectedContainerColor = Color(0xFF2196F3),
                            timeSelectorUnselectedContainerColor = Color(0xFFE0E0E0),
                            timeSelectorSelectedContentColor = Color.White,
                            timeSelectorUnselectedContentColor = Color.DarkGray
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (selectedTimeIndex >= 0) {
                            val hour = timePickerState.hour.toString().padStart(2, '0')
                            val minute = timePickerState.minute.toString().padStart(2, '0')
                            waktuAlarm[selectedTimeIndex] = "$hour:$minute"
                        }
                        showTimePicker = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2196F3)
                    )
                ) {
                    Text("Simpan", fontFamily = martel)
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text(
                        "Batal",
                        color = Color.Gray,
                        fontFamily = martel
                    )
                }
            }
        )
    }
}

@Preview
@Composable
fun AddReminderScreenPreview() {
    AddReminderScreen(navController = rememberNavController())
}
