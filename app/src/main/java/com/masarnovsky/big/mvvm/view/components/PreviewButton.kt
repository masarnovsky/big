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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.masarnovsky.big.mvvm.viewmodel.ellipsis
import com.masarnovsky.big.mvvm.viewmodel.maxAmountOfSymbolsOnShowButton
import com.masarnovsky.big.mvvm.viewmodel.space

@Composable
fun PreviewButton(
    text: String,
    font: String,
    background: String,
    orientation: String,
    onShowFullscreen: () -> Unit,
    enabled: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                brush = when (background) {
                    "gradient" -> Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF667eea),
                            Color(0xFF764ba2),
                            Color(0xFFf093fb)
                        )
                    )
                    "white" -> SolidColor(Color.White)
                    else -> SolidColor(Color.Black)
                }
            )
            .clickable(enabled = enabled) { onShowFullscreen() }
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = formatFullscreenText(text),
            fontSize = 18.sp,
            fontFamily = getFontFamily(font),
            fontWeight = FontWeight.Bold,
            color = if (background == "white") Color.Black else Color.White,
            textAlign = TextAlign.Center,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
    }
}

private fun formatFullscreenText(text: String) : String {
    return when {
        text.length > maxAmountOfSymbolsOnShowButton -> space + text.take(maxAmountOfSymbolsOnShowButton) + ellipsis
        text.isNotEmpty() -> space + text
        else -> "this is the preview"
    }
}