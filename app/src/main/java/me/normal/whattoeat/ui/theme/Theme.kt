package me.normal.whattoeat.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val PurpleDarkScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val PurpleLightScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

private val BlueLightScheme = lightColorScheme(
    primary = BluePrimary,
    secondary = NeutralSecondary,
    tertiary = NeutralTertiary
)

private val YellowLightScheme = lightColorScheme(
    primary = YellowPrimary,
    secondary = NeutralSecondary,
    tertiary = NeutralTertiary
)

private val GreenLightScheme = lightColorScheme(
    primary = GreenPrimary,
    secondary = NeutralSecondary,
    tertiary = NeutralTertiary
)

private val PinkLightScheme = lightColorScheme(
    primary = PinkPrimary,
    secondary = NeutralSecondary,
    tertiary = NeutralTertiary
)


enum class ColorTheme{
    Blue,
    Yellow,
    Green,
    Pink,
    Purple;

    fun toColorScheme(darkTheme: Boolean): ColorScheme {
        return when (this) {
            ColorTheme.Blue -> BlueLightScheme
            ColorTheme.Yellow -> YellowLightScheme
            ColorTheme.Green -> GreenLightScheme
            ColorTheme.Pink -> PinkLightScheme
            ColorTheme.Purple -> if (darkTheme) PurpleDarkScheme else PurpleLightScheme
        }
    }
}



@Composable
fun WhatToEatTheme(
    colorTheme: ColorTheme,
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {

//    if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//        val context = LocalContext.current
//        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
//    } else { 系统动态色彩

    val colorScheme = colorTheme.toColorScheme(darkTheme)


    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}