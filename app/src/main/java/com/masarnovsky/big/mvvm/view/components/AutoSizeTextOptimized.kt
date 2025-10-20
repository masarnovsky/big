package com.masarnovsky.big.mvvm.view.components

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
    fontFamily: FontFamily = FontFamily.Default,
    minFontSize: Float = 20f,
    maxFontSize: Float = 500f
) {
    var fontSizeValue by remember(text) { mutableStateOf(maxFontSize) }
    var minSize by remember(text) { mutableStateOf(minFontSize) }
    var maxSize by remember(text) { mutableStateOf(maxFontSize) }
    var readyToDraw by remember(text) { mutableStateOf(false) }

    val (maxLines, softWrap) = remember(text) {
        when {
            // Single word or very short - force single line
            text.trim().split("\\s+".toRegex()).size == 1 && text.length <= 20 ->
                Pair(1, false)

            // Very short text - prefer single line
            text.length <= 10 ->
                Pair(1, false)

            // Short text - allow minimal wrapping
            text.length < 50 ->
                Pair(2, true)

            // Medium text
            text.length < 150 ->
                Pair(5, true)

            // Long text
            else ->
                Pair(10, true)
        }
    }

    Text(
        text = text,
        color = if (readyToDraw) color else Color.Transparent,
        fontSize = fontSizeValue.sp,
        fontFamily = fontFamily,
        fontWeight = FontWeight.Bold, // FIXME: delete this one?
        textAlign = TextAlign.Center,
        maxLines = maxLines,
        softWrap = softWrap,
        lineHeight = (fontSizeValue * 1.1f).sp,
        modifier = modifier
            .padding(32.dp)
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