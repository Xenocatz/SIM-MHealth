package com.example.sim_mhealth.ui.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.example.sim_mhealth.ui.theme.*
import com.example.sim_mhealth.ui.theme.SIMMHealthTheme
import kotlinx.coroutines.launch

enum class AuthSegment {
    LOGIN, REGISTER
}

@Composable
fun AuthSegmentedControl(
    selectedSegment: AuthSegment,
    onSegmentSelected: (AuthSegment) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color.LightGray.copy(alpha = 0.2f)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { onSegmentSelected(AuthSegment.LOGIN) },
                modifier = Modifier.weight(1f).fillMaxHeight(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedSegment == AuthSegment.LOGIN) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        Color.Transparent
                    },
                    contentColor = if (selectedSegment == AuthSegment.LOGIN) {
                        Color.White
                    } else {
                        Color.Gray
                    }
                ),
                elevation = if (selectedSegment == AuthSegment.LOGIN) {
                    ButtonDefaults.buttonElevation(4.dp)
                } else {
                    ButtonDefaults.buttonElevation(0.dp)
                },
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Login",
                    fontWeight = if (selectedSegment == AuthSegment.LOGIN) FontWeight.Bold else FontWeight.Normal,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Button(
                onClick = { onSegmentSelected(AuthSegment.REGISTER) },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedSegment == AuthSegment.REGISTER) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        Color.Transparent
                    },
                    contentColor = if (selectedSegment == AuthSegment.REGISTER) {
                        Color.White
                    } else {
                        Color.Gray
                    }
                ),
                elevation = if (selectedSegment == AuthSegment.REGISTER) {
                    ButtonDefaults.buttonElevation(4.dp)
                } else {
                    ButtonDefaults.buttonElevation(0.dp)
                },
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Register",
                    fontWeight = if (selectedSegment == AuthSegment.REGISTER) FontWeight.Bold else FontWeight.Normal,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
fun LoginScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember { AuthRepository() }
    val prefsManager = remember { PreferencesManager(context) }

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val currentAuthSegment by remember { mutableStateOf(AuthSegment.LOGIN) }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.forest_jogging_group),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .blur(8.dp),
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
            modifier = Modifier
                .fillMaxSize(),
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
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Text(
                    text = "MHealth",
                    fontSize = 48.sp,
                    style = MaterialTheme.typography.headlineLarge,
                    color = Primary50,
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
                    .fillMaxSize(),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AuthSegmentedControl(
                        selectedSegment = currentAuthSegment,
                        onSegmentSelected = { segment ->
                            if (segment == AuthSegment.REGISTER) {
                                navController.navigate("register_screen")
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

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
                        placeholder = { Text("Masukkan Username Anda") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Username",
                                tint = Color(0xFF2196F3)
                            )
                        },
                        textStyle = TextStyle(color = Gray700),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2196F3),
                            unfocusedBorderColor = Color.LightGray
                        ),
                        enabled = !isLoading,
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

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
                        placeholder = { Text("••••••••") },
                        textStyle = TextStyle(color = Gray700),
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

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = rememberMe,
                                onCheckedChange = { rememberMe = it },
                                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF2196F3)),
                                enabled = !isLoading
                            )
                            Text(text = "Ingat Saya", fontSize = 14.sp, color = Color.Gray)
                        }

                        TextButton(
                            onClick = { /* Handle forgot password */ },
                            enabled = !isLoading
                        ) {
                            Text(
                                text = "Lupa Password?",
                                fontSize = 14.sp,
                                style = MaterialTheme.typography.bodyLarge,
                                color = Primary500
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            when {
                                username.isBlank() -> {
                                    Toast.makeText(context, "Silahkan isi username", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                password.isBlank() -> {
                                    Toast.makeText(context, "Input password tidak boleh kosong", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                            }

                            isLoading = true
                            scope.launch {
                                repository.login(username.trim(), password).fold(
                                    onSuccess = { response ->
                                        if (response.success && response.data != null) {
                                            prefsManager.saveLoginData(
                                                token = response.data.token,
                                                userId = response.data.user.id_pasien,
                                                username = response.data.user.username,
                                                email = response.data.user.email
                                            )

                                            Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()

                                            navController.navigate("home_screen") {
                                                popUpTo("intro_screen") { inclusive = true }
                                            }
                                        } else {
                                            Toast.makeText(context, "Login gagal: ${response.message}", Toast.LENGTH_SHORT).show()
                                        }
                                        isLoading = false
                                    },
                                    onFailure = { error ->
                                        val errorMessage = when {
                                            error.message?.contains("401") == true -> "Username atau password tidak valid"
                                            error.message?.contains("Failed to connect") == true -> "Gagal terhubung ke server"
                                            else -> error.message ?: "Terjadi kesalahan"
                                        }
                                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
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
                                text = "Login",
                                fontSize = 18.sp,
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    SIMMHealthTheme {
        LoginScreen(navController = rememberNavController())
    }
}