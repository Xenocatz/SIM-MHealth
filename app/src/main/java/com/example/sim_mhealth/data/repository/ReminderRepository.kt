package com.example.sim_mhealth.data.repository

import com.example.sim_mhealth.data.api.*

class ReminderRepository {
    private val apiService = RetrofitClient.apiService

    suspend fun getPengingatByPasien(token: String, idPasien: Int): Result<PengingatResponse> {
        return try {
            val response = apiService.getPengingatByPasien("Bearer $token", idPasien)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Gagal mengambil data: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPengingatById(token: String, id: Int): Result<PengingatDetailResponse> {
        return try {
            val response = apiService.getPengingatById("Bearer $token", id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Gagal mengambil data: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createPengingat(
        token: String,
        request: CreatePengingatRequest
    ): Result<CreatePengingatResponse> {
        return try {
            val response = apiService.createPengingat("Bearer $token", request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Gagal membuat pengingat: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updatePengingat(
        token: String,
        id: Int,
        request: UpdatePengingatRequest
    ): Result<UpdatePengingatResponse> {
        return try {
            val response = apiService.updatePengingat("Bearer $token", id, request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Gagal update pengingat: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deletePengingat(token: String, id: Int): Result<DeletePengingatResponse> {
        return try {
            val response = apiService.deletePengingat("Bearer $token", id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Gagal menghapus pengingat: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}