package com.example.sim_mhealth.data.repository

import com.example.sim_mhealth.data.api.AddLangkahRequest
import com.example.sim_mhealth.data.api.AddLangkahResponse
import com.example.sim_mhealth.data.api.RetrofitClient
import com.example.sim_mhealth.data.api.RiwayatLangkahResponse
import com.example.sim_mhealth.data.api.StatistikLangkahResponse

class StepsRepository {
    private val apiService = RetrofitClient.apiService

    suspend fun addLangkah(
        token: String,
        idPasien: Int,
        jumlahLangkah: Int,
        tanggal: String,
        catatan: String? = null
    ): Result<AddLangkahResponse> {
        return try {
            val request = AddLangkahRequest(
                id_pasien = idPasien,
                jumlah_langkah = jumlahLangkah,
                tanggal = tanggal,
                catatan = catatan
            )
            val response = apiService.addLangkah(token, request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to save steps: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRiwayatLangkah(
        token: String,
        idPasien: Int
    ): Result<RiwayatLangkahResponse> {
        return try {
            val response = apiService.getRiwayatLangkah(token, idPasien)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get history: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getStatistikLangkah(
        token: String,
        idPasien: Int
    ): Result<StatistikLangkahResponse> {
        return try {
            val response = apiService.getStatistikLangkah(token, idPasien)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get statistics: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}