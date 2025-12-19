package com.example.sim_mhealth.ui.onBoardingScreen

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.sim_mhealth.data.preferences.PreferencesManager
import com.example.sim_mhealth.data.repository.OnBoardingRepository
import com.example.sim_mhealth.ui.theme.DateInputWithCalendarPicker
import com.example.sim_mhealth.ui.theme.Gray200
import com.example.sim_mhealth.ui.theme.Gray50
import com.example.sim_mhealth.ui.theme.Gray700
import com.example.sim_mhealth.ui.theme.Primary600
import com.example.sim_mhealth.ui.theme.SIMMHealthTheme
import com.example.sim_mhealth.utils.DateUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnBoardingScreen3(
    navController: NavController
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember { OnBoardingRepository() }
    val prefsManager = remember { PreferencesManager(context) }

    var hasHealthCondition by remember { mutableStateOf(OnBoardingData.hasHealthCondition) }
    var jenisKondisi by remember { mutableStateOf("") }
    var sejakKapan by remember { mutableStateOf(OnBoardingData.sejakKapan) }
    var isLoading by remember { mutableStateOf(false) }

    Box(
        Modifier
            .fillMaxSize()
            .background(Gray50)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LinearProgressIndicator(
                    progress = 1f,
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp),
                    color = Color(0xFF2196F3),
                    trackColor = Color(0xFFE0E0E0),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "3/3",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Column(Modifier.padding(16.dp)) {
                Text(
                    text = "Ada kondisi kesehatan yang perlu kami tahu?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Gray700,
                    style = MaterialTheme.typography.displayLarge
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Memberi tahu kami membantu aplikasi memberi peringatan dan rekomendasi yang lebih aman.",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Apakah Anda memiliki riwayat kondisi medis?",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Gray700,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { hasHealthCondition = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (hasHealthCondition == true) Color(0xFF2196F3) else Color.Transparent,
                            contentColor = if (hasHealthCondition == true) Color.White else Color.Gray
                        ),
                        shape = CircleShape,
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            width = 1.dp,
                            brush = androidx.compose.ui.graphics.SolidColor(
                                if (hasHealthCondition == true) Color(0xFF2196F3) else Color.LightGray
                            )
                        )
                    ) {
                        Text(text = "ya, ada", style = MaterialTheme.typography.bodyLarge)
                    }

                    OutlinedButton(
                        onClick = {
                            hasHealthCondition = false
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (hasHealthCondition == false) Color(0xFF2196F3) else Color.Transparent,
                            contentColor = if (hasHealthCondition == false) Color.White else Color.Gray
                        ),
                        shape = CircleShape,
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            width = 1.dp,
                            brush = androidx.compose.ui.graphics.SolidColor(
                                if (hasHealthCondition == false) Color(0xFF2196F3) else Color.LightGray
                            )
                        )
                    ) {
                        Text(text = "Tidak", style = MaterialTheme.typography.bodyLarge)
                    }
                }

                if (hasHealthCondition == true) {
                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Jenis kondisi?*",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "Tuliskan kondisi kesehatan Anda (pisahkan dengan koma jika lebih dari satu)",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.fillMaxWidth(),
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = jenisKondisi,
                        onValueChange = { jenisKondisi = it },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(color = Color.DarkGray),
                        placeholder = {
                            Text(
                                "Contoh: Diabetes, Hipertensi, Asma",
                                color = Color.LightGray,
                                fontSize = 14.sp
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2196F3),
                            unfocusedBorderColor = Color.LightGray
                        ),
                        minLines = 3,
                        maxLines = 5
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Tips: Pisahkan setiap kondisi dengan koma (,)",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        modifier = Modifier.fillMaxWidth(),
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Sejak kapan?",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "mm/yyyy",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    DateInputWithCalendarPicker(
                        selectedDate = sejakKapan,
                        onDateSelected = { newDate ->
                            sejakKapan = newDate
                        }
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "Informasi ini tersimpan aman dan hanya digunakan untuk memberikan saran yang aman. Kami tidak membagikannya tanpa izin.",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = CircleShape,
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            width = 1.dp,
                            brush = androidx.compose.ui.graphics.SolidColor(Gray700)
                        ),
                        enabled = !isLoading
                    ) {
                        Text(
                            text = "Kembali",
                            fontSize = 16.sp,
                            color = Gray700,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    Button(
                        onClick = {
                            if (hasHealthCondition == null) {
                                Toast.makeText(
                                    context,
                                    "Pilih apakah Anda memiliki kondisi medis",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@Button
                            }

                            if (hasHealthCondition == true && jenisKondisi.isBlank()) {
                                Toast.makeText(
                                    context,
                                    "Mohon isi jenis kondisi kesehatan Anda",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@Button
                            }

                            isLoading = true
                            scope.launch {
                                val token = prefsManager.getToken()
                                val userId = prefsManager.getUserId()

                                if (token == null || userId == -1) {
                                    Toast.makeText(
                                        context,
                                        "Session expired, please login again",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    navController.navigate("login_screen") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                    return@launch
                                }

                                val tanggalLahir = OnBoardingData.tanggalLahir
                                val jenisKelamin = OnBoardingData.jenisKelamin
                                val beratBadan = OnBoardingData.beratBadan
                                val tinggiBadan = OnBoardingData.tinggiBadan

                                // Convert date format DD/MM/YYYY to YYYY-MM-DD
                                val dateFormatted =
                                    DateUtils.convertToISO8601(tanggalLahir) ?: tanggalLahir

                                // Parse input user jadi array
                                // User input: "Diabetes, Hipertensi, Asma"
                                // Convert jadi: ["Diabetes", "Hipertensi", "Asma"]
                                val jenisKondisiArray =
                                    if (hasHealthCondition == true && jenisKondisi.isNotBlank()) {
                                        jenisKondisi
                                            .split(",")        // Split by comma
                                            .map { it.trim() }             // Trim whitespace
                                            .filter { it.isNotBlank() }    // Remove empty strings
                                    } else {
                                        null
                                    }

                                val sejakKapanClean =
                                    if (hasHealthCondition == true && sejakKapan.isNotBlank()) {
                                        DateUtils.convertToISO8601(sejakKapan)
                                    } else {
                                        null
                                    }

                                repository.updateProfile(
                                    token = token,
                                    idPasien = userId,
                                    tanggalLahir = dateFormatted,
                                    tinggiBadan = tinggiBadan.toFloatOrNull(),
                                    beratBadan = beratBadan.toFloatOrNull(),
                                    jenisKelamin = jenisKelamin,
                                    jenisKondisi = jenisKondisiArray,
                                    sejakKapan = sejakKapanClean
                                ).fold(
                                    onSuccess = { response ->
                                        if (response.success) {
                                            Toast.makeText(
                                                context,
                                                response.message,
                                                Toast.LENGTH_SHORT
                                            )
                                                .show()

                                            OnBoardingData.reset()
                                            prefsManager.setOnboardingCompleted(true)

                                            prefsManager.setOnboardingCompleted(true)
                                            navController.navigate("home_screen") {
                                                popUpTo(0) { inclusive = true }
                                            }
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "Update gagal: ${response.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        isLoading = false
                                    },
                                    onFailure = { error ->
                                        Toast.makeText(
                                            context,
                                            "Error: ${error.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        isLoading = false
                                    }
                                )
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Primary600,
                            disabledContainerColor = Gray200
                        ),
                        shape = CircleShape,
                        enabled = hasHealthCondition != null && !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White
                            )
                        } else {
                            Text(
                                text = "Simpan & Selesai",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Gray50,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Preview
@Composable
fun OnBoardingScreenStep3Preview() {
    SIMMHealthTheme {
        OnBoardingScreen3(navController = rememberNavController())
    }
}