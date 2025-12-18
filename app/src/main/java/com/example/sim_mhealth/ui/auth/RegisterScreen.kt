package com.example.sim_mhealth.ui.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.sim_mhealth.R
import com.example.sim_mhealth.data.preferences.PreferencesManager
import com.example.sim_mhealth.data.repository.AuthRepository
import com.example.sim_mhealth.ui.theme.Gray700
import com.example.sim_mhealth.ui.theme.MintGreen500
import com.example.sim_mhealth.ui.theme.Primary50
import com.example.sim_mhealth.ui.theme.SIMMHealthTheme
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember { AuthRepository() }
    val prefsManager = remember { PreferencesManager(context) }

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val currentAuthSegment by remember { mutableStateOf(AuthSegment.REGISTER) }

    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.forest_jogging_group),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .blur(8.dp)
                .navigationBarsPadding(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.6f),
                            Color.Black.copy(alpha = 0.4f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(horizontal = 20.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.8f),
                    contentColor = MaterialTheme.colorScheme.onBackground,
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(scrollState)
            ) {
                Text(
                    text = "MHealth",
                    fontSize = 48.sp,
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                )

                Text(
                    text = buildAnnotatedString {
                        append("Mulai ")
                        withStyle(style = SpanStyle(color = MintGreen500)) {
                            append("Rutinitas Sehat")
                        }
                        append(" Anda")
                    },
                    fontSize = 22.sp,
                    color = Color.White,
                    style = MaterialTheme.typography.displayLarge,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding(),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp).verticalScroll(scrollState)
                        .navigationBarsPadding()
                ) {
                    AuthSegmentedControl(
                        selectedSegment = currentAuthSegment,
                        onSegmentSelected = { segment ->
                            if (segment == AuthSegment.LOGIN) {
                                navController.popBackStack()
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Username",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("alpitraonichan") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Username",
                                tint = Color(0xFF2196F3)
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        textStyle = TextStyle(color = Gray700),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2196F3),
                            unfocusedBorderColor = Color.LightGray
                        ),
                        enabled = !isLoading,
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Email",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("health_care@gmail.com") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Email",
                                tint = Color(0xFF2196F3)
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        textStyle = TextStyle(color = Gray700),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2196F3),
                            unfocusedBorderColor = Color.LightGray
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        enabled = !isLoading,
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Password",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(color = Gray700),
                        placeholder = { Text("••••••••") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Password",
                                tint = Color(0xFF2196F3)
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                    tint = Color.Gray
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2196F3),
                            unfocusedBorderColor = Color.LightGray
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        enabled = !isLoading,
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Ulangi Password",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        textStyle = TextStyle(color = Gray700),
                        placeholder = { Text("••••••••") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Confirm Password",
                                tint = Color(0xFF2196F3)
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = {
                                confirmPasswordVisible = !confirmPasswordVisible
                            }) {
                                Icon(
                                    imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password",
                                    tint = Color.Gray
                                )
                            }
                        },
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2196F3),
                            unfocusedBorderColor = Color.LightGray
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        enabled = !isLoading,
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            when {
                                username.isBlank() || email.isBlank() || password.isBlank() -> {
                                    Toast.makeText(
                                        context,
                                        "Username, email dan password wajib hukumnya untuk diisi",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@Button
                                }

                                !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                                    Toast.makeText(
                                        context,
                                        "Format email tidak valid",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@Button
                                }

                                password != confirmPassword -> {
                                    Toast.makeText(
                                        context,
                                        "Password tidak cocok",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@Button
                                }

                                password.length < 6 -> {
                                    Toast.makeText(
                                        context,
                                        "Password minimal 6 karakter",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@Button
                                }
                            }

                            isLoading = true
                            scope.launch {
                                repository.register(
                                    username = username.trim(),
                                    email = email.trim().lowercase(),
                                    password = password
                                ).fold(
                                    onSuccess = { response ->
                                        if (response.success && response.data != null) {
                                            prefsManager.saveLoginData(
                                                token = response.data.token,
                                                userId = response.data.user.id_pasien,
                                                username = response.data.user.username,
                                                email = response.data.user.email
                                            )

                                            Toast.makeText(
                                                context,
                                                response.message,
                                                Toast.LENGTH_SHORT
                                            ).show()

                                            navController.navigate("onboarding_screen_1") {
                                                popUpTo("intro_screen") { inclusive = true }
                                            }
                                        } else {
                                            Toast.makeText(
                                                context,
                                                response.message,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        isLoading = false
                                    },
                                    onFailure = { error ->
                                        val errorMessage = when {
                                            error.message?.contains("409") == true -> "Username atau Email sudah dipakai"
                                            error.message?.contains("400") == true -> "Data tidak valid"
                                            error.message?.contains("Failed to connect") == true -> "Gagal terhubung ke server"
                                            else -> error.message ?: "Terjadi kesalahan"
                                        }
                                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT)
                                            .show()
                                        isLoading = false
                                    }
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White
                            )
                        } else {
                            Text(
                                text = "Register",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Primary50,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Preview
@Composable
fun RegisterScreenPreview() {
    SIMMHealthTheme {
        RegisterScreen(navController = rememberNavController())
    }
}