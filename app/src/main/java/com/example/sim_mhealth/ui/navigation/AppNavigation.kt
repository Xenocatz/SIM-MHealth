package com.example.sim_mhealth.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.sim_mhealth.ui.auth.LoginScreen
import com.example.sim_mhealth.ui.auth.RegisterScreen
import com.example.sim_mhealth.ui.introScreen.IntroScreen
import com.example.sim_mhealth.ui.onBoardingScreen.OnBoardingScreen1
import com.example.sim_mhealth.ui.onBoardingScreen.OnBoardingScreen2
import com.example.sim_mhealth.ui.onBoardingScreen.OnBoardingScreen3
import com.example.sim_mhealth.ui.dashboard.DashboardScreen
import com.example.sim_mhealth.ui.reminder.ReminderScreen
import com.example.sim_mhealth.ui.ai.AIScreen
import com.example.sim_mhealth.ui.profile.ProfileScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomBarRoutes = listOf(
        "home_screen", "reminder_screen", "ai_screen", "profile_screen"
    )
    val shouldShowBottomBar = currentRoute in bottomBarRoutes

    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar) {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "intro_screen",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("intro_screen") { _ ->
                IntroScreen(navController = navController)
            }
            composable("onboarding_screen_1") { _ ->
                OnBoardingScreen1(navController = navController)
            }
            composable("onboarding_screen_2") { _ ->
                OnBoardingScreen2(navController = navController)
            }
            composable("onboarding_screen_3") { _ ->
                OnBoardingScreen3(navController = navController)
            }
            composable("login_screen") { _ ->
                LoginScreen(navController = navController)
            }
            composable("register_screen") { _ ->
                RegisterScreen(navController = navController)
            }
            composable("home_screen") { _ ->
                DashboardScreen(navController = navController)
            }
            composable("reminder_screen") { _ ->
                ReminderScreen()
            }
            composable("ai_screen") { _ ->
                AIScreen()
            }
            composable("profile_screen") { _ ->
                ProfileScreen(navController = navController)
            }
        }
    }
}