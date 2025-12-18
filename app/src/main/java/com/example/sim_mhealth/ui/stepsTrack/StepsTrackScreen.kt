package com.example.sim_mhealth.ui.stepsTrack

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.sim_mhealth.data.api.LangkahData
import com.example.sim_mhealth.data.api.StatistikData
import com.example.sim_mhealth.data.preferences.PreferencesManager
import com.example.sim_mhealth.data.repository.StepsRepository
import com.example.sim_mhealth.data.service.StepsCounterService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepsTrackScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember { StepsRepository() }
    val prefsManager = remember { PreferencesManager(context) }

    var isTracking by remember { mutableStateOf(isServiceRunning(context)) }
    var currentSteps by remember { mutableIntStateOf(0) }
    var statistik by remember { mutableStateOf<StatistikData?>(null) }
    var riwayat by remember { mutableStateOf<List<LangkahData>>(emptyList()) }
    var showHistory by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            startStepTracking(context)
            isTracking = true
        } else {
            Toast.makeText(context, "Permission required to track steps", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        loadData(context, scope, repository, prefsManager) { stats, history ->
            statistik = stats
            riwayat = history
        }
    }

    LaunchedEffect(isTracking) {
        val userId = prefsManager.getUserId()
        while (isTracking) {
            currentSteps = getCurrentStepsFromPrefs(context, userId)
            delay(1000)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Steps Tracking") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back to Dashboard",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2196F3),
                    titleContentColor = Color.White
                ),
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5F5))
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsRun,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color(0xFF2196F3)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Target: 10,000 langkah",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Steps with fire emoji
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "🔥",
                            fontSize = 48.sp
                        )
                        Text(
                            text = currentSteps.toString(),
                            fontSize = 56.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2196F3)
                        )
                    }

                    Text(
                        text = "langkah hari ini",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Motivational text
                    Text(
                        text = if (currentSteps >= 10000) {
                            "Luar biasa! Target tercapai! 🎉"
                        } else if (currentSteps >= 5000) {
                            "Tetap bergerak, ritmenya bagus! 💪"
                        } else {
                            "Ayo mulai bergerak! 🚶‍♂️"
                        },
                        fontSize = 14.sp,
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (isTracking) {
                                stopStepTracking(context)
                                isTracking = false
                            } else {
                                val permissions = mutableListOf(
                                    Manifest.permission.ACTIVITY_RECOGNITION
                                )
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    permissions.add(Manifest.permission.POST_NOTIFICATIONS)
                                }
                                permissionLauncher.launch(permissions.toTypedArray())
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isTracking) Color.Red else Color(0xFF2196F3)
                        ),
                        shape = CircleShape
                    ) {
                        Icon(
                            imageVector = if (isTracking) Icons.Default.Stop else Icons.Default.PlayArrow,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isTracking) "Stop Tracking" else "Start Tracking",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Statistics Card
            statistik?.let { stats ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Statistik",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        StatItem("Total Langkah", stats.total_langkah.toString())
                        StatItem("Rata-rata Harian", stats.rata_rata_harian.toString())
                        StatItem("Hari Tercatat", stats.hari_tercatat.toString())
                        StatItem("Tertinggi", "${stats.langkah_tertinggi} langkah")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // History Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Riwayat",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = { showHistory = !showHistory }) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "Toggle History",
                        tint = Color(0xFF2196F3)
                    )
                }
            }

            // History List
            if (showHistory) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    items(riwayat) { item ->
                        HistoryItem(item)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = Color.Gray
        )
        Text(
            text = value,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2196F3)
        )
    }
}

@Composable
private fun HistoryItem(item: LangkahData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = formatDate(item.tanggal),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                item.catatan?.let {
                    Text(
                        text = it,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🔥",
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = item.jumlah_langkah.toString(),
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2196F3),
                    fontSize = 18.sp
                )
            }
        }
    }
}

private fun formatDate(dateString: String): String {
    // Convert YYYY-MM-DD to DD MMM YYYY
    return try {
        val parts = dateString.split("-")
        val months = listOf(
            "Jan", "Feb", "Mar", "Apr", "Mei", "Jun",
            "Jul", "Agu", "Sep", "Okt", "Nov", "Des"
        )
        "${parts[2]} ${months[parts[1].toInt() - 1]} ${parts[0]}"
    } catch (e: Exception) {
        dateString
    }
}

private fun startStepTracking(context: Context) {
    val intent = Intent(context, StepsCounterService::class.java)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        context.startForegroundService(intent)
    } else {
        context.startService(intent)
    }
}

private fun stopStepTracking(context: Context) {
    val intent = Intent(context, StepsCounterService::class.java)
    context.stopService(intent)
}

private fun isServiceRunning(context: Context): Boolean {
    val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
    for (service in manager.getRunningServices(Int.MAX_VALUE)) {
        if (StepsCounterService::class.java.name == service.service.className) {
            return true
        }
    }
    return false
}

private fun getCurrentStepsFromPrefs(context: Context, userId: Int): Int {
    val prefsName = "steps_prefs_user_$userId"
    val prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
    return prefs.getInt("current_steps", 0)
}

private fun loadData(
    context: Context,
    scope: CoroutineScope,
    repository: StepsRepository,
    prefsManager: PreferencesManager,
    onResult: (StatistikData?, List<LangkahData>) -> Unit
) {
    scope.launch {
        val token = prefsManager.getToken() ?: return@launch
        val userId = prefsManager.getUserId()
        if (userId == -1) return@launch

        val statsResult = repository.getStatistikLangkah(token, userId)
        val historyResult = repository.getRiwayatLangkah(token, userId)

        val stats = statsResult.getOrNull()?.data
        val history = historyResult.getOrNull()?.data ?: emptyList()

        onResult(stats, history)
    }
}
