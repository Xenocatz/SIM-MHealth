package com.example.sim_mhealth.data.repository

import com.example.sim_mhealth.data.api.PasienResponse
import com.example.sim_mhealth.data.api.RetrofitClient
import com.example.sim_mhealth.data.api.UpdateProfileRequest
import com.example.sim_mhealth.data.api.UpdateProfileResponse

class ProfileRepository {
    private val apiService = RetrofitClient.apiService

    suspend fun updateProfile(
        token: String,
        userId: Int,
        email: String?,
        tanggalLahir: String?,
        beratBadan: Float?,
        tinggiBadan: Float?,
        jenisKelamin: String?
    ): Result<UpdateProfileResponse> {
        return try {
            val request = UpdateProfileRequest(
                email = email,
                tanggal_lahir = tanggalLahir,
                berat_badan = beratBadan,
                tinggi_badan = tinggiBadan,
                jenis_kelamin = jenisKelamin
            )
            val response = apiService.updatePasien("Bearer $token", userId, request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Update gagal: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

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