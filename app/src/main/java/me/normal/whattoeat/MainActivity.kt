package me.normal.whattoeat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import me.normal.whattoeat.compose.MainScreen
import me.normal.whattoeat.ui.theme.WhatToEatTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WhatToEatTheme {
                MainScreen()
            }
        }
    }
}

