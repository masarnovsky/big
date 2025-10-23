package com.masarnovsky.big.mvvm.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.masarnovsky.big.getBackgroundColor
import com.masarnovsky.big.getTextColor
import com.masarnovsky.big.mvvm.BackgroundColor
import com.masarnovsky.big.mvvm.GradientColor
import com.masarnovsky.big.mvvm.Orientation

@Composable
fun FullscreenTextScreen(
    text: String,
    font: String,
    background: BackgroundColor,
    gradient: GradientColor,
    orientation: Orientation,
    onExit: () -> Unit
) {
    val backgroundColor = remember(background, gradient, orientation) {
        getBackgroundColor(
            background,
            gradient
        )
    }
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