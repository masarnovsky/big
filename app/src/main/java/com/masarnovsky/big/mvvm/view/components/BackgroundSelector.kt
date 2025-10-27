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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.masarnovsky.big.mvvm.BackgroundColor


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackgroundSelector(
    selectedBackground: BackgroundColor,
    onBackgroundSelected: (BackgroundColor) -> Unit,
    shouldShowTooltip: Boolean,
    onTooltipShown: () -> Unit
) {
    val backgrounds = BackgroundColor.entries.map { Pair(it, it.label) }
    val tooltipState = rememberTooltipState(initialIsVisible = shouldShowTooltip, isPersistent = true)

    LaunchedEffect(shouldShowTooltip) {
        if (shouldShowTooltip) {
            tooltipState.show()
        }
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        backgrounds.forEach { (value, label) ->
            if (value == BackgroundColor.GRADIENT) {
                TooltipBox(
                    positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                        TooltipAnchorPosition.Above,
                    ),
                    tooltip = {
                        PlainTooltip {
                            Text("Tap to change gradient")
                        }
                    },
                    state = tooltipState
                ) {
                    BackgroundOption(
                        label = label,
                        isSelected = selectedBackground == value,
                        onClick = {
                            if (shouldShowTooltip) {
                                onTooltipShown()
                            }
                            onBackgroundSelected(value)
                        }
                    )
                }
            } else {
                BackgroundOption(
                    label = label,
                    isSelected = selectedBackground == value,
                    onClick = { onBackgroundSelected(value) }
                )
            }
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