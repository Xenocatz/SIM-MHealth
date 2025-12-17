package com.example.sim_mhealth.data.repository

import com.example.sim_mhealth.data.api.ApiService
import com.example.sim_mhealth.ui.notification.NotificationItem
import com.example.sim_mhealth.ui.notification.NotificationType
import java.text.SimpleDateFormat
import java.util.*

class NotificationRepository(private val apiService: ApiService) {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    suspend fun getNotifications(token: String, idPasien: Int): Result<List<NotificationItem>> {
        return try {
            val response = apiService.getPengingatByPasien(token, idPasien)
            if (response.isSuccessful) {
                val pengingatList = response.body()?.pengingat ?: emptyList()
                val notifications = pengingatList.map { pengingat ->
                    NotificationItem(
                        id = pengingat.id_pengingat.toString(),
                        type = NotificationType.REMINDER,
                        title = "Waktunya Minum Obat: ${pengingat.nama_obat}",
                        message = "${pengingat.dosis_kuantitas} ${pengingat.dosis_unit} - ${pengingat.frekuensi}",
                        timestamp = dateFormat.parse(pengingat.tanggal_mulai)?.time ?: System.currentTimeMillis(),
                        isRead = pengingat.stok_saat_ini == 0
                    )
                }
                Result.success(notifications)
            } else {
                Result.failure(Exception("Gagal memuat: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteReminder(token: String, id: Int): Result<Unit> {
        return try {
            val response = apiService.deletePengingat(token, id)
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Gagal menghapus: ${response.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}