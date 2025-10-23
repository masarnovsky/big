package com.masarnovsky.big.mvvm

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.masarnovsky.big.R

enum class BackgroundColor(val label: String) {
    BLACK("Black"), WHITE("White"), GRADIENT("Gradient")
}

enum class Orientation(val label: String) {
    LANDSCAPE("Landscape"), PORTRAIT("Portrait")
}

enum class InputFont(val label: String, val fontFamily: FontFamily) {
    MONTSERRAT("Montserrat", FontFamily(Font(R.font.montserrat_black, FontWeight.Black))),
    PANGOLIN("Pangolin", FontFamily(Font(R.font.pangolin_regular, FontWeight.Normal))),
    ROBOTO_SLAB("Roboto", FontFamily(Font(R.font.robotoslab_regular, FontWeight.Normal))),
    PLAYFAIR_DISPLAY("Playfair", FontFamily(Font(R.font.playfairdisplay_regular, FontWeight.Normal)));
}

enum class GradientColor(val displayName: String, val colors: List<Color>) {
    PURPLE_PINK(
        displayName = "Purple to Pink",
        colors = listOf(
            Color(0xFF6200EE),
            Color(0xFFBB86FC)
        )
    ),
    BLUE_PURPLE(
        displayName = "Blue to Purple",
        colors = listOf(
            Color(0xFF2196F3),
            Color(0xFF9C27B0)
        )
    ),
    PINK_ORANGE(
        displayName = "Pink to Orange",
        colors = listOf(
            Color(0xFFE91E63),
            Color(0xFFFF9800)
        )
    ),
    TEAL_BLUE(
        displayName = "Teal to Blue",
        colors = listOf(
            Color(0xFF009688),
            Color(0xFF2196F3)
        )
    ),
    RED_PURPLE(
        displayName = "Red to Purple",
        colors = listOf(
            Color(0xFFF44336),
            Color(0xFF9C27B0)
        )
    ),
    DARK_PURPLE_CYAN(
        displayName = "Dark Purple to Cyan",
        colors = listOf(
            Color(0xFF42047e),
            Color(0xFF07f49e)
        )
    ),
    YELLOW_GREEN(
        displayName = "Yellow to Green",
        colors = listOf(
            Color(0xFFf4f269),
            Color(0xFF5cb270)
        )
    ),
    PEACH_RED(
        displayName = "Peach to Red",
        colors = listOf(
            Color(0xFFffb88e),
            Color(0xFFea5753)
        )
    );

    fun toBrush(alpha: Float): Brush {
        return Brush.linearGradient(
            colors = colors,
            start = Offset(0f, 0f),
            end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
        )
    }

}