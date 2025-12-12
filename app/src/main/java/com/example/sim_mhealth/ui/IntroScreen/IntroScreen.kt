package com.example.sim_mhealth.ui.IntroScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.sim_mhealth.R
import com.example.sim_mhealth.ui.theme.SIMMHealthTheme

@Composable
fun IntroScreen(modifier: Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        BackgroundImage()
    }
}

@Composable
fun BackgroundImage() {
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
                    colors = listOf(
                        Color.Transparent,
                        MaterialTheme.colorScheme.onBackground
                    )
                )
            )
    ) {

    }
}

@Preview(showBackground = true)
@Composable
fun IntroScreenPreview() {
    SIMMHealthTheme {
        IntroScreen(modifier = Modifier.fillMaxWidth())
    }
}