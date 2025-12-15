package com.example.sim_mhealth.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sim_mhealth.ui.auth.LoginScreen
import com.example.sim_mhealth.ui.auth.RegisterScreen
import com.example.sim_mhealth.ui.introScreen.IntroScreen
import com.example.sim_mhealth.ui.onBoardingScreen.OnBoardingScreen1
import com.example.sim_mhealth.ui.onBoardingScreen.OnBoardingScreen2
import com.example.sim_mhealth.ui.onBoardingScreen.OnBoardingScreen3

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController, startDestination = "intro_screen"
    ) {
        composable("intro_screen") {
            IntroScreen(navController = navController)
        }
        composable("onboarding_screen_1") {
            OnBoardingScreen1(navController = navController)
        }
        composable("onboarding_screen_2") {
            OnBoardingScreen2(navController = navController)
        }
        composable("onboarding_screen_3") {
            OnBoardingScreen3(navController = navController)
        }
        composable("login_screen") {
            LoginScreen(navController = navController)
        }
        composable("register_screen") {
            RegisterScreen(navController = navController)
        }
    }
}
