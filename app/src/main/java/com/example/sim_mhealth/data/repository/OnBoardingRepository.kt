package com.example.sim_mhealth.data.repository

import com.example.sim_mhealth.data.api.OnBoardingRequest
import com.example.sim_mhealth.data.api.OnBoardingResponse
import com.example.sim_mhealth.data.api.RetrofitClient

class OnBoardingRepository {
    private val apiService = RetrofitClient.apiService

    suspend fun updateProfile(
        token: String,
        idPasien: Int,
        tanggalLahir: String?,
        tinggiBadan: Float?,
        beratBadan: Float?,
        jenisKelamin: String?,
        jenisKondisi: List<String>?,
        sejakKapan: String?
    ): Result<OnBoardingResponse> {
        return try {
            val request = OnBoardingRequest(
                id_pasien = idPasien,
                tanggal_lahir = tanggalLahir,
                tinggi_badan = tinggiBadan,
                berat_badan = beratBadan,
                jenis_kelamin = jenisKelamin,
                jenis_kondisi = jenisKondisi,
                sejak_kapan = sejakKapan
            )
            val response = apiService.updateProfile("Bearer $token", request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Update gagal: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}