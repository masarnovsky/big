package com.masarnovsky.big.mvvm.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.masarnovsky.big.mvvm.Orientation
import com.masarnovsky.big.mvvm.viewmodel.maxLinesToTryLandscapeDefault
import com.masarnovsky.big.mvvm.viewmodel.maxLinesToTryPortraitDefault
import kotlin.math.min

// Constants for binary search and layout precision
private const val BINARY_SEARCH_PRECISION = 0.5f
private const val LAYOUT_TOLERANCE = 0.5f

@Composable
fun AutoSizeTextOptimized(
    text: String,
    modifier: Modifier = Modifier.fillMaxSize(),
    color: Color = Color.White,
    fontFamily: FontFamily = FontFamily.Default,
    fontWeight: FontWeight = FontWeight.Bold,
    orientation: Orientation,
    minFontSize: Float = 6f,
    maxFontSize: Float = 900f,
    paddingHorizontal: Dp = 24.dp,
    paddingVertical: Dp = 24.dp,
    background: Brush
) {
    BoxWithConstraints(modifier = modifier.background(Color.Black)) {
        val boxWidthPx = with(LocalDensity.current) { (maxWidth - paddingHorizontal * 2).toPx() }
        val boxHeightPx = with(LocalDensity.current) { (maxHeight - paddingVertical * 2).toPx() }
        val measurer = rememberTextMeasurer()

        var textFit by remember(text, maxWidth, maxHeight) {
            mutableStateOf(TextFit(text, minFontSize))
        }
        var ready by remember(text, maxWidth, maxHeight) { mutableStateOf(false) }

        // Avoid blocking UI: run calculation in LaunchedEffect; it's synchronous but safe here.
        LaunchedEffect(text, boxWidthPx, boxHeightPx, fontFamily, fontWeight, orientation) {
            ready = false
            val style = TextStyle(
                fontFamily = fontFamily,
                fontWeight = fontWeight,
                textAlign = TextAlign.Center
            )
            textFit = findBestFit(
                text = text,
                measurer = measurer,
                boxWidthPx = boxWidthPx,
                boxHeightPx = boxHeightPx,
                style = style,
                orientation = orientation,
                minFontSize = minFontSize,
                maxFontSize = maxFontSize,
            )
            ready = true
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(background)
                .padding(horizontal = paddingHorizontal, vertical = paddingVertical),
            contentAlignment = Alignment.Center
        ) {
            if (ready) {
                Text(
                    text = textFit.text,
                    color = color,
                    fontSize = textFit.fontSize.sp,
                    fontFamily = fontFamily,
                    fontWeight = fontWeight,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                // (prevents flicker)
                Spacer(modifier = Modifier.size(0.dp))
            }
        }
    }
}

private fun findBestFit(
    text: String,
    measurer: TextMeasurer,
    boxWidthPx: Float,
    boxHeightPx: Float,
    style: TextStyle,
    orientation: Orientation,
    minFontSize: Float,
    maxFontSize: Float,
): TextFit {
    val words = text.trim().split("\\s+".toRegex()).filter { it.isNotEmpty() }
    if (words.isEmpty()) {
        return TextFit("", minFontSize)
    }

    val maxLinesToTry =
        if (orientation == Orientation.LANDSCAPE) maxLinesToTryLandscapeDefault else maxLinesToTryPortraitDefault
    val maxTry = min(maxLinesToTry, words.size)

    var bestFit = TextFit(text, minFontSize)

    // For each candidate number of lines, try to find max font that fits
    for (linesCount in 1..maxTry) {
        val candidateText = buildBalancedLines(words, linesCount)

        val bestFontSizeForCandidate = binarySearchForFontSize(
            text = candidateText,
            measurer = measurer,
            boxWidthPx = boxWidthPx,
            boxHeightPx = boxHeightPx,
            style = style,
            minFontSize = minFontSize,
            maxFontSize = maxFontSize
        )

        val fraction = calculateUsedFraction(
            text = candidateText,
            fontSize = bestFontSizeForCandidate,
            measurer = measurer,
            style = style,
            boxWidthPx = boxWidthPx,
            boxHeightPx = boxHeightPx
        )

        // prefer higher fraction; tie-breaker: bigger font (visually nicer)
        if (fraction > bestFit.usedFraction || (fraction == bestFit.usedFraction && bestFontSizeForCandidate > bestFit.fontSize)) {
            bestFit = TextFit(candidateText, bestFontSizeForCandidate, fraction)
        }
    }

    return bestFit
}

private fun binarySearchForFontSize(
    text: String,
    measurer: TextMeasurer,
    boxWidthPx: Float,
    boxHeightPx: Float,
    style: TextStyle,
    minFontSize: Float,
    maxFontSize: Float,
): Float {
    var low = minFontSize
    var high = maxFontSize
    var bestSize = low

    while (high - low > BINARY_SEARCH_PRECISION) {
        val mid = (low + high) / 2f
        val currentStyle = style.copy(fontSize = mid.sp)
        val layoutResult = measurer.measure(
            text = AnnotatedString(text),
            style = currentStyle,
        )

        val fits =
            layoutResult.size.width <= boxWidthPx + LAYOUT_TOLERANCE && layoutResult.size.height <= boxHeightPx + LAYOUT_TOLERANCE

        if (fits) {
            bestSize = mid
            low = mid
        } else {
            high = mid
        }
    }
    return bestSize
}

private fun calculateUsedFraction(
    text: String,
    fontSize: Float,
    measurer: TextMeasurer,
    style: TextStyle,
    boxWidthPx: Float,
    boxHeightPx: Float,
): Double {
    val finalStyle = style.copy(fontSize = fontSize.sp)
    val finalLayout = measurer.measure(AnnotatedString(text), style = finalStyle)
    val usedW = min(finalLayout.size.width.toFloat(), boxWidthPx)
    val usedH = min(finalLayout.size.height.toFloat(), boxHeightPx)
    val usedArea = usedW * usedH
    val totalArea = boxWidthPx * boxHeightPx
    return if (totalArea > 0f) (usedArea / totalArea).toDouble() else 0.0
}

// create roughly balanced lines preserving order
fun buildBalancedLines(words: List<String>, linesCount: Int): String {
    if (linesCount <= 1) return words.joinToString(" ")
    val total = words.size
    val base = total / linesCount
    val extra = total % linesCount
    val sb = StringBuilder()
    var idx = 0
    for (line in 0 until linesCount) {
        val take = base + if (line < extra) 1 else 0
        if (take <= 0) continue
        val segment = words.slice(idx until idx + take).joinToString(" ")
        if (sb.isNotEmpty()) sb.append("\n")
        sb.append(segment)
        idx += take
    }
    // If leftover words (shouldn't), append them
    if (idx < total) {
        sb.append("\n").append(words.slice(idx until total).joinToString(" "))
    }
    return sb.toString()
}

private data class TextFit(
    val text: String,
    val fontSize: Float,
    val usedFraction: Double = 0.0,
)
