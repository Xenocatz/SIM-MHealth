package com.example.sim_mhealth.ui.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.sim_mhealth.data.api.RetrofitClient.apiService
import com.example.sim_mhealth.data.repository.NotificationRepository
import com.example.sim_mhealth.ui.theme.martel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class NotificationItem(
    val id: String,
    val type: NotificationType,
    val title: String,
    val message: String,
    val timestamp: Long,
    val isRead: Boolean = false
)

enum class NotificationType {
    REMINDER,
    HEALTH,
    SYSTEM,
    ACHIEVEMENT
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    navController: NavController,
    token: String,
    idPasien: Int
) {
    val viewModel: NotificationViewModel = viewModel(
        factory = NotificationViewModelFactory(
            repository = NotificationRepository(apiService),
            token = token,
            idPasien = idPasien
        )
    )

    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(NotificationTab.ALL) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<NotificationItem?>(null) }

    val filteredNotifications = when (selectedTab) {
        NotificationTab.ALL -> uiState.notifications
        NotificationTab.UNREAD -> uiState.notifications.filter { !it.isRead }
    }

    val unreadCount = uiState.notifications.count { !it.isRead }

    val snackbarHostState = remember { SnackbarHostState() }
    uiState.errorMessage?.let { error ->
        LaunchedEffect(error) {
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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
                    Icon(Icons.Default.ArrowBack, "Back", tint = Color.Black)
                }

                Text(
                    text = "Notifikasi",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.Center)
                )

                if (uiState.notifications.isNotEmpty()) {
                    TextButton(
                        onClick = { viewModel.markAllAsRead() },
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Text(
                            "Tandai Semua",
                            color = Color(0xFF2196F3),
                            fontFamily = martel,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            if (unreadCount > 0) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    NotificationTabButton(
                        text = "Semua",
                        count = uiState.notifications.size,
                        isSelected = selectedTab == NotificationTab.ALL,
                        onClick = { selectedTab = NotificationTab.ALL },
                        modifier = Modifier.weight(1f)
                    )

                    NotificationTabButton(
                        text = "Belum Dibaca",
                        count = unreadCount,
                        isSelected = selectedTab == NotificationTab.UNREAD,
                        onClick = { selectedTab = NotificationTab.UNREAD },
                        modifier = Modifier.weight(1f)
                    )
                }

                Divider(color = Color(0xFFE0E0E0))
            }

            when {
                uiState.isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                filteredNotifications.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Notifications,
                                null,
                                Modifier.size(80.dp),
                                tint = Color.LightGray
                            )
                            Spacer(Modifier.height(16.dp))
                            Text(
                                text = if (selectedTab == NotificationTab.UNREAD)
                                    "Tidak ada notifikasi belum dibaca"
                                else
                                    "Belum ada notifikasi",
                                fontSize = 16.sp,
                                color = Color.Gray
                            )

                            if (uiState.errorMessage != null) {
                                Spacer(Modifier.height(16.dp))
                                Button(onClick = { viewModel.loadNotifications() }) {
                                    Text("Coba Lagi")
                                }
                            }
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(filteredNotifications, key = { it.id }) { notification ->
                            NotificationCard(
                                notification = notification,
                                onClick = { viewModel.markAsRead(notification.id) },
                                onDelete = {
                                    itemToDelete = notification
                                    showDeleteDialog = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDeleteDialog && itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Pengingat?") },
            text = { Text("Apakah Anda yakin ingin menghapus pengingat obat ini?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteNotification(itemToDelete!!.id)
                    showDeleteDialog = false
                    itemToDelete = null
                }) {
                    Text("Hapus", color = Color(0xFFF44336))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationCard(
    notification: NotificationItem,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) Color.White else Color(0xFFE3F2FD)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                Modifier
                    .size(48.dp)
                    .background(
                        getNotificationColor(notification.type).copy(alpha = 0.2f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    getNotificationIcon(notification.type),
                    null,
                    tint = getNotificationColor(notification.type),
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(Modifier.weight(1f)) {
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                    Text(
                        notification.title,
                        fontSize = 15.sp,
                        fontWeight = if (notification.isRead) FontWeight.Normal else FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.weight(1f)
                    )
                    if (!notification.isRead) {
                        Box(
                            Modifier
                                .size(8.dp)
                                .background(Color(0xFF2196F3), CircleShape)
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))
                Text(
                    notification.message,
                    fontSize = 13.sp,
                    color = Color.Gray,
                    lineHeight = 18.sp,
                    maxLines = 2
                )
                Spacer(Modifier.height(4.dp))
                Text(formatTimestamp(notification.timestamp), fontSize = 12.sp, color = Color.Gray)
            }

            Box {
                IconButton(
                    onClick = { showMenu = true },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
                DropdownMenu(showMenu, { showMenu = false }) {
                    DropdownMenuItem(
                        text = { Text("Hapus") },
                        onClick = { showMenu = false; onDelete() },
                        leadingIcon = { Icon(Icons.Default.Delete, null, tint = Color(0xFFF44336)) }
                    )
                }
            }
        }
    }
}


@Composable
private fun NotificationTabButton(
    text: String,
    count: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(40.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color(0xFF2196F3) else Color.Transparent,
            contentColor = if (isSelected) Color.White else Color.Gray
        ),
        elevation = ButtonDefaults.buttonElevation(0.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            "$text ($count)",
            fontSize = 14.sp,
            fontFamily = martel,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}


enum class NotificationTab {
    ALL,
    UNREAD
}

private fun getNotificationIcon(type: NotificationType) = when (type) {
    NotificationType.REMINDER -> Icons.Default.Notifications
    NotificationType.HEALTH -> Icons.Default.Favorite
    NotificationType.SYSTEM -> Icons.Default.Info
    NotificationType.ACHIEVEMENT -> Icons.Default.EmojiEvents
}

private fun getNotificationColor(type: NotificationType) = when (type) {
    NotificationType.REMINDER -> Color(0xFF2196F3)
    NotificationType.HEALTH -> Color(0xFF4CAF50)
    NotificationType.SYSTEM -> Color(0xFF9E9E9E)
    NotificationType.ACHIEVEMENT -> Color(0xFFFFC107)
}

private fun formatTimestamp(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    return when {
        diff < 60000 -> "Baru saja"
        diff < 3600000 -> "${diff / 60000} menit lalu"
        diff < 86400000 -> "${diff / 3600000} jam lalu"
        diff < 172800000 -> "Kemarin"
        else -> SimpleDateFormat("dd MMM yyyy", Locale("id", "ID")).format(Date(timestamp))
    }
}