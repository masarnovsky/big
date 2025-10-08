package com.masarnovsky.big

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import kotlin.random.Random

class FullscreenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable fullscreen mode
        setupFullscreen()

        val displayText = intent.getStringExtra("DISPLAY_TEXT") ?: "No text provided"

        setContent {
            FullscreenTextScreen(
                text = displayText,
                onExit = { finish() }
            )
        }
    }

    private fun setupFullscreen() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}

@Composable
fun FullscreenTextScreen(
    text: String,
    onExit: () -> Unit
) {
    val gradient = remember { getRandomGradient() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .clickable { onExit() }
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center,
            lineHeight = 56.sp,
            modifier = Modifier.fillMaxWidth()
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
        )
    )

    return gradients[Random.nextInt(gradients.size)]
}