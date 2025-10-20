package com.masarnovsky.big.mvvm.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

@Composable
fun FullscreenTextScreen(
    text: String,
    font: String,
    background: String,
    onExit: () -> Unit
) {
    val backgroundColor = remember(background) { getBackgroundColor(background) }
    val textColor = remember(background) { getTextColor(background) }
    val fontFamily = remember(font) { getFontFamily(font) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .clickable { onExit() },
        contentAlignment = Alignment.Center
    ) {
        AutoSizeTextOptimized(
            text = text,
            color = textColor,
            fontFamily = fontFamily,
            modifier = Modifier.fillMaxSize()
        )
    }
}

fun getRandomGradient(): Brush {
    val gradients = listOf(
        // Purple to Pink
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF6200EE),
                Color(0xFFBB86FC)
            )
        ),
        // Blue to Purple
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF2196F3),
                Color(0xFF9C27B0)
            )
        ),
        // Pink to Orange
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFFE91E63),
                Color(0xFFFF9800)
            )
        ),
        // Teal to Blue
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF009688),
                Color(0xFF2196F3)
            )
        ),
        // Deep Purple to Indigo
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF673AB7),
                Color(0xFF3F51B5)
            )
        ),
        // Red to Purple
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFFF44336),
                Color(0xFF9C27B0)
            )
        ),
        //https://coolors.co/gradients
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF42047e),
                Color(0xFF07f49e)
            )
        ),
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFFf4f269),
                Color(0xFF5cb270)
            )
        ),
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFFffb88e),
                Color(0xFFea5753)
            )
        )
    )

    return gradients[Random.nextInt(gradients.size)]
}