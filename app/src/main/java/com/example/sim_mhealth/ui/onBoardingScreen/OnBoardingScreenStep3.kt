package com.example.sim_mhealth.ui.onBoardingScreen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.sim_mhealth.data.preferences.PreferencesManager
import com.example.sim_mhealth.data.repository.OnBoardingRepository
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
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

        Text(
            text = "Ada kondisi kesehatan yang perlu kami tahu?",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Memberi tahu kami membantu aplikasi memberi peringatan dan rekomendasi yang lebih aman.",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Apakah Anda memiliki riwayat kondisi medis?",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
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
                shape = RoundedCornerShape(12.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    width = 1.dp,
                    brush = androidx.compose.ui.graphics.SolidColor(
                        if (hasHealthCondition == true) Color(0xFF2196F3) else Color.LightGray
                    )
                )
            ) {
                Text("ya, ada")
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
                shape = RoundedCornerShape(12.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    width = 1.dp,
                    brush = androidx.compose.ui.graphics.SolidColor(
                        if (hasHealthCondition == false) Color(0xFF2196F3) else Color.LightGray
                    )
                )
            ) {
                Text("Tidak")
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

            OutlinedTextField(
                value = sejakKapan,
                onValueChange = { sejakKapan = it },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(color = Color.DarkGray),
                placeholder = { Text("mm/yyyy", color = Color.LightGray) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF2196F3),
                    unfocusedBorderColor = Color.LightGray
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Informasi ini tersimpan aman dan hanya digunakan untuk memberikan saran yang aman. Kami tidak membagikannya tanpa izin.",
            fontSize = 12.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    width = 1.dp,
                    brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF2196F3))
                ),
                enabled = !isLoading
            ) {
                Text(
                    text = "Kembali",
                    fontSize = 16.sp,
                    color = Color(0xFF2196F3),
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = {
                    if (hasHealthCondition == null) {
                        Toast.makeText(context, "Pilih apakah Anda memiliki kondisi medis", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (hasHealthCondition == true && jenisKondisi.isBlank()) {
                        Toast.makeText(context, "Mohon isi jenis kondisi kesehatan Anda", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    isLoading = true
                    scope.launch {
                        val token = prefsManager.getToken()
                        val userId = prefsManager.getUserId()

                        if (token == null || userId == -1) {
                            Toast.makeText(context, "Session expired, please login again", Toast.LENGTH_SHORT).show()
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
                        val dateFormatted = try {
                            val parts = tanggalLahir.split("/")
                            if (parts.size == 3) {
                                "${parts[2]}-${parts[1]}-${parts[0]}"
                            } else {
                                tanggalLahir
                            }
                        } catch (e: Exception) {
                            tanggalLahir
                        }

                        // Parse input user jadi array
                        // User input: "Diabetes, Hipertensi, Asma"
                        // Convert jadi: ["Diabetes", "Hipertensi", "Asma"]
                        val jenisKondisiArray = if (hasHealthCondition == true && jenisKondisi.isNotBlank()) {
                            jenisKondisi
                                .split(",")        // Split by comma
                                .map { it.trim() }             // Trim whitespace
                                .filter { it.isNotBlank() }    // Remove empty strings
                        } else {
                            null
                        }

                        val sejakKapanClean = if (hasHealthCondition == true && sejakKapan.isNotBlank()) {
                            sejakKapan.trim()
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
                                    Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()

                                    OnBoardingData.reset()

                                    navController.navigate("home_screen") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                } else {
                                    Toast.makeText(context, "Update gagal: ${response.message}", Toast.LENGTH_SHORT).show()
                                }
                                isLoading = false
                            },
                            onFailure = { error ->
                                Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                                isLoading = false
                            }
                        )
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                shape = RoundedCornerShape(12.dp),
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
                        color = Color.White
                    )
                }
            }
        }
    }
}