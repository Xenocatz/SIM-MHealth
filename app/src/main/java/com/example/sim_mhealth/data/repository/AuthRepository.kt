package com.example.sim_mhealth.data.repository

import com.example.sim_mhealth.data.api.LoginRequest
import com.example.sim_mhealth.data.api.LoginResponse
import com.example.sim_mhealth.data.api.RegisterRequest
import com.example.sim_mhealth.data.api.RegisterResponse
import com.example.sim_mhealth.data.api.RetrofitClient

class AuthRepository {
    private val apiService = RetrofitClient.apiService

    suspend fun login(username: String, password: String): Result<LoginResponse> {
        return try {
            val response = apiService.login(LoginRequest(username, password))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Login gagal: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(
        username: String,
        email: String,
        password: String
    ): Result<RegisterResponse> {
        return try {
            val request = RegisterRequest(
                username = username,
                email = email,
                password = password
            )
            val response = apiService.register(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Registrasi gagal: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}