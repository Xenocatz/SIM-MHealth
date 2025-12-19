package com.example.sim_mhealth.ui.profile

import android.content.Context
import android.content.Intent
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
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.sim_mhealth.data.api.PasienDetail
import com.example.sim_mhealth.data.preferences.PreferencesManager
import com.example.sim_mhealth.data.repository.ProfileRepository
import com.example.sim_mhealth.data.service.StepsCounterService
import com.example.sim_mhealth.ui.theme.DateInputWithCalendarPicker
import com.example.sim_mhealth.utils.DateUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember { ProfileRepository() }
    val prefsManager = remember { PreferencesManager(context) }

    var pasienData by remember { mutableStateOf<PasienDetail?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    var email by remember { mutableStateOf("") }
    var tanggalLahir by remember { mutableStateOf("") }
    var beratBadan by remember { mutableStateOf("") }
    var tinggiBadan by remember { mutableStateOf("") }
    var jenisKelamin by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    val jenisKelaminOptions = listOf("Laki-laki", "Perempuan")

    LaunchedEffect(Unit) {
        val token = prefsManager.getToken()
        val userId = prefsManager.getUserId()

        if (token != null && userId != -1) {
            scope.launch {
                repository.getPasienData(token, userId).fold(
                    onSuccess = { response ->
                        pasienData = response.pasien
                        email = response.pasien.email
                        tanggalLahir = DateUtils.formatDateForDisplay(response.pasien.tanggal_lahir)
                        beratBadan = response.pasien.berat_badan?.toString() ?: ""
                        tinggiBadan = response.pasien.tinggi_badan?.toString() ?: ""
                        jenisKelamin = response.pasien.jenis_kelamin ?: ""
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
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .verticalScroll(rememberScrollState())
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
                    text = "Profil",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(Color(0xFF424242), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = Color(0xFF7E57C2),
                        modifier = Modifier.size(60.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = pasienData?.username ?: "User",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Text(
                    text = "Alamat Email",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = Color.DarkGray),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF2196F3),
                        unfocusedBorderColor = Color(0xFFE0E0E0)
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Tanggal Lahir",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                DateInputWithCalendarPicker(
                    selectedDate = tanggalLahir,
                    onDateSelected = { newDate ->
                        tanggalLahir = newDate
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Berat Badan",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = beratBadan,
                            onValueChange = { beratBadan = it },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = TextStyle(color = Color.DarkGray),
                            placeholder = { Text("65Kg") },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF2196F3),
                                unfocusedBorderColor = Color(0xFFE0E0E0)
                            ),
                            singleLine = true
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Tinggi Badan",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = tinggiBadan,
                            onValueChange = { tinggiBadan = it },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = TextStyle(color = Color.DarkGray),
                            placeholder = { Text("170cm") },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF2196F3),
                                unfocusedBorderColor = Color(0xFFE0E0E0)
                            ),
                            singleLine = true
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Jenis Kelamin",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = jenisKelamin,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        textStyle = TextStyle(color = Color.DarkGray),
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2196F3),
                            unfocusedBorderColor = Color(0xFFE0E0E0)
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        jenisKelaminOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    jenisKelamin = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        val token = prefsManager.getToken()
                        val userId = prefsManager.getUserId()

                        if (token != null && userId != -1) {
                            scope.launch {
                                val dateFormatted = try {
                                    val parts = tanggalLahir.split("-")
                                    if (parts.size == 3) {
                                        "${parts[2]}-${parts[1]}-${parts[0]}"
                                    } else {
                                        tanggalLahir
                                    }
                                } catch (e: Exception) {
                                    tanggalLahir
                                }

                                repository.updateProfile(
                                    token = token,
                                    userId = userId,
                                    email = email,
                                    tanggalLahir = dateFormatted,
                                    beratBadan = beratBadan.toFloatOrNull(),
                                    tinggiBadan = tinggiBadan.toFloatOrNull(),
                                    jenisKelamin = jenisKelamin
                                ).fold(
                                    onSuccess = { response ->
                                        Toast.makeText(
                                            context,
                                            response.message,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        pasienData = response.pasien
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
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudUpload,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Simpan",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        prefsManager.logout()
                        navController.navigate("intro_screen") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Logout",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

private fun stopStepTracking(context: Context) {
    val intent = Intent(context, StepsCounterService::class.java)
    context.stopService(intent)
}