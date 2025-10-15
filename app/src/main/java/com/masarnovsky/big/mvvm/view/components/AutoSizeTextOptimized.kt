package com.masarnovsky.big.mvvm.view.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun AutoSizeTextOptimized(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    maxLines: Int = Int.MAX_VALUE,
    fontFamily: FontFamily = FontFamily.Default,
    minFontSize: Float = 20f,
    maxFontSize: Float = 500f
) {
    var fontSizeValue by remember(text) { mutableStateOf(maxFontSize) }
    var minSize by remember(text) { mutableStateOf(minFontSize) }
    var maxSize by remember(text) { mutableStateOf(maxFontSize) }
    var readyToDraw by remember(text) { mutableStateOf(false) }

    // Calculate max lines based on text length
    val maxLines = remember(text) {
        when {
            text.length <= 10 -> 1           // Very short: single line
            text.length < 50 -> 2            // Short: 2 lines max
            text.length < 150 -> 5           // Medium: 5 lines
            else -> 10                       // Long: 10 lines
        }
    }

    val softWrap = text.length > 20

    Text(
        text = text,
        color = if (readyToDraw) color else Color.Transparent,
        fontSize = fontSizeValue.sp,
        fontFamily = fontFamily,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        maxLines = maxLines,
        softWrap = softWrap,
        lineHeight = (fontSizeValue * 1.1f).sp, // Tighter line spacing
        modifier = modifier
//            .fillMaxSize() // probably not needed
            .padding(32.dp) // Safe margin from edges
            .wrapContentSize(Alignment.Center),
        onTextLayout = { textLayoutResult ->
            val overflow = textLayoutResult.didOverflowWidth ||
                    textLayoutResult.didOverflowHeight

            if (!readyToDraw) {
                if (overflow) {
                    // Text too big - search smaller sizes
                    maxSize = fontSizeValue
                    fontSizeValue = (minSize + maxSize) / 2f
                } else {
                    // Text fits - try bigger
                    if ((maxSize - minSize) < 1f) {
                        // Converged - done!
                        readyToDraw = true
                    } else {
                        minSize = fontSizeValue
                        fontSizeValue = (minSize + maxSize) / 2f
                    }
                }
            }
        }
    )
}