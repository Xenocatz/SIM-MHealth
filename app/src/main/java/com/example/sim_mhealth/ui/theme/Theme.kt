package com.example.sim_mhealth.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Primary600,
    onPrimary = Color.White,

    secondary = MintGreen500,
    onSecondary = DarkGray900,

    background = DarkGray900,
    onBackground = Gray50,

    surface = Gray700,
    onSurface = Gray50,

    error = Error,
    onError = Color.Black,

    primaryContainer = Primary600,
    onPrimaryContainer = Primary50,

    secondaryContainer = MintGreen500,
    onSecondaryContainer = MintGreen50,

    surfaceVariant = Gray700,
    onSurfaceVariant = Gray200,
)


private val LightColorScheme = lightColorScheme(
    primary = Primary500,
    onPrimary = Color.White,

    secondary = MintGreen500,
    onSecondary = Color.White,

    background = Gray50,
    onBackground = DarkGray900,

    surface = Color.White,
    onSurface = DarkGray900,

    error = Error,
    onError = Color.White,

    // Optional slots untuk warna custom turunan
    primaryContainer = Primary50,
    onPrimaryContainer = Primary600,

    secondaryContainer = MintGreen50,
    onSecondaryContainer = MintGreen500,

    surfaceVariant = Gray200,
    onSurfaceVariant = Gray700,
)

@Composable
fun SIMMHealthTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}