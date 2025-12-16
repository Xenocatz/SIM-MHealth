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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.sim_mhealth.data.api.PengingatDetail
import com.example.sim_mhealth.data.api.UpdatePengingatRequest
import com.example.sim_mhealth.data.preferences.PreferencesManager
import com.example.sim_mhealth.data.repository.ReminderRepository
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

fun convertIsoToDisplayDate(isoDateString: String?): String {
    if (isoDateString.isNullOrBlank()) return ""
    return try {
        val isoDate = LocalDate.parse(isoDateString.substringBefore("T"))
        val displayFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale("id", "ID"))
        isoDate.format(displayFormatter)
    } catch (e: DateTimeParseException) {
        e.printStackTrace()
        isoDateString.substringBefore("T")
    }
}

fun convertDisplayToIsoDate(displayDateString: String?): String? {
    if (displayDateString.isNullOrBlank()) return null
    return try {
        val displayFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale("id", "ID"))
        val localDate = LocalDate.parse(displayDateString, displayFormatter)
        localDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
    } catch (e: DateTimeParseException) {
        e.printStackTrace()
        null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditReminderScreen(navController: NavController, reminderId: Int) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember { ReminderRepository() }
    val prefsManager = remember { PreferencesManager(context) }

    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }

    // Form fields
    var namaObat by remember { mutableStateOf("") }
    var dosisKuantitas by remember { mutableStateOf("") }
    var dosisUnit by remember { mutableStateOf("ml") }
    var tanggalMulai by remember { mutableStateOf("") }
    var tanggalAkhir by remember { mutableStateOf("") }
    var frekuensi by remember { mutableStateOf("") }
    var waktuAlarm by remember { mutableStateOf(mutableListOf<String>()) }
    var catatan by remember { mutableStateOf("") }
    var stokAwal by remember { mutableStateOf("") }

    var expandedUnit by remember { mutableStateOf(false) }
    val dosisUnitOptions = listOf("ml", "tablet", "kapsul", "mg", "tetes")

    // Load existing data
    LaunchedEffect(reminderId) {
        val token = prefsManager.getToken()
        if (token != null) {
            scope.launch {
                repository.getPengingatById(token, reminderId).fold(
                    onSuccess = { response ->
                        val detail = response.pengingat
                        namaObat = detail.nama_obat
                        dosisKuantitas = detail.dosis_kuantitas.toString()
                        dosisUnit = detail.dosis_unit
                        tanggalMulai = convertIsoToDisplayDate(detail.tanggal_mulai)
                        tanggalAkhir = convertIsoToDisplayDate(detail.tanggal_akhir)
                        frekuensi = detail.frekuensi
                        waktuAlarm = detail.waktu_alarm.toMutableList()
                        catatan = detail.catatan ?: ""
                        stokAwal = detail.stok_awal?.toString() ?: ""
                        isLoading = false
                    },
                    onFailure = {
                        Toast.makeText(context, "Gagal memuat data", Toast.LENGTH_SHORT).show()
                        isLoading = false
                    }
                )
            }
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            // Header
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
                    text = "Edit Jadwal",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.Center)
                )
                TextButton(
                    onClick = {
                        isSaving = true
                        val token = prefsManager.getToken()
                        if (token != null) {
                            val isoTanggalMulai = convertDisplayToIsoDate(tanggalMulai)
                            val isoTanggalAkhir = convertDisplayToIsoDate(tanggalAkhir.ifBlank { null })

                            if (isoTanggalMulai == null) {
                                Toast.makeText(context, "Format Tanggal Mulai tidak valid (DD-MM-YYYY)", Toast.LENGTH_LONG).show()
                                isSaving = false
                                return@TextButton
                            }
                            scope.launch {
                                val request = UpdatePengingatRequest(
                                    nama_obat = namaObat,
                                    dosis_kuantitas = dosisKuantitas.toFloatOrNull(),
                                    dosis_unit = dosisUnit,
                                    frekuensi = frekuensi,
                                    tanggal_mulai = isoTanggalMulai,
                                    tanggal_akhir = isoTanggalAkhir,
                                    catatan = catatan.ifBlank { null },
                                    waktu_alarm = waktuAlarm,
                                    stok_awal = stokAwal.toIntOrNull()
                                )

                                repository.updatePengingat(token, reminderId, request).fold(
                                    onSuccess = {
                                        Toast.makeText(context, "Berhasil diupdate", Toast.LENGTH_SHORT).show()
                                        navController.popBackStack()
                                    },
                                    onFailure = { error ->
                                        val msg = error.message ?: "Gagal memperbarui pengingat."
                                        Toast.makeText(context, "Error: $msg", Toast.LENGTH_LONG).show()
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
                            fontWeight = FontWeight.Bold
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
                // Medicine Icon
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(Color(0xFFE0E0E0), CircleShape)
                        .align(Alignment.CenterHorizontally),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Color(0xFF2196F3),
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Nama Obat
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

                // Dosis
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
                    OutlinedTextField(
                        value = tanggalMulai,
                        onValueChange = { tanggalMulai = it },
                        modifier = Modifier.weight(1f),
                        textStyle = TextStyle(color = Color.DarkGray),
                        placeholder = { Text("dd-MM-yyyy") },
                        leadingIcon = {
                            Icon(Icons.Default.DateRange, null, tint = Color(0xFF2196F3))
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2196F3),
                            unfocusedBorderColor = Color(0xFFE0E0E0)
                        ),
                        singleLine = true
                    )

                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .size(20.dp),
                        tint = Color.Gray
                    )

                    OutlinedTextField(
                        value = tanggalAkhir,
                        onValueChange = { tanggalAkhir = it },
                        modifier = Modifier.weight(1f),
                        textStyle = TextStyle(color = Color.DarkGray),
                        placeholder = { Text("dd-MM-yyyy") },
                        leadingIcon = {
                            Icon(Icons.Default.DateRange, null, tint = Color(0xFF2196F3))
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2196F3),
                            unfocusedBorderColor = Color(0xFFE0E0E0)
                        ),
                        singleLine = true
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

                    TextButton(
                        onClick = {
                            waktuAlarm.add("00:00")
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Color(0xFF2196F3),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Tambah",
                            color = Color(0xFF2196F3),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                waktuAlarm.forEachIndexed { index, time ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = time,
                            onValueChange = { newTime ->
                                waktuAlarm[index] = newTime
                            },
                            modifier = Modifier.weight(1f),
                            textStyle = TextStyle(color = Color.DarkGray),
                            placeholder = { Text("--:--") },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF2196F3),
                                unfocusedBorderColor = Color(0xFFE0E0E0)
                            ),
                            singleLine = true
                        )

                        if (waktuAlarm.size > 1) {
                            IconButton(
                                onClick = { waktuAlarm.removeAt(index) }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove",
                                    tint = Color(0xFFF44336)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
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
    }
}