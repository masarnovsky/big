package com.masarnovsky.big.ui.theme

import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val monochromeLight = lightColorScheme(
    background = Color(0xFFF5F5F5),        // Light gray canvas
    primary = Color(0xFF3A3A3A),           // Dark gray (selected elements)
    secondary = Color(0xFFE5E5E5),         // Light gray (unselected elements)
    tertiary = Color(0xFFFFFFFF),          // White (cards/history items background)
    onBackground = Color(0xFF1A1A1A),      // Near black (primary text)
    error = Color(0xFFDC2626)              // Bright red (delete icons)
)