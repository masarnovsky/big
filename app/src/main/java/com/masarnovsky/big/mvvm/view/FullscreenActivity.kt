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
import com.masarnovsky.big.mvvm.Orientation
import com.masarnovsky.big.mvvm.view.IntentExtras.DISPLAY_TEXT
import com.masarnovsky.big.mvvm.view.IntentExtras.SELECTED_BACKGROUND
import com.masarnovsky.big.mvvm.view.IntentExtras.SELECTED_FONT
import com.masarnovsky.big.mvvm.view.IntentExtras.SELECTED_GRADIENT
import com.masarnovsky.big.mvvm.view.IntentExtras.SELECTED_ORIENTATION
import com.masarnovsky.big.mvvm.view.components.FullscreenTextScreen

class FullscreenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupFullscreen()

        val displayText = intent.getStringExtra(DISPLAY_TEXT) ?: "No text provided"

        val selectedFont = try {
            val fontName = intent.getStringExtra(SELECTED_FONT) ?: InputFont.MONTSERRAT.name
            InputFont.valueOf(fontName)
        } catch (_: IllegalArgumentException) {
            InputFont.MONTSERRAT
        }

        val selectedBackground = try {
            val backgroundName = intent.getStringExtra(SELECTED_BACKGROUND) ?: BackgroundColor.BLACK.name
            BackgroundColor.valueOf(backgroundName)
        } catch (_: IllegalArgumentException) {
            BackgroundColor.BLACK
        }

        val selectedGradient = try {
            val gradientName = intent.getStringExtra(SELECTED_GRADIENT) ?: GradientColor.PURPLE_PINK.name
            GradientColor.valueOf(gradientName)
        } catch (_: IllegalArgumentException) {
            GradientColor.PURPLE_PINK
        }

        val selectedOrientation = try {
            val orientationName = intent.getStringExtra(SELECTED_ORIENTATION) ?: Orientation.LANDSCAPE.name
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
