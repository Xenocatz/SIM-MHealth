package com.example.sim_mhealth.ui.dashboard

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.sim_mhealth.R
import com.example.sim_mhealth.data.api.PasienDetail
import com.example.sim_mhealth.data.api.PengingatItem
import com.example.sim_mhealth.data.preferences.PreferencesManager
import com.example.sim_mhealth.data.repository.DashboardRepository
import com.example.sim_mhealth.data.repository.ReminderRepository
import com.example.sim_mhealth.data.repository.StepsRepository
import kotlinx.coroutines.delay
import com.example.sim_mhealth.ui.theme.Gray700
import com.example.sim_mhealth.ui.theme.SIMMHealthTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow

@Composable
fun DashboardScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember { DashboardRepository() }
    val reminderRepository = remember { ReminderRepository() }
    val stepsRepository = remember { StepsRepository() }
    val prefsManager = remember { PreferencesManager(context) }

    var pasienData by remember { mutableStateOf<PasienDetail?>(null) }
    var nextReminder by remember { mutableStateOf<PengingatItem?>(null) }

    val userId = prefsManager.getUserId()
    val prefsName = "steps_prefs_user_$userId"
    val prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
    var currentSteps by remember { 
		val userId = prefsManager.getUserId()
		val prefsName = if (userId != -1) "steps_prefs_user_$userId" else "steps_prefs_default"
		val prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
		mutableIntStateOf(prefs.getInt("current_steps", 0)) 
	}
    var targetSteps by remember { mutableIntStateOf(8000) }
    var calories by remember { mutableIntStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        while (true) {
            val userId = prefsManager.getUserId()
            if (userId != -1) {
                val prefsName = "steps_prefs_user_$userId"
                val prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
                currentSteps = prefs.getInt("current_steps", 0)
            }
            calories = (currentSteps * 0.04).toInt()
            delay(1000)
        }
    }


    LaunchedEffect(Unit) {
        val token = prefsManager.getToken()
        val userId = prefsManager.getUserId()

        if (token != null && userId != -1) {
            scope.launch {
                repository.getPasienData(token, userId).fold(
                    onSuccess = { response ->
                        pasienData = response.pasien
                    },
                    onFailure = { error ->
                        Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                )

                reminderRepository.getPengingatByPasien(token, userId).fold(
                    onSuccess = { response ->
                        nextReminder = response.pengingat.firstOrNull()
                    },
                    onFailure = { }
                )

                isLoading = false
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
            CircularProgressIndicator(color = Color(0xFF2196F3))
        }
    } else {
        DashboardContent(
            navController = navController,
            pasienData = pasienData,
            nextReminder = nextReminder,
            username = prefsManager.getUsername() ?: "User",
            currentSteps = currentSteps,
            targetSteps = targetSteps,
            calories = calories
        )
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun DashboardContent(
    navController: NavController,
    pasienData: PasienDetail?,
    nextReminder: PengingatItem?,
    username: String,
    currentSteps: Int,
    targetSteps: Int,
    calories: Int
) {
    val beratBadan = pasienData?.berat_badan ?: 0f
    val tinggiBadan = pasienData?.tinggi_badan ?: 0f
    val bmi = calculateBMI(beratBadan, tinggiBadan)
    val bmiStatus = getBMIStatus(bmi)
    val bmiColor = getBMIColor(bmi)

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
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Haloo, $username👋",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Gray
                )
                IconButton(onClick = { navController.navigate("notification_screen") }) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = Color.Black
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clickable { navController.navigate("track_screen") },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF2196F3),
                                Color(0xFF64B5F6)
                            )
                        )
                    )
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Langkahmu hari ini:",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                text = currentSteps.toString(),
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = " / $targetSteps",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "langkah",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Image(
                            painter = painterResource(id = R.drawable.undraw_jogging),
                            contentDescription = "Walking",
                            modifier = Modifier.fillMaxWidth(),
                            contentScale = ContentScale.Fit
                        )
                    }

                    Card(
                        modifier = Modifier
                            .width(150.dp)
                            .height(140.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "1200kkal",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "🔥",
                                    fontSize = 28.sp
                                )
                                Text(
                                    text = currentSteps.toString(),
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Tetap bergerak,\nritmenya bagus.",
                                fontSize = 11.sp,
                                color = Gray700,
                                lineHeight = 14.sp,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier.padding(horizontal = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Status kamu saat ini",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = getCurrentDate(),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF64B5F6),
                                Color(0xFF4DB6AC)
                            )
                        )
                    )
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatusCardFigma(
                            icon = "😴",
                            title = "Pola tidur",
                            value = "7",
                            unit = "jam",
                            maxValue = "/8jam",
                            status = "bagus",
                            statusColor = Color(0xFF4CAF50),
                            modifier = Modifier.weight(1f)
                        )

                        StatusCardFigma(
                            icon = "⚖️",
                            title = "BMI",
                            value = if (bmi > 0) String.format("%.0f", bmi) else "0",
                            unit = "",
                            maxValue = "",
                            status = bmiStatus,
                            statusColor = bmiColor,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val bmi = remember(beratBadan, tinggiBadan) {
                        if (beratBadan > 0 && tinggiBadan > 0) hitungBMI(beratBadan, tinggiBadan) else 0.0
                    }

                    val kategoriBMI = remember(bmi) { getKategoriBMI(bmi.toFloat()) }
                    val statusColor = remember(bmi) { getStatusBMIColor(bmi.toFloat()) }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatusCardFigma(
                            icon = "⚖",
                            title = "Berat badan",
                            value = if (beratBadan > 0) beratBadan.toInt().toString() else "0",
                            unit = "kg",
                            maxValue = kategoriBMI,
                            status = "",
                            statusColor = statusColor,
                            modifier = Modifier.weight(1f)
                        )

                        StatusCardFigma(
                            icon = "📏",
                            title = "Tinggi badan",
                            value = if (tinggiBadan > 0) tinggiBadan.toInt().toString() else "0",
                            unit = "cm",
                            maxValue = kategoriBMI,
                            status = "",
                            statusColor = statusColor,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Konsumsi obat selanjutnya",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        nextReminder?.let { reminder ->
            MedicationCardFigma(reminder = reminder)
        } ?: run {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Tidak ada pengingat obat",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
fun StatusCardFigma(
    icon: String,
    title: String,
    value: String,
    unit: String,
    maxValue: String,
    status: String,
    statusColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = icon,
                    fontSize = 24.sp
                )
                Text(
                    text = status,
                    fontSize = 10.sp,
                    color = statusColor,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .background(
                            color = statusColor.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

            Column {
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = value,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = unit,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    if (maxValue.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = maxValue,
                            fontSize = 11.sp,
                            color = Color(0xFFFFA726),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MedicationCardFigma(reminder: PengingatItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color(0xFF4DB6AC),
                    Color(0xFF64B5F6)
                )
            )
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFE91E63).copy(alpha = 0.2f),
                                    Color(0xFFFF5722).copy(alpha = 0.2f)
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "💊", fontSize = 24.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = reminder.nama_obat,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "${reminder.dosis_kuantitas.toInt()} ${reminder.dosis_unit} - ${reminder.catatan ?: "setelah makan"}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
            Text(
                text = reminder.waktu_alarm.firstOrNull() ?: "00:00",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

fun calculateBMI(weight: Float, height: Float): Float {
    if (weight <= 0 || height <= 0) return 0f
    val heightInMeters = height / 100
    return weight / (heightInMeters.pow(2))
}

fun getBMIStatus(bmi: Float): String {
    return when {
        bmi == 0f -> "N/A"
        bmi < 18.5 -> "kurus"
        bmi < 25 -> "ideal"
        bmi < 30 -> "gemuk"
        else -> "obesitas"
    }
}

fun getBMIColor(bmi: Float): Color {
    return when {
        bmi == 0f -> Color.Gray
        bmi < 18.5 -> Color(0xFFFFA726)
        bmi < 25 -> Color(0xFF4CAF50)
        bmi < 30 -> Color(0xFFFFA726)
        else -> Color(0xFFF44336)
    }
}

fun getCurrentDate(): String {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.forLanguageTag("id-ID"))
    return dateFormat.format(Date())
}

fun hitungBMI(beratKg: Float, tinggiCm: Float): Double {
    val tinggiM = tinggiCm / 100.0
    return beratKg / (tinggiM * tinggiM)
}

fun getKategoriBMI(bmi: Float): String {
    return when {
        bmi < 18.5 -> "Kurang berat badan"
        bmi < 25.0 -> "Normal"
        bmi < 30.0 -> "Kelebihan berat badan"
        else -> "Obesitas"
    }
}

fun getStatusBMIColor(bmi: Float): Color {
    return when {
        bmi < 18.5 -> Color(0xFF2196F3)
        bmi < 25.0 -> Color(0xFF4CAF50)
        bmi < 30.0 -> Color(0xFFFFA726)
        else -> Color(0xFFF44336)
    }
}

@Preview
@Composable
fun DashboardScreenPreview() {
    SIMMHealthTheme {
        DashboardScreen(navController = rememberNavController())
    }
}
