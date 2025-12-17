package com.example.sim_mhealth.ui.onBoardingScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.sim_mhealth.R
import com.example.sim_mhealth.ui.theme.DateInputWithCalendarPicker
import com.example.sim_mhealth.ui.theme.Gray200
import com.example.sim_mhealth.ui.theme.Gray50
import com.example.sim_mhealth.ui.theme.Gray700
import com.example.sim_mhealth.ui.theme.Primary600
import com.example.sim_mhealth.ui.theme.SIMMHealthTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun OnBoardingScreen2(navController: NavController) {
    var tanggalLahir by remember { mutableStateOf(OnBoardingData.tanggalLahir) }
    var jenisKelamin by remember { mutableStateOf(OnBoardingData.jenisKelamin) }
    var beratBadan by remember { mutableStateOf(OnBoardingData.beratBadan) }
    var tinggiBadan by remember { mutableStateOf(OnBoardingData.tinggiBadan) }

    Box(
        Modifier
            .fillMaxSize()
            .background(Gray50)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LinearProgressIndicator(
                    progress = 0.66f,
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp),
                    color = Color(0xFF2196F3),
                    trackColor = Color(0xFFE0E0E0),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "2/3",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Image(
                painter = painterResource(id = R.drawable.undraw_activity_tracker),
                contentDescription = "Health Data",
                modifier = Modifier
                    .size(200.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Column(Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                Text(
                    text = "Tanggal Lahir*",
                    fontSize = 14.sp,
                    color = Gray700,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )

                DateInputWithCalendarPicker(
                    selectedDate = tanggalLahir,
                    onDateSelected = { newDate ->
                        tanggalLahir = newDate
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Jenis Kelamin*",
                    fontSize = 14.sp,
                    color = Gray700,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    // Radio Button Pria
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .selectable(
                                selected = (jenisKelamin == "Pria"),
                                onClick = { jenisKelamin = "Pria" }
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (jenisKelamin == "Pria"),
                            onClick = { jenisKelamin = "Pria" },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color(0xFF2196F3),
                                unselectedColor = Color.Gray
                            )
                        )
                        Text(
                            text = "Pria",
                            modifier = Modifier.padding(start = 8.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            color = Gray700
                        )
                    }

                    // Radio Button Wanita
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .selectable(
                                selected = (jenisKelamin == "Wanita"),
                                onClick = { jenisKelamin = "Wanita" }
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (jenisKelamin == "Wanita"),
                            onClick = { jenisKelamin = "Wanita" },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color(0xFF2196F3),
                                unselectedColor = Color.Gray
                            )
                        )
                        Text(
                            text = "Wanita",
                            modifier = Modifier.padding(start = 8.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            color = Gray700
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Berat Badan*",
                    fontSize = 14.sp,
                    color = Gray700,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = beratBadan,
                    onValueChange = { beratBadan = it },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = Color.DarkGray),
                    placeholder = { Text("kg", color = Color.LightGray) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_weight),
                            contentDescription = "Weight",
                            tint = Color(0xFF2196F3)
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF2196F3),
                        unfocusedBorderColor = Color.LightGray
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )

                Spacer(modifier = Modifier.height(16.dp))


                Text(
                    text = "Tinggi Badan*",
                    fontSize = 14.sp,
                    color = Gray700,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = tinggiBadan,
                    onValueChange = { tinggiBadan = it },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = Color.DarkGray),
                    placeholder = { Text("cm", color = Color.LightGray) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_height),
                            contentDescription = "Height",
                            tint = Color(0xFF2196F3)
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF2196F3),
                        unfocusedBorderColor = Color.LightGray
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = CircleShape,
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            width = 1.dp,
                            brush = androidx.compose.ui.graphics.SolidColor(Gray700)
                        )
                    ) {
                        Text(
                            text = "Kembali",
                            fontSize = 16.sp,
                            color = Gray700,
                            fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    Button(
                        onClick = {
                            OnBoardingData.tanggalLahir = tanggalLahir
                            OnBoardingData.jenisKelamin = jenisKelamin
                            OnBoardingData.beratBadan = beratBadan
                            OnBoardingData.tinggiBadan = tinggiBadan

                            navController.navigate("onboarding_screen_3")
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors( containerColor = Primary600, disabledContainerColor = Gray200),
                        shape = CircleShape,
                        enabled = tanggalLahir.isNotBlank() && jenisKelamin.isNotBlank() &&
                                beratBadan.isNotBlank() && tinggiBadan.isNotBlank()
                    ) {
                        Text(
                            text = "Lanjutkan",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Gray50,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Preview
@Composable
fun OnBoardingScreenStep2Preview() {
    SIMMHealthTheme {
        OnBoardingScreen2(navController = rememberNavController())
    }
}