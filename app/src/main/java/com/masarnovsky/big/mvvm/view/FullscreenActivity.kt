package com.masarnovsky.big.mvvm.view

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.masarnovsky.big.mvvm.BackgroundColor
import com.masarnovsky.big.mvvm.GradientColor
import com.masarnovsky.big.mvvm.InputFont
import com.masarnovsky.big.mvvm.IntentExtras
import com.masarnovsky.big.mvvm.Orientation
import com.masarnovsky.big.mvvm.view.components.FullscreenTextScreen

class FullscreenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupFullscreen()

        // Safely parse intent extras with error handling
        val displayText = intent.getStringExtra(IntentExtras.DISPLAY_TEXT) ?: "No text provided"

        val selectedFont = try {
            val fontName = intent.getStringExtra(IntentExtras.SELECTED_FONT) ?: InputFont.MONTSERRAT.name
            InputFont.valueOf(fontName)
        } catch (_: IllegalArgumentException) {
            InputFont.MONTSERRAT
        }

        val selectedBackground = try {
            val backgroundName = intent.getStringExtra(IntentExtras.SELECTED_BACKGROUND) ?: BackgroundColor.BLACK.name
            BackgroundColor.valueOf(backgroundName)
        } catch (_: IllegalArgumentException) {
            BackgroundColor.BLACK
        }

        val selectedGradient = try {
            val gradientName = intent.getStringExtra(IntentExtras.SELECTED_GRADIENT) ?: GradientColor.PURPLE_PINK.name
            GradientColor.valueOf(gradientName)
        } catch (_: IllegalArgumentException) {
            GradientColor.PURPLE_PINK
        }

        val selectedOrientation = try {
            val orientationName = intent.getStringExtra(IntentExtras.SELECTED_ORIENTATION) ?: Orientation.LANDSCAPE.name
            Orientation.valueOf(orientationName)
        } catch (_: IllegalArgumentException) {
            Orientation.LANDSCAPE
        }

        requestedOrientation = when (selectedOrientation) {
            Orientation.PORTRAIT -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            else -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }

        setContent {
            FullscreenTextScreen(
                text = displayText,
                inputFont = selectedFont,
                background = selectedBackground,
                orientation = selectedOrientation,
                gradient = selectedGradient,
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
