package com.masarnovsky.big.mvvm.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun FontSelector(
    selectedFont: String,
    onFontSelected: (String) -> Unit
) {
    val fonts = listOf(
        "Default" to FontFamily.Default,
        "Serif" to FontFamily.Serif,
        "Cursive" to FontFamily.Cursive,
        "Monospace" to FontFamily.Monospace
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(fonts) { (fontName, fontFamily) ->
            FontOption(
                fontName = fontName,
                fontFamily = fontFamily,
                isSelected = selectedFont == fontName,
                onClick = { onFontSelected(fontName) }
            )
        }
    }
}

@Composable
fun FontOption(
    fontName: String,
    fontFamily: FontFamily,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(80.dp)
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
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Aa",
                fontFamily = fontFamily,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = fontName,
                fontSize = 10.sp,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface.copy(
                    alpha = 0.7f
                )
            )
        }
    }
}