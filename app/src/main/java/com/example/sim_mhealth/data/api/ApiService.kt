package com.example.sim_mhealth.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

// Request Models
data class LoginRequest(
    val username: String,
    val password: String
)

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)

// Response Models
data class LoginResponse(
    val success: Boolean,
    val data: LoginData?,
    val message: String
)

data class LoginData(
    val user: User,
    val token: String
)

data class User(
    val id_pasien: Int,
    val username: String,
    val email: String,
    val is_active: Boolean
)

data class RegisterResponse(
    val success: Boolean,
    val data: Any?,
    val message: String
)

// API Interface
interface ApiService {
    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("registration")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>
}