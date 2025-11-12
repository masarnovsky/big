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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
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
    paddingVertical: Dp = 24.dp
) {
    BoxWithConstraints(modifier = modifier.background(Color.Black)) {
        val boxWidthPx = with(LocalDensity.current) { (maxWidth - paddingHorizontal * 2).toPx() }
        val boxHeightPx = with(LocalDensity.current) { (maxHeight - paddingVertical * 2).toPx() }
        val measurer = rememberTextMeasurer()

        var chosenText by remember(text, maxWidth, maxHeight) { mutableStateOf(text) }
        var chosenFontSize by remember(
            text,
            maxWidth,
            maxHeight
        ) { mutableFloatStateOf(minFontSize) }
        var ready by remember(text, maxWidth, maxHeight) { mutableStateOf(false) }

        // Avoid blocking UI: run calculation in LaunchedEffect; it's synchronous but safe here.
        LaunchedEffect(text, boxWidthPx, boxHeightPx) {
            ready = false

            val words = text.trim().split("\\s+".toRegex()).filter { it.isNotEmpty() }
            if (words.isEmpty()) {
                chosenText = ""
                chosenFontSize = minFontSize
                ready = true
                return@LaunchedEffect
            }

            val maxLinesToTry =
                if (orientation == Orientation.LANDSCAPE) maxLinesToTryLandscapeDefault else maxLinesToTryPortraitDefault
            val maxTry = min(maxLinesToTry, words.size)

            var bestFont = minFontSize
            var bestBreak = text
            var bestUsedFraction = 0.0

            // For each candidate number of lines, try to find max font that fits via binary search
            for (linesCount in 1..maxTry) {
                val candidate = buildBalancedLines(words, linesCount)

                // binary search font size for this candidate
                var lo = minFontSize
                var hi = maxFontSize
                var bestForCandidate = lo

                while (hi - lo > 0.5f) {
                    val mid = (lo + hi) / 2f
                    val style = TextStyle(
                        fontSize = mid.sp,
                        fontFamily = fontFamily,
                        fontWeight = fontWeight,
                        textAlign = TextAlign.Center
                    )
                    val layoutResult = measurer.measure(
                        text = AnnotatedString(candidate),
                        style = style,
                        maxLines = Int.MAX_VALUE
                    )
                    val fitsWidth = layoutResult.size.width <= boxWidthPx + 0.5f
                    val fitsHeight = layoutResult.size.height <= boxHeightPx + 0.5f

                    if (fitsWidth && fitsHeight) {
                        bestForCandidate = mid
                        lo = mid
                    } else {
                        hi = mid
                    }
                }

                // Measure final using bestForCandidate to compute used fraction
                val finalStyle = TextStyle(
                    fontSize = bestForCandidate.sp,
                    fontFamily = fontFamily,
                    fontWeight = fontWeight,
                    textAlign = TextAlign.Center
                )
                val finalLayout = measurer.measure(
                    AnnotatedString(candidate),
                    style = finalStyle,
                    maxLines = Int.MAX_VALUE
                )
                val usedW = min(finalLayout.size.width, boxWidthPx.toInt())
                val usedH = min(finalLayout.size.height, boxHeightPx.toInt())
                val usedArea = usedW * usedH
                val totalArea = boxWidthPx * boxHeightPx
                val fraction = if (totalArea > 0f) (usedArea / totalArea).toDouble() else 0.0

                // prefer higher fraction; tie-breaker: bigger font (visually nicer)
                if (fraction > bestUsedFraction || (fraction == bestUsedFraction && bestForCandidate > bestFont)) {
                    bestUsedFraction = fraction
                    bestFont = bestForCandidate
                    bestBreak = candidate
                }
            }

            // Final chosen values
            chosenText = bestBreak
            chosenFontSize = bestFont
            ready = true
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = paddingHorizontal, vertical = paddingVertical),
            contentAlignment = Alignment.Center
        ) {
            if (ready) {
                Text(
                    text = chosenText,
                    color = color,
                    fontSize = chosenFontSize.sp,
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