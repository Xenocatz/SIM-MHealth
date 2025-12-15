package com.example.sim_mhealth.data.repository

import com.example.sim_mhealth.data.api.PasienResponse
import com.example.sim_mhealth.data.api.RetrofitClient

class DashboardRepository {
    private val apiService = RetrofitClient.apiService

    suspend fun getPasienData(token: String, userId: Int): Result<PasienResponse> {
        return try {
            val response = apiService.getPasienById("Bearer $token", userId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Gagal mengambil data: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}