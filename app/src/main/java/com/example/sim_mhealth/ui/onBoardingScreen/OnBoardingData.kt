package com.example.sim_mhealth.ui.onBoardingScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object OnBoardingData {
    var tanggalLahir by mutableStateOf("")
    var jenisKelamin by mutableStateOf("")
    var beratBadan by mutableStateOf("")
    var tinggiBadan by mutableStateOf("")
    var hasHealthCondition by mutableStateOf<Boolean?>(null)
    var sejakKapan by mutableStateOf("")

    fun reset() {
        tanggalLahir = ""
        jenisKelamin = ""
        beratBadan = ""
        tinggiBadan = ""
        hasHealthCondition = null
        sejakKapan = ""
    }
}