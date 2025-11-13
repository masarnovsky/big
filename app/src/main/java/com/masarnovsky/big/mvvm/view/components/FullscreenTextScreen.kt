package com.masarnovsky.big.mvvm.view.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.masarnovsky.big.mvvm.BackgroundColor
import com.masarnovsky.big.mvvm.GradientColor
import com.masarnovsky.big.mvvm.InputFont
import com.masarnovsky.big.mvvm.Orientation
import com.masarnovsky.big.mvvm.getBackgroundColor
import com.masarnovsky.big.mvvm.getTextColor

@Composable
fun FullscreenTextScreen(
    text: String,
    inputFont: InputFont,
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
    val fontFamily = remember(inputFont) { inputFont.fontFamily }

    AutoSizeTextOptimized(
        text = text,
        color = textColor,
        fontFamily = fontFamily,
        background = backgroundColor,
        orientation = orientation,
        modifier = Modifier
            .fillMaxSize()
            .clickable { onExit() }
    )
}