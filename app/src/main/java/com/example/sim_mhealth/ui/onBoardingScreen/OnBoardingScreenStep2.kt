package com.example.sim_mhealth.ui.onBoardingScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.sim_mhealth.R

@Composable
fun OnBoardingScreen2(navController: NavController) {
    // Gunakan data dari OnBoardingData object
    var tanggalLahir by remember { mutableStateOf(OnBoardingData.tanggalLahir) }
    var jenisKelamin by remember { mutableStateOf(OnBoardingData.jenisKelamin) }
    var beratBadan by remember { mutableStateOf(OnBoardingData.beratBadan) }
    var tinggiBadan by remember { mutableStateOf(OnBoardingData.tinggiBadan) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Progress Indicator
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LinearProgressIndicator(
                progress = 0.66f,
                modifier = Modifier
                    .weight(1f)
                    .height(8.dp),
                color = Color(0xFF2196F3),
                trackColor = Color(0xFFE0E0E0),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "2/3",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Illustration
        Image(
            painter = painterResource(id = R.drawable.forest_jogging_group),
            contentDescription = "Health Data",
            modifier = Modifier
                .size(200.dp)
                .padding(16.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Tanggal Lahir
        Text(
            text = "Tanggal Lahir",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = tanggalLahir,
            onValueChange = { tanggalLahir = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("DD/MM/YYYY", color = Color.LightGray) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = "Date",
                    tint = Color(0xFF2196F3)
                )
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF2196F3),
                unfocusedBorderColor = Color.LightGray
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Jenis Kelamin
        Text(
            text = "Jenis Kelamin",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = { jenisKelamin = "Pria" },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (jenisKelamin == "Pria") Color(0xFF2196F3) else Color.Transparent,
                    contentColor = if (jenisKelamin == "Pria") Color.White else Color.Gray
                ),
                shape = RoundedCornerShape(12.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    width = 1.dp,
                    brush = androidx.compose.ui.graphics.SolidColor(
                        if (jenisKelamin == "Pria") Color(0xFF2196F3) else Color.LightGray
                    )
                )
            ) {
                Text("Pria")
            }

            OutlinedButton(
                onClick = { jenisKelamin = "Wanita" },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (jenisKelamin == "Wanita") Color(0xFF2196F3) else Color.Transparent,
                    contentColor = if (jenisKelamin == "Wanita") Color.White else Color.Gray
                ),
                shape = RoundedCornerShape(12.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    width = 1.dp,
                    brush = androidx.compose.ui.graphics.SolidColor(
                        if (jenisKelamin == "Wanita") Color(0xFF2196F3) else Color.LightGray
                    )
                )
            ) {
                Text("Wanita")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Berat Badan
        Text(
            text = "Berat Badan",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = beratBadan,
            onValueChange = { beratBadan = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("kg", color = Color.LightGray) },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_weight),
                    contentDescription = "Weight",
                    tint = Color(0xFF2196F3)
                )
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF2196F3),
                unfocusedBorderColor = Color.LightGray
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Tinggi Badan
        Text(
            text = "Tinggi Badan",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = tinggiBadan,
            onValueChange = { tinggiBadan = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("cm", color = Color.LightGray) },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_height),
                    contentDescription = "Height",
                    tint = Color(0xFF2196F3)
                )
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF2196F3),
                unfocusedBorderColor = Color.LightGray
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        Spacer(modifier = Modifier.weight(1f))

        // Buttons
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
                )
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
                    // Simpan data ke OnBoardingData object
                    OnBoardingData.tanggalLahir = tanggalLahir
                    OnBoardingData.jenisKelamin = jenisKelamin
                    OnBoardingData.beratBadan = beratBadan
                    OnBoardingData.tinggiBadan = tinggiBadan

                    // Navigate ke screen 3 tanpa passing data via arguments
                    navController.navigate("onboarding_screen_3")
                },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                shape = RoundedCornerShape(12.dp),
                enabled = tanggalLahir.isNotBlank() && jenisKelamin.isNotBlank() &&
                        beratBadan.isNotBlank() && tinggiBadan.isNotBlank()
            ) {
                Text(
                    text = "Lanjutkan",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}