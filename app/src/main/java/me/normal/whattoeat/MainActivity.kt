package me.normal.whattoeat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import me.normal.whattoeat.compose.MainScreen
import me.normal.whattoeat.data.local.config.Config
import me.normal.whattoeat.ui.theme.ColorTheme
import me.normal.whattoeat.ui.theme.WhatToEatTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val colorTheme by Config.colorThemeFlow(this) // 获取colorTheme这个枚举类型的流
                .collectAsState(ColorTheme.Pink) // 并且为State，一更新就会通知ui更新

            WhatToEatTheme(colorTheme) {
                MainScreen()
            }
        }
    }
}

