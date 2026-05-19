package com.saptarshi.nutrisnap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.saptarshi.nutrisnap.navigation.AppNavGraph
import com.saptarshi.nutrisnap.ui.theme.NutriSnapTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NutriSnapTheme {
                AppNavGraph()
            }
        }
    }
}
