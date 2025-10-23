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
import com.masarnovsky.big.mvvm.view.components.FullscreenTextScreen

class FullscreenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupFullscreen()

        val displayText = intent.getStringExtra("DISPLAY_TEXT") ?: "No text provided" // ask: what is intent and why i need this?
        val selectedFontName = intent.getStringExtra("SELECTED_FONT") ?: InputFont.MONTSERRAT.name
        val selectedFont = InputFont.valueOf(selectedFontName)
        val selectedBackgroundName = intent.getStringExtra("SELECTED_BACKGROUND") ?: BackgroundColor.BLACK.name
        val selectedBackground = BackgroundColor.valueOf(selectedBackgroundName)
        val selectedGradientName = intent.getStringExtra("SELECTED_GRADIENT") ?: GradientColor.PURPLE_PINK.name
        val selectedGradient = GradientColor.valueOf(selectedGradientName)
        val selectedOrientationName = intent.getStringExtra("SELECTED_ORIENTATION") ?: Orientation.LANDSCAPE.name
        val selectedOrientation = Orientation.valueOf(selectedOrientationName)

        requestedOrientation = when (selectedOrientation) {
            Orientation.PORTRAIT -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            else -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }

        setContent { // ask: what purpose? open new view?
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
