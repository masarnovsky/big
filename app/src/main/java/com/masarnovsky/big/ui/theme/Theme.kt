package com.masarnovsky.big.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

// Theme 1: "Midnight Slate" (Calm Blue-Gray)
val MidnightSlate = darkColorScheme(
    background = Color(0xFF0D1117),        // Deep blue-black
    primary = Color(0xFF58A6FF),           // Soft blue (accent)
    secondary = Color(0xFF6E7681),         // Gray (inactive)
    tertiary = Color(0xFF161B22),          // History items background
    onBackground = Color(0xFFE6EDF3),      // Off-white (text)
    error = Color(0xFFDA6962)              // Muted red (delete)
)

// Theme 2: "Forest Night" (Calm Green)
val ForestNight = darkColorScheme(
    background = Color(0xFF0D1B0D),        // Dark green-black
    primary = Color(0xFF5FB573),           // Soft green (accent)
    secondary = Color(0xFF3D4A3D),         // Dark gray-green (inactive)
    tertiary = Color(0xFF1A2A1A),          // History items background
    onBackground = Color(0xFFE8F2E8),      // Light green-white (text)
    error = Color(0xFFD97373)              // Soft red (delete)
)

// Theme 3: "Warm Charcoal" (Calm Orange)
val WarmCharcoal = darkColorScheme(
    background = Color(0xFF1A1512),        // Warm dark brown
    primary = Color(0xFFE89A5D),           // Soft orange (accent)
    secondary = Color(0xFF4A4239),         // Warm gray (inactive)
    tertiary = Color(0xFF252017),          // History items background
    onBackground = Color(0xFFF5EDE5),      // Warm white (text)
    error = Color(0xFFD97373)              // Soft red (delete)
)

// Theme 4: "Arctic Minimal" (Calm Purple)
val ArcticMinimal = darkColorScheme(
    background = Color(0xFF0F0F14),        // Cool dark purple-black
    primary = Color(0xFF9D8FE8),           // Soft lavender (accent)
    secondary = Color(0xFF3C3C47),         // Cool gray (inactive)
    tertiary = Color(0xFF1A1A22),          // History items background
    onBackground = Color(0xFFEFEDF5),      // Cool white (text)
    error = Color(0xFFDA6962)              // Muted red (delete)
)

// Theme 5: "Monochrome Pro" (Pure Minimal)
val MonochromePro = darkColorScheme(
    background = Color(0xFF0A0A0A),        // Pure black
    primary = Color(0xFF9E9E9E),           // Medium gray (accent)
    secondary = Color(0xFF2C2C2C),         // Dark gray (inactive)
    tertiary = Color(0xFF1A1A1A),          // History items background
    onBackground = Color(0xFFE0E0E0),      // Light gray (text)
    error = Color(0xFFB85C5C)              // Deep red (delete)
)