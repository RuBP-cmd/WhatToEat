package me.normal.whattoeat.model

import androidx.compose.runtime.Composable

data class Cell(
    val content: @Composable () -> Unit,
    val weight: Float
)