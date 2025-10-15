package com.masarnovsky.big.mvvm.view

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.masarnovsky.big.mvvm.view.components.FullscreenTextScreen

class FullscreenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable fullscreen mode
        setupFullscreen()

        val displayText = intent.getStringExtra("DISPLAY_TEXT") ?: "No text provided"
        val selectedFont = intent.getStringExtra("SELECTED_FONT") ?: "Montserrat"
        val selectedBackground = intent.getStringExtra("SELECTED_BACKGROUND") ?: "black"
        val selectedOrientation = intent.getStringExtra("SELECTED_ORIENTATION") ?: "landscape"

        // Set orientation
        requestedOrientation = when (selectedOrientation) {
            "portrait" -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            else -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }

        setContent {
            FullscreenTextScreen(
                text = displayText,
                font = selectedFont,
                background = selectedBackground,
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
