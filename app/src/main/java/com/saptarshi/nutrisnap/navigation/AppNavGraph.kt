package com.saptarshi.nutrisnap.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.saptarshi.nutrisnap.presentation.camera.CameraScreen
import com.saptarshi.nutrisnap.presentation.home.HomeScreen
import com.saptarshi.nutrisnap.presentation.log.LogScreen
import com.saptarshi.nutrisnap.presentation.settings.SettingsScreen


@Composable
fun AppNavGraph() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen()
        }
        composable("camera") {
            CameraScreen()
        }
        composable("log") {
            LogScreen()
        }
        composable("settings") {
            SettingsScreen()
        }
    }
}