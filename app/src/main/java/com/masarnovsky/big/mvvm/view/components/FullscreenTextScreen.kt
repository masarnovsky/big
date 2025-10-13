package com.masarnovsky.big.mvvm.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            .clickable { onExit() }
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 48.sp,
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold,
            color = textColor,
            textAlign = TextAlign.Center,
            lineHeight = 56.sp,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

fun getBackgroundColor(background: String): Brush {
    return when (background) {
        "black" -> Brush.linearGradient(listOf(Color.Black, Color.Black))
        "white" -> Brush.linearGradient(listOf(Color.White, Color.White))
        else -> getRandomGradient()
    }
}

fun getTextColor(background: String): Color {
    return when (background) {
        "black" -> Color.White
        "white" -> Color.Black
        else -> Color.White
    }
}

fun getFontFamily(font: String): FontFamily {
    return when (font) {
        "Serif" -> FontFamily.Serif
        "Cursive" -> FontFamily.Cursive
        "Monospace" -> FontFamily.Monospace
        else -> FontFamily.Default
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
        )
    )

    return gradients[Random.nextInt(gradients.size)]
}