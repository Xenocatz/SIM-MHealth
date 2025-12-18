package com.example.sim_mhealth.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.sim_mhealth.ui.theme.DarkGray900
import com.example.sim_mhealth.ui.theme.Gray50
import com.example.sim_mhealth.ui.theme.Primary500

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem("Beranda", Icons.Default.Home, "home_screen"),
        BottomNavItem("Pengingat", Icons.Default.NotificationsNone, "reminder_screen"),
        BottomNavItem("AI", Icons.Default.StarBorder, "ai_screen"),
        BottomNavItem("Profil", Icons.Default.PersonOutline, "profile_screen")
    )

    val activeColor = Primary500
    val inactiveColor = DarkGray900

    NavigationBar(
        containerColor = Gray50,
        tonalElevation = 0.dp
    ) {
        val navBackStackEntry = navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry.value?.destination?.route

        items.forEach { item ->
            val isSelected = currentRoute == item.route

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = if (isSelected) activeColor else inactiveColor
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Normal,
                        color = if (isSelected) activeColor else inactiveColor
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Gray50
                )
            )
        }
    }
}

@Preview
@Composable
fun BottomNavigationBarPreview() {
    BottomNavigationBar(navController = rememberNavController())
}
