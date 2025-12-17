package com.example.sim_mhealth.ui.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.sim_mhealth.data.repository.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NotificationViewModel(
    private val repository: NotificationRepository,
    private val token: String,
    private val idPasien: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    data class NotificationUiState(
        val notifications: List<NotificationItem> = emptyList(),
        val isLoading: Boolean = false,
        val errorMessage: String? = null
    )

    init {
        loadNotifications()
    }

    fun loadNotifications() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            repository.getNotifications(token, idPasien).fold(
                onSuccess = { notifications ->
                    _uiState.update { it.copy(notifications = notifications, isLoading = false) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(errorMessage = error.message, isLoading = false) }
                }
            )
        }
    }

    fun markAsRead(notificationId: String) {
        _uiState.update { state ->
            state.copy(notifications = state.notifications.map {
                if (it.id == notificationId) it.copy(isRead = true) else it
            })
        }
    }

    fun deleteNotification(notificationId: String) {
        viewModelScope.launch {
            val id = notificationId.toIntOrNull() ?: return@launch
            repository.deleteReminder(token, id).fold(
                onSuccess = {
                    _uiState.update { state ->
                        state.copy(notifications = state.notifications.filter { it.id != notificationId })
                    }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(errorMessage = "Gagal menghapus: ${error.message}") }
                }
            )
        }
    }

    fun markAllAsRead() {
        _uiState.update { state ->
            state.copy(notifications = state.notifications.map { it.copy(isRead = true) })
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}

@Suppress("UNCHECKED_CAST")
class NotificationViewModelFactory(
    private val repository: NotificationRepository,
    private val token: String,
    private val idPasien: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotificationViewModel::class.java)) {
            return NotificationViewModel(repository, token, idPasien) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}