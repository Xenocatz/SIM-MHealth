package com.example.sim_mhealth.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

data class LoginRequest(
    val username: String,
    val password: String
)

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)

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

    @PUT("pasien/{id}")
    suspend fun updatePasien(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: UpdateProfileRequest
    ): Response<UpdateProfileResponse>

    @GET("pengingat/pasien/{id_pasien}")
    suspend fun getPengingatByPasien(
        @Header("Authorization") token: String,
        @Path("id_pasien") idPasien: Int
    ): Response<PengingatResponse>

    @GET("pengingat/{id}")
    suspend fun getPengingatById(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<PengingatDetailResponse>

    @POST("pengingat")
    suspend fun createPengingat(
        @Header("Authorization") token: String,
        @Body request: CreatePengingatRequest
    ): Response<CreatePengingatResponse>

    @PUT("pengingat/{id}")
    suspend fun updatePengingat(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: UpdatePengingatRequest
    ): Response<UpdatePengingatResponse>

    @DELETE("pengingat/{id}")
    suspend fun deletePengingat(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<DeletePengingatResponse>

    @PUT("pengingat/detail/{id}/status")
    suspend fun updateDetailPengingatStatus(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: UpdateStatusRequest
    ): Response<UpdateStatusResponse>

    @GET("pasien/username/{username}")
    suspend fun getPasienByUsername(
        @Header("Authorization") token: String,
        @Path("username") username: String
    ): Response<PasienResponse>

    @PUT("pasien/{username}/password")
    suspend fun changePassword(
        @Header("Authorization") token: String,
        @Path("username") username: String,
        @Body request: ChangePasswordRequest
    ): Response<ChangePasswordResponse>
}

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

data class OnBoardingRequest(
    val id_pasien: Int,
    val tanggal_lahir: String?,
    val tinggi_badan: Float?,
    val berat_badan: Float?,
    val jenis_kelamin: String?,
    val jenis_kondisi: List<String>?,
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
    val jenis_kondisi: List<String>?,
    val sejak_kapan: String?
)

data class UpdateProfileRequest(
    val email: String?,
    val tanggal_lahir: String?,
    val berat_badan: Float?,
    val tinggi_badan: Float?,
    val jenis_kelamin: String?
)

data class UpdateProfileResponse(
    val message: String,
    val pasien: PasienDetail
)

data class PengingatResponse(
    val pengingat: List<PengingatItem>
)

data class PengingatDetailResponse(
    val pengingat: PengingatDetail
)

data class PengingatItem(
    val id_pengingat: Int,
    val id_pasien: Int,
    val nama_obat: String,
    val dosis_kuantitas: Float,
    val dosis_unit: String,
    val frekuensi: String,
    val tanggal_mulai: String,
    val tanggal_akhir: String?,
    val catatan: String?,
    val waktu_alarm: List<String>,
    val stok_awal: Int?,
    val stok_saat_ini: Int?
)

data class PengingatDetail(
    val id_pengingat: Int,
    val id_pasien: Int,
    val nama_obat: String,
    val dosis_kuantitas: Float,
    val dosis_unit: String,
    val frekuensi: String,
    val tanggal_mulai: String,
    val tanggal_akhir: String?,
    val catatan: String?,
    val waktu_alarm: List<String>,
    val stok_awal: Int?,
    val stok_saat_ini: Int?,
    val detail_jadwal: List<DetailPengingat>?
)

data class DetailPengingat(
    val id_detail: Int,
    val id_pengingat: Int,
    val tanggal_minum: String,
    val waktu_minum: String,
    val status: String,
    val waktu_diselesaikan: String?
)

data class CreatePengingatRequest(
    val id_pasien: Int,
    val nama_obat: String,
    val dosis_kuantitas: Float,
    val dosis_unit: String,
    val frekuensi: String,
    val tanggal_mulai: String,
    val tanggal_akhir: String?,
    val catatan: String?,
    val waktu_alarm: List<String>,
    val stok_awal: Int?
)

data class UpdatePengingatRequest(
    val nama_obat: String?,
    val dosis_kuantitas: Float?,
    val dosis_unit: String?,
    val frekuensi: String?,
    val tanggal_mulai: String?,
    val tanggal_akhir: String?,
    val catatan: String?,
    val waktu_alarm: List<String>?,
    val stok_awal: Int?
)

data class CreatePengingatResponse(
    val message: String,
    val pengingat: PengingatItem
)

data class UpdatePengingatResponse(
    val message: String,
    val pengingat: PengingatItem
)

data class DeletePengingatResponse(
    val message: String
)

data class UpdateStatusRequest(
    val status: String, // "sudah_minum" atau "belum_minum"
    val waktu: String
)

data class UpdateStatusResponse(
    val message: String,
    val detail: DetailPengingat
)

data class VerifyUsernameRequest(
    val username: String
)

data class VerifyUsernameResponse(
    val success: Boolean,
    val data: PasienDetail?,
    val message: String
)

data class ChangePasswordRequest(
    val newPassword: String
)

data class ChangePasswordResponse(
    val message: String
)