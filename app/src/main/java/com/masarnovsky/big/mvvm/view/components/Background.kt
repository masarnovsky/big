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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


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
                value = value,
                isSelected = selectedBackground == value,
                onClick = { onBackgroundSelected(value) }
            )
        }
    }
}

@Composable
fun BackgroundOption(
    label: String,
    value: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .height(48.dp)
            .background(
                color = if (isSelected) Color(0xFF6200EE) else MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 2.dp,
                color = if (isSelected) Color(0xFF6200EE) else Color(0xFF444444),
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