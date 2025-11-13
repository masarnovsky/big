package com.masarnovsky.big.mvvm.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.masarnovsky.big.mvvm.BackgroundColor
import com.masarnovsky.big.mvvm.GradientColor
import com.masarnovsky.big.mvvm.InputFont
import com.masarnovsky.big.mvvm.Orientation
import com.masarnovsky.big.mvvm.getBackgroundColor
import com.masarnovsky.big.mvvm.getTextColor
import com.masarnovsky.big.mvvm.viewmodel.ellipsis
import com.masarnovsky.big.mvvm.viewmodel.maxAmountOfSymbolsOnShowButton
import com.masarnovsky.big.mvvm.viewmodel.space

@Composable
fun PreviewButton(
    text: String,
    inputFont: InputFont,
    background: BackgroundColor,
    gradient: GradientColor,
    orientation: Orientation,
    onShowFullscreen: () -> Unit,
    enabled: Boolean
) {
    val brush = getBackgroundColor(background, gradient, enabled)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(12.dp),
                clip = false,
                ambientColor = Color.Black.copy(alpha = if (enabled) 1f else 0.1f),
                spotColor = Color.Black.copy(alpha = if (enabled) 1f else 0.1f)
            )
            .clip(RoundedCornerShape(12.dp))
            .background(
                brush = brush
            )
            .clickable(enabled = enabled) { onShowFullscreen() }
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = formatFullscreenText(text),
            fontSize = 18.sp,
            fontFamily = inputFont.fontFamily,
            fontWeight = FontWeight.Bold,
            color = getTextColor(background, enabled),
            textAlign = TextAlign.Center,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
    }
}

private fun formatFullscreenText(text: String): String {
    return when {
        text.length > maxAmountOfSymbolsOnShowButton -> space + text.take(
            maxAmountOfSymbolsOnShowButton
        ) + ellipsis

        text.isNotEmpty() -> space + text
        else -> "input text to show"
    }
}