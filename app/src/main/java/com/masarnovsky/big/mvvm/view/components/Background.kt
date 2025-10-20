package com.masarnovsky.big.mvvm.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

fun getBackgroundColor(background: String): Brush {
    return when (background) {
        "black" -> Brush.linearGradient(listOf(Color.Black, Color.Black))
        "white" -> Brush.linearGradient(listOf(Color.White, Color.White))
        else -> getRandomGradient()
    }
}

fun getTextColor(background: String): Color {
    return when (background) {
        "black" -> Color.White
        "white" -> Color.Black
        else -> Color.White
    }
}

@Composable
fun BackgroundSelector(
    selectedBackground: String,
    onBackgroundSelected: (String) -> Unit
) {
    val backgrounds = listOf(
        "black" to "Black",
        "white" to "White",
        "gradient" to "Gradient"
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        backgrounds.forEach { (value, label) ->
            BackgroundOption(
                label = label,
                isSelected = selectedBackground == value,
                onClick = { onBackgroundSelected(value) }
            )
        }
    }
}

@Composable
fun BackgroundOption(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .height(48.dp)
            .background(
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary,
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 2.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
        )
    }
}