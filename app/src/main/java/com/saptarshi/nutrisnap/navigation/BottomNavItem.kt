package com.saptarshi.nutrisnap.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    data object Home : BottomNavItem("home", "Home", Icons.Default.Home)
    data object Camera : BottomNavItem("camera", "Snap", Icons.Default.PhotoCamera)
    data object Log : BottomNavItem("log", "Log", Icons.Default.DateRange)
    data object Settings : BottomNavItem("settings", "Settings", Icons.Default.Settings)
}
