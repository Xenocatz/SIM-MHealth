package com.example.sim_mhealth.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

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
    val email: String? = null
)

data class RegisterResponse(
    val success: Boolean,
    val data: RegisterData?,
    val message: String
)

data class RegisterData(
    val user: User,
    val token: String,
    val expiresIn: String
)

// API Interface
interface ApiService {
    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("registration")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("onBoarding")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body request: OnBoardingRequest
    ): Response<OnBoardingResponse>

    @GET("pasien/{id}")
    suspend fun getPasienById(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<PasienResponse>
}

// OnBoarding Models
data class OnBoardingRequest(
    val id_pasien: Int,
    val tanggal_lahir: String?,
    val tinggi_badan: Float?,
    val berat_badan: Float?,
    val jenis_kelamin: String?,
    val jenis_kondisi: List<String>?,  // Array of strings
    val sejak_kapan: String?
)

data class OnBoardingResponse(
    val success: Boolean,
    val data: OnBoardingData?,
    val message: String
)

data class OnBoardingData(
    val id_pasien: Int,
    val username: String,
    val email: String,
    val tanggal_lahir: String?,
    val tinggi_badan: Float?,
    val berat_badan: Float?,
    val jenis_kelamin: String?,
    val jenis_kondisi: List<String>?,  // Array of strings
    val sejak_kapan: String?
)

data class PasienResponse(
    val pasien: PasienDetail
)

data class PasienDetail(
    val id_pasien: Int,
    val username: String,
    val email: String,
    val tanggal_lahir: String?,
    val tinggi_badan: Float?,
    val berat_badan: Float?,
    val jenis_kelamin: String?,
    val alamat: String?,
    val nomor_telepon: String?,
    val jenis_kondisi: List<String>?,
    val sejak_kapan: String?
)