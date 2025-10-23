package com.masarnovsky.big.mvvm

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import kotlin.random.Random

fun getBackgroundColor(background: BackgroundColor, gradient: GradientColor, enabled: Boolean = true): Brush {
    val alpha = if (enabled) 1f else 0.38f

    return when (background) {
        BackgroundColor.BLACK -> SolidColor(Color.Black.copy(alpha = alpha))
        BackgroundColor.WHITE -> SolidColor(Color.White.copy(alpha = alpha))
        else -> gradient.toBrush(alpha)
    }
}

fun getTextColor(background: BackgroundColor, enabled: Boolean = true): Color {
    val alpha = if (enabled) 1f else 0.38f

    return when (background) {
        BackgroundColor.WHITE -> Color.Black.copy(alpha = alpha)
        else -> Color.White.copy(alpha = alpha)
    }
}

fun getRandomGradient(): GradientColor =
    GradientColor.entries[Random.nextInt(GradientColor.entries.size)]