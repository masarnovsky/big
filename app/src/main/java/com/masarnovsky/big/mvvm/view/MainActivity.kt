package com.masarnovsky.big.mvvm.view

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.masarnovsky.big.mvvm.view.components.BackgroundSelector
import com.masarnovsky.big.mvvm.view.components.FontSelector
import com.masarnovsky.big.mvvm.view.components.HistoryItem
import com.masarnovsky.big.mvvm.view.components.OrientationSelector
import com.masarnovsky.big.mvvm.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

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
                        onDelete = { viewModel.deleteText(item) },
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
