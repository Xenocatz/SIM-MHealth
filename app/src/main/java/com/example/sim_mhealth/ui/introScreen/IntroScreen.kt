package com.example.sim_mhealth.ui.introScreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.sim_mhealth.R
import com.example.sim_mhealth.ui.theme.SIMMHealthTheme

@Composable
fun IntroScreen(modifier: Modifier = Modifier, navController: NavHostController) {
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        BackgroundImage()
        Content(
            navController = navController,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(24.dp),
        )
    }
}

@Composable
fun BackgroundImage() {
    val solidColor = MaterialTheme.colorScheme.background
    Image(
        painter = painterResource(id = R.drawable.forest_jogging_group),
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop,
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colorStops = arrayOf(
                        0.0f to Color.Transparent,
                        0.75f to solidColor
                    ),
                )
            )
    ) {

    }
}

@Composable
fun Content(modifier: Modifier = Modifier, navController: NavHostController) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "MHealth",
            style = MaterialTheme.typography.headlineLarge,
            fontSize = 54.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.padding(28.dp))
        Text(
            text = buildAnnotatedString {
                append("Mulai ")
                withStyle(SpanStyle(color = MaterialTheme.colorScheme.secondary)) {
                    append("Perjalanan Sehat")
                }
                append(" Anda Hari Ini")
            },
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 28.sp,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.displayLarge,
            lineHeight = 40.sp
        )
        Spacer(Modifier.padding(12.dp))
        Text(
            text = "Akses cepat ke jadwal obat, catatan kesehatan harian, serta rekomendasi gaya hidup dari AI.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
        Spacer(Modifier.padding(22.dp))
        PrimaryButton(
            text = "Login",
            onClick = { navController.navigate("login_screen") }
        )
        Spacer(Modifier.padding(5.dp))
        OutlinedPrimaryButton(
            text = "Register",
            onClick = { navController.navigate("register_screen") }
        )
    }
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text,
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 20.sp
        )
    }
}

@Composable
fun OutlinedPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primaryContainer),
    ) {
        Text(
            text,
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 20.sp
        )
    }
}


//@Preview(showBackground = true)
//@Composable
//fun IntroScreenPreview() {
//    SIMMHealthTheme {
//        IntroScreen(navController = NavHostController(), modifier = Modifier.fillMaxWidth())
//    }
//}