package com.example.sim_mhealth.ui.onBoardingScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.sim_mhealth.R
import com.example.sim_mhealth.ui.theme.DarkGray900
import com.example.sim_mhealth.ui.theme.Gray50
import com.example.sim_mhealth.ui.theme.Gray700

@Composable
fun OnBoardingScreen1(navController: NavController) {
    Box(
        modifier = Modifier
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
                    progress = 0.33f,
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp),
                    color = Color(0xFF2196F3),
                    trackColor = Color(0xFFE0E0E0),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "1/3",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Image(
                painter = painterResource(id = R.drawable.undraw_fitness_tracker),
                contentDescription = "Welcome",
            )

            Column(Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                Text(
                    text = "Selamat datang di mHealth!",
                    style = MaterialTheme.typography.headlineMedium,
                    color = DarkGray900,
                    textAlign = TextAlign.Start,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "mHealth membantu mengelola obat, memantau tanda vital, dan memberi saran gaya hidup berbasis AI sebelum lanjut, kami perlu izin untuk menyimpan data kesehatan Anda. Jangan khawatir, semua informasi Privasi Anda penting, data hanya dipakai untuk layanan dan tidak dibagikan tanpa persetujuan.",
                    fontSize = 14.sp,
                    color = Gray700,
                    textAlign = TextAlign.Start,
                    lineHeight = 22.sp,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    BulletPoint("Menyimpan catatan kesehatan dan jadwal obat.")
                    Spacer(modifier = Modifier.height(8.dp))
                    BulletPoint("Mengirim notifikasi pengingat.")
                    Spacer(modifier = Modifier.height(8.dp))
                    BulletPoint("Menggunakan data untuk rekomendasi AI yang personal.")
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { navController.navigate("onboarding_screen_2") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Setuju & Lanjutkan",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
fun BulletPoint(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "•",
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            text = text,
            fontSize = 14.sp,
            color = Gray700,
            lineHeight = 20.sp,
            style = MaterialTheme.typography.bodyMedium

        )
    }
}

@Preview
@Composable
fun OnBoardingScreen1Preview() {
    OnBoardingScreen1(navController = rememberNavController())
}