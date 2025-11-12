package com.masarnovsky.big.mvvm

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import kotlin.random.Random

val blackColor = Color(0xff1A1A1A)
val whiteColor = Color(0XffFAFAFA)

fun getBackgroundColor(
    background: BackgroundColor,
    gradient: GradientColor,
    enabled: Boolean = true
): Brush {
    val alpha = if (enabled) 1f else 0.38f

    return when (background) {
        BackgroundColor.BLACK -> SolidColor(blackColor.copy(alpha = alpha))
        BackgroundColor.WHITE -> SolidColor(whiteColor.copy(alpha = alpha))
        else -> gradient.toBrush(alpha)
    }
}

fun getTextColor(background: BackgroundColor, enabled: Boolean = true): Color {
    val alpha = if (enabled) 1f else 0.38f

    return when (background) {
        BackgroundColor.WHITE -> blackColor.copy(alpha = alpha)
        else -> whiteColor.copy(alpha = alpha)
    }
}

fun getRandomGradient(): GradientColor =
    GradientColor.entries[Random.nextInt(GradientColor.entries.size)]