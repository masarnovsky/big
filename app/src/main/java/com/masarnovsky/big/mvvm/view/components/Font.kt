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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.masarnovsky.big.R

val montserratFontFamily = FontFamily(
    Font(R.font.montserrat_black, FontWeight.Black)
)

val pangolinFontFamily = FontFamily(
    Font(R.font.pangolin_regular, FontWeight.Normal)
)

val robotoSlabFontFamily = FontFamily(
    Font(R.font.robotoslab_regular, FontWeight.Normal)
)

val playfairDisplayFontFamily = FontFamily(
    Font(R.font.playfairdisplay_regular, FontWeight.Normal)
)


@Composable
fun FontSelector(
    selectedFont: String,
    onFontSelected: (String) -> Unit
) {
    val fonts = listOf(
        "Montserrat" to montserratFontFamily,
        "Pangolin" to pangolinFontFamily,
        "Roboto" to robotoSlabFontFamily,
        "Playfair" to playfairDisplayFontFamily
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
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary,
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 2.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
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