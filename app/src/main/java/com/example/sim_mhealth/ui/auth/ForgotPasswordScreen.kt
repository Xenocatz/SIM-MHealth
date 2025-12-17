package com.example.sim_mhealth.ui.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.sim_mhealth.data.preferences.PreferencesManager
import com.example.sim_mhealth.data.repository.AuthRepository
import com.example.sim_mhealth.ui.theme.Gray700
import com.example.sim_mhealth.ui.theme.hindMadurai
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val prefsManager = remember { PreferencesManager(context) }
    val authRepository = remember { AuthRepository() }

    var currentStep by remember { mutableIntStateOf(1) }
    var username by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val token = prefsManager.getToken() ?: ""

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lupa Password") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Reset Password",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(vertical = 24.dp)
            )

            when (currentStep) {
                1 -> UsernameStep(
                    username = username,
                    onUsernameChange = { username = it },
                    onNextClick = {
                        if (username.isBlank()) {
                            Toast.makeText(context, "Username tidak boleh kosong", Toast.LENGTH_SHORT).show()
                            return@UsernameStep
                        }

                        scope.launch {
                            isLoading = true
                            authRepository.verifyUsername(token, username).fold(
                                onSuccess = { pasien ->
                                    isLoading = false
                                    if (pasien != null) {
                                        currentStep = 2
                                    } else {
                                        Toast.makeText(context, "Username tidak ditemukan", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                onFailure = { error ->
                                    isLoading = false
                                    Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    },
                    isLoading = isLoading
                )

                2 -> PasswordStep(
                    newPassword = newPassword,
                    confirmPassword = confirmPassword,
                    onNewPasswordChange = { newPassword = it },
                    onConfirmPasswordChange = { confirmPassword = it },
                    onSaveClick = {
                        if (newPassword.isBlank() || newPassword.length < 6) {
                            Toast.makeText(context, "Password minimal 6 karakter", Toast.LENGTH_SHORT).show()
                            return@PasswordStep
                        }
                        if (newPassword != confirmPassword) {
                            Toast.makeText(context, "Password tidak cocok", Toast.LENGTH_SHORT).show()
                            return@PasswordStep
                        }

                        scope.launch {
                            isLoading = true
                            authRepository.changePassword(token, username, newPassword).fold(
                                onSuccess = { message ->
                                    isLoading = false
                                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                    navController.popBackStack()
                                },
                                onFailure = { error ->
                                    isLoading = false
                                    Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    },
                    isLoading = isLoading
                )
            }
        }
    }
}

@Composable
fun UsernameStep(
    username: String,
    onUsernameChange: (String) -> Unit,
    onNextClick: () -> Unit,
    isLoading: Boolean
) {
    OutlinedTextField(
        value = username,
        onValueChange = onUsernameChange,
        modifier = Modifier.fillMaxWidth(),
        textStyle = TextStyle(color = Gray700),
        label = { Text("Username") },
        placeholder = { Text("Masukkan username Anda") },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        )
    )

    Spacer(modifier = Modifier.height(24.dp))

    Button(
        onClick = onNextClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF2196F3)
        ),
        enabled = !isLoading
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = Color.White)
        } else {
            Text("Lanjut", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

@Composable
fun PasswordStep(
    newPassword: String,
    confirmPassword: String,
    onNewPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    isLoading: Boolean
) {
    OutlinedTextField(
        value = newPassword,
        onValueChange = onNewPasswordChange,
        modifier = Modifier.fillMaxWidth(),
        textStyle = TextStyle(color = Gray700),
        label = { Text("Password Baru") },
        placeholder = { Text("Minimal 6 karakter") },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Next
        )
    )

    Spacer(modifier = Modifier.height(16.dp))

    OutlinedTextField(
        value = confirmPassword,
        onValueChange = onConfirmPasswordChange,
        modifier = Modifier.fillMaxWidth(),
        textStyle = TextStyle(color = Gray700),
        label = { Text("Konfirmasi Password") },
        placeholder = { Text("Ulangi password baru") },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        )
    )

    Spacer(modifier = Modifier.height(24.dp))

    Button(
        onClick = onSaveClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF4CAF50)
        ),
        enabled = !isLoading
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = Color.White)
        } else {
            Text("Simpan Password", fontWeight = FontWeight.Bold, fontFamily = hindMadurai, fontSize = 16.sp)
        }
    }
}