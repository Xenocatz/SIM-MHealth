package com.example.sim_mhealth.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.sim_mhealth.R

// Set of Material typography styles to start with
val EuphoriaScript = FontFamily(
    Font(R.font.euphoriascript_regular, FontWeight.Normal),
)

val hindMadurai = FontFamily(
    Font(R.font.hindmadurai_light, FontWeight.Light),
    Font(R.font.hindmadurai_regular, FontWeight.Normal),
    Font(R.font.hindmadurai_medium, FontWeight.Medium),
    Font(R.font.hindmadurai_semibold, FontWeight.SemiBold),
    Font(R.font.hindmadurai_bold, FontWeight.Bold),
)

val martel = FontFamily(
    Font(R.font.martel_extralight, FontWeight.ExtraLight),
    Font(R.font.martel_light, FontWeight.Light),
    Font(R.font.martel_regular, FontWeight.Normal),
    Font(R.font.martel_semibold, FontWeight.SemiBold),
    Font(R.font.martel_bold, FontWeight.Bold),
    Font(R.font.martel_extrabold, FontWeight.ExtraBold),
    Font(R.font.martel_black, FontWeight.Black),
)

val Typography = Typography(
    headlineLarge = TextStyle(
        fontFamily = EuphoriaScript,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp
    ),
    displayLarge = TextStyle(
        fontFamily = martel,
        fontWeight = FontWeight.Normal,
        fontSize = 40.sp
    ), headlineMedium = TextStyle(
        fontFamily = martel,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = hindMadurai,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = hindMadurai,
        fontWeight = FontWeight.Light,
        fontSize = 14.sp
    ),
    labelLarge = TextStyle(
        fontFamily = com.example.sim_mhealth.ui.theme.EuphoriaScript,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp
    )
)
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
