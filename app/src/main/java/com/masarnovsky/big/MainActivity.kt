package com.masarnovsky.big

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FullscreenTextTheme {
                MainScreen(
                    onShowFullscreen = { text, font, background, orientation ->
                        val intent = Intent(this, FullscreenActivity::class.java)
                        intent.putExtra("DISPLAY_TEXT", text)
                        intent.putExtra("SELECTED_FONT", font)
                        intent.putExtra("SELECTED_BACKGROUND", background)
                        intent.putExtra("SELECTED_ORIENTATION", orientation)
                        startActivity(intent)
                    }
                )
            }
        }
    }
}

@Composable
fun FullscreenTextTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFF6200EE),
            secondary = Color(0xFF03DAC6),
            background = Color(0xFF121212),
            surface = Color(0xFF1E1E1E)
        ),
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel(),
    onShowFullscreen: (String, String, String, String) -> Unit
) {
    val inputText by viewModel.inputText.collectAsState()
    val history by viewModel.history.collectAsState()
    val selectedFont by viewModel.selectedFont.collectAsState()
    val selectedBackground by viewModel.selectedBackground.collectAsState()
    val selectedOrientation by viewModel.selectedOrientation.collectAsState()

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            // Input Section
            OutlinedTextField(
                value = inputText,
                onValueChange = { viewModel.updateInputText(it) },
                label = { Text("Enter text to display") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 4,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Font Selector
            Text(
                "Font Style",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))

            FontSelector(
                selectedFont = selectedFont,
                onFontSelected = { viewModel.updateFont(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Background Selector
            Text(
                "Background",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))

            BackgroundSelector(
                selectedBackground = selectedBackground,
                onBackgroundSelected = { viewModel.updateBackground(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Orientation Selector
            Text(
                "Orientation",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))

            OrientationSelector(
                selectedOrientation = selectedOrientation,
                onOrientationSelected = { viewModel.updateOrientation(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Show Button
            Button(
                onClick = {
                    if (inputText.isNotBlank()) {
                        viewModel.saveText(inputText)
                        onShowFullscreen(
                            inputText,
                            selectedFont,
                            selectedBackground,
                            selectedOrientation
                        )
                        viewModel.updateInputText("")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = inputText.isNotBlank(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6200EE)
                )
            ) {
                Text(
                    "Show Fullscreen",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // History Section
            Text(
                "History",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(history, key = { it.id }) { item ->
                    HistoryItem(
                        text = item.text,
                        timestamp = item.timestamp,
                        onDelete = { viewModel.deleteText(item.id) },
                        onClick = {
                            onShowFullscreen(
                                item.text,
                                selectedFont,
                                selectedBackground,
                                selectedOrientation
                            )
                        }
                    )
                }
            }
        }
    }
}

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

@Composable
fun OrientationSelector(
    selectedOrientation: String,
    onOrientationSelected: (String) -> Unit
) {
    val orientations = listOf(
        "landscape" to "Landscape",
        "portrait" to "Portrait"
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        orientations.forEach { (value, label) ->
            OrientationOption(
                label = label,
                value = value,
                isSelected = selectedOrientation == value,
                onClick = { onOrientationSelected(value) }
            )
        }
    }
}

@Composable
fun OrientationOption(
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
            .padding(horizontal = 20.dp, vertical = 12.dp),
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

@Composable
fun HistoryItem(
    text: String,
    timestamp: Long,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = text,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatTimestamp(timestamp),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color(0xFFE57373)
                )
            }
        }
    }
}

fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}