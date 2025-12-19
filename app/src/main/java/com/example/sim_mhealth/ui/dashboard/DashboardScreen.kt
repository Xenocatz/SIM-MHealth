package com.example.sim_mhealth.ui.dashboard

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.sim_mhealth.R
import com.example.sim_mhealth.data.api.PasienDetail
import com.example.sim_mhealth.data.api.PengingatItem
import com.example.sim_mhealth.data.preferences.PreferencesManager
import com.example.sim_mhealth.data.repository.DashboardRepository
import com.example.sim_mhealth.data.repository.ReminderRepository
import com.example.sim_mhealth.ui.components.SleepTimerDialog
import com.example.sim_mhealth.ui.theme.Gray700
import com.example.sim_mhealth.ui.theme.SIMMHealthTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.pow

private const val STEPS_POLL_INTERVAL_MS = 5000L
private const val CALORIES_PER_STEP = 0.04
private const val DEFAULT_TARGET_STEPS = 8000
private const val RECOMMENDED_SLEEP_HOURS = 8

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun DashboardScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember { DashboardRepository() }
    val reminderRepository = remember { ReminderRepository() }
    val prefsManager = remember { PreferencesManager(context) }
    val lifecycleOwner = LocalLifecycleOwner.current

    var pasienData by remember { mutableStateOf<PasienDetail?>(null) }
    var nextReminder by remember { mutableStateOf<PengingatItem?>(null) }
    var refreshTrigger by remember { mutableIntStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val userId = prefsManager.getUserId()
    val prefs = remember(userId) {
        val prefsName = if (userId != -1) {
            "steps_prefs_user_$userId"
        } else {
            "steps_prefs_default"
        }
        context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
    }

    var currentSteps by remember { mutableIntStateOf(prefs.getInt("current_steps", 0)) }
    val targetSteps = DEFAULT_TARGET_STEPS
    val calories by remember { derivedStateOf { (currentSteps * CALORIES_PER_STEP).toInt() } }

    LaunchedEffect(Unit) {
        while (true) {
            currentSteps = prefs.getInt("current_steps", 0)
            delay(STEPS_POLL_INTERVAL_MS)
        }
    }

    LaunchedEffect(refreshTrigger) {
        val token = prefsManager.getToken()
        val userId = prefsManager.getUserId()

        if (token != null && userId != -1) {
            isLoading = true
            errorMessage = null

            scope.launch {
                repository.getPasienData(token, userId).fold(
                    onSuccess = { response ->
                        pasienData = response.pasien
                        errorMessage = null
                    },
                    onFailure = { error ->
                        errorMessage = "Gagal memuat data: ${error.message}"
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
            errorMessage = "Session tidak valid"
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                refreshTrigger++
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    when {
        isLoading -> LoadingScreen()
        errorMessage != null -> ErrorScreen(
            message = errorMessage!!,
            onRetry = { refreshTrigger++ }
        )

        else -> DashboardContent(
            navController = navController,
            pasienData = pasienData,
            nextReminder = nextReminder,
            username = prefsManager.getUsername() ?: "User",
            currentSteps = currentSteps,
            targetSteps = targetSteps,
            calories = calories,
            prefsManager = prefsManager
        )
    }
}

@RequiresApi(Build.VERSION_CODES.S)
@SuppressLint("DefaultLocale")
@Composable
fun DashboardContent(
    navController: NavController,
    pasienData: PasienDetail?,
    nextReminder: PengingatItem?,
    username: String,
    currentSteps: Int,
    targetSteps: Int,
    calories: Int,
    prefsManager: PreferencesManager
) {
    val beratBadan = pasienData?.berat_badan ?: 0f
    val tinggiBadan = pasienData?.tinggi_badan ?: 0f
    val bmi = remember(beratBadan, tinggiBadan) {
        calculateBMI(beratBadan, tinggiBadan)
    }
    val bmiStatus = remember(bmi) { getBMIStatus(bmi) }
    val bmiColor = remember(bmi) { getBMIColor(bmi) }
    val bmiCategory = remember(bmi) { getBMICategory(bmi) }

    val context = LocalContext.current
    var showSleepDialog by remember { mutableStateOf(false) }

    var sleepDuration by remember {
        mutableLongStateOf(prefsManager.getSleepDuration())
    }
    val sleepData = remember(sleepDuration) {
        getSleepData(sleepDuration)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .verticalScroll(rememberScrollState())
    ) {
        DashboardHeader(
            username = username,
            onNotificationClick = { navController.navigate("notification_screen") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        StepsCard(
            currentSteps = currentSteps,
            targetSteps = targetSteps,
            calories = calories,
            onClick = { navController.navigate("track_screen") }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Status Section Header
        StatusSectionHeader()

        Spacer(modifier = Modifier.height(16.dp))

        // Health Status Cards
        HealthStatusCards(
            sleepData = sleepData,
            bmi = bmi,
            bmiStatus = bmiStatus,
            bmiColor = bmiColor,
            beratBadan = beratBadan,
            tinggiBadan = tinggiBadan,
            bmiCategory = bmiCategory,
            onSleepClick = { showSleepDialog = true }
        )

        Spacer(modifier = Modifier.height(24.dp))

        MedicationSection(nextReminder = nextReminder)

        Spacer(modifier = Modifier.height(100.dp))
    }

    if (showSleepDialog) {
        SleepTimerDialog(
            context = context,
            preferencesManager = prefsManager,
            onDismiss = {
                showSleepDialog = false
                sleepDuration = prefsManager.getSleepDuration()
            },
            onTimerSet = {
                Toast.makeText(context, "Timer tidur diatur!", Toast.LENGTH_SHORT).show()
                sleepDuration = prefsManager.getSleepDuration()
            }
        )
    }
}

@Composable
private fun DashboardHeader(
    username: String,
    onNotificationClick: () -> Unit
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
            IconButton(onClick = onNotificationClick) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = Color.Black
                )
            }
        }
    }
}

@Composable
private fun StepsCard(
    currentSteps: Int,
    targetSteps: Int,
    calories: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clickable(onClick = onClick),
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Langkahmu hari ini:",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
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
                            text = "${calories}kkal",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(text = "🔥", fontSize = 28.sp)
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
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusSectionHeader() {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
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
}

@Composable
private fun HealthStatusCards(
    sleepData: SleepData,
    bmi: Float,
    bmiStatus: String,
    bmiColor: Color,
    beratBadan: Float,
    tinggiBadan: Float,
    bmiCategory: String,
    onSleepClick: () -> Unit
) {
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
                        value = sleepData.hours,
                        unit = "jam",
                        maxValue = "/${RECOMMENDED_SLEEP_HOURS}jam",
                        status = sleepData.status,
                        statusColor = sleepData.statusColor,
                        modifier = Modifier
                            .weight(1f)
                            .clickable(onClick = onSleepClick)
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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatusCardFigma(
                        icon = "⚖",
                        title = "Berat badan",
                        value = if (beratBadan > 0) beratBadan.toInt().toString() else "0",
                        unit = "kg",
                        maxValue = bmiCategory,
                        status = "",
                        statusColor = bmiColor,
                        modifier = Modifier.weight(1f)
                    )

                    StatusCardFigma(
                        icon = "📏",
                        title = "Tinggi badan",
                        value = if (tinggiBadan > 0) tinggiBadan.toInt().toString() else "0",
                        unit = "cm",
                        maxValue = bmiCategory,
                        status = "",
                        statusColor = bmiColor,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
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
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Icon and status badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(text = icon, fontSize = 24.sp)
                if (status.isNotEmpty()) {
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
            }

            // Value and title
            Column {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = value,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    if (unit.isNotEmpty()) {
                        Text(
                            text = unit,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.Black
                        )
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
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
private fun MedicationSection(nextReminder: PengingatItem?) {
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
        EmptyMedicationCard()
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
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

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = reminder.nama_obat,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Text(
                    text = reminder.waktu_alarm.firstOrNull() ?: "00:00",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "${reminder.dosis_kuantitas.toInt()} ${reminder.dosis_unit} - ${reminder.catatan ?: "setelah makan"}",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun EmptyMedicationCard() {
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

@Composable
private fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = Color(0xFF2196F3))
    }
}

@Composable
private fun ErrorScreen(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = message,
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2196F3)
                )
            ) {
                Text("Coba Lagi")
            }
        }
    }
}

data class SleepData(
    val hours: String,
    val status: String,
    val statusColor: Color
)

fun getSleepData(durationMinutes: Long): SleepData {
    val hours = durationMinutes / 60.0
    val (status, color) = when {
        hours >= 7 -> "Bagus" to Color(0xFF4CAF50)
        hours >= 5 -> "Cukup" to Color(0xFFFFC107)
        hours > 0 -> "Kurang" to Color(0xFFF44336)
        else -> "Belum tidur" to Color.Gray
    }

    return SleepData(
        hours = String.format("%.1f", hours),
        status = status,
        statusColor = color
    )
}

fun calculateBMI(weight: Float, height: Float): Float {
    if (weight <= 0 || height <= 0) return 0f
    val heightInMeters = height / 100
    return weight / (heightInMeters.pow(2))
}

fun getBMIStatus(bmi: Float): String = when {
    bmi == 0f -> "N/A"
    bmi < 18.5 -> "Kurus"
    bmi < 25 -> "Ideal"
    bmi < 30 -> "Gemuk"
    else -> "Obesitas"
}

fun getBMIColor(bmi: Float): Color = when {
    bmi == 0f -> Color.Gray
    bmi < 18.5 -> Color(0xFFFFA726)
    bmi < 25 -> Color(0xFF4CAF50)
    bmi < 30 -> Color(0xFFFFA726)
    else -> Color(0xFFF44336)
}

fun getBMICategory(bmi: Float): String = when {
    bmi < 18.5 -> "Kurang berat badan"
    bmi < 25.0 -> "Normal"
    bmi < 30.0 -> "Kelebihan berat badan"
    else -> "Obesitas"
}

fun getCurrentDate(): String {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.forLanguageTag("id-ID"))
    return dateFormat.format(Date())
}

@RequiresApi(Build.VERSION_CODES.S)
@Preview
@Composable
fun DashboardScreenPreview() {
    SIMMHealthTheme {
        DashboardScreen(navController = rememberNavController())
    }
}

@Deprecated("Use calculateBMI instead", ReplaceWith("calculateBMI(beratKg, tinggiCm)"))
fun hitungBMI(beratKg: Float, tinggiCm: Float): Double {
    return calculateBMI(beratKg, tinggiCm).toDouble()
}

@Deprecated("Use getBMICategory instead", ReplaceWith("getBMICategory(bmi)"))
fun getKategoriBMI(bmi: Float): String = getBMICategory(bmi)

@Deprecated("Use getBMIColor instead", ReplaceWith("getBMIColor(bmi)"))
fun getStatusBMIColor(bmi: Float): Color = getBMIColor(bmi)