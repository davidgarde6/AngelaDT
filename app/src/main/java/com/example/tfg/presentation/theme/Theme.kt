package com.example.tfg.presentation.theme

import androidx.compose.runtime.Composable
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Colors
import androidx.compose.ui.graphics.Color

private val pastelColorPalette = Colors(
    primary = Color(0xFF9C8199),          // Color principal
    primaryVariant = Color(0xFF6B7D7D),   // Variante
    secondary = Color(0xFFF4F1DE),        // Secundario
    background = Color(0xFFF1E3E4),       // Fondo
    surface = Color(0xFFF1E3E4),          // Superficie
    onPrimary = Color(0xFFFFFFFF),
    onSecondary = Color(0xFF1C1D21),
    onBackground = Color(0xFF1C1D21),
    onSurface = Color(0xFF1C1D21)
)

@Composable
fun TFGTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = pastelColorPalette,
        content = content
    )
}
