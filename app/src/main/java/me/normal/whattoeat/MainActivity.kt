package me.normal.whattoeat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import me.normal.whattoeat.ui.screens.MainScreen
import me.normal.whattoeat.ui.theme.ColorTheme
import me.normal.whattoeat.ui.theme.WhatToEatTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val application = LocalContext.current.applicationContext as MainApplication
            val colorTheme by application.settingsRepository.colorThemeFlow.collectAsState(ColorTheme.Pink)

            WhatToEatTheme(colorTheme) {
                MainScreen()
            }
        }
    }
}

