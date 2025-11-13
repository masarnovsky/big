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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.masarnovsky.big.mvvm.BackgroundColor
import com.masarnovsky.big.mvvm.GradientColor
import com.masarnovsky.big.mvvm.InputFont
import com.masarnovsky.big.mvvm.IntentExtras
import com.masarnovsky.big.mvvm.Orientation
import com.masarnovsky.big.mvvm.view.components.BackgroundSelector
import com.masarnovsky.big.mvvm.view.components.FontSelector
import com.masarnovsky.big.mvvm.view.components.HistoryItem
import com.masarnovsky.big.mvvm.view.components.OrientationSelector
import com.masarnovsky.big.mvvm.view.components.PreviewButton
import com.masarnovsky.big.mvvm.view.components.getAppVersion
import com.masarnovsky.big.mvvm.viewmodel.MainViewModel
import com.masarnovsky.big.mvvm.viewmodel.inputTextMaxAmount
import com.masarnovsky.big.ui.theme.monochromeLight
import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.navigation.compose.hiltViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        setContent {
            FullscreenTextTheme {
                MainScreen(
                    onShowFullscreen = { text, font, background, gradient, orientation ->
                        val intent =
                            Intent(this, FullscreenActivity::class.java)
                        intent.putExtra(IntentExtras.DISPLAY_TEXT, text)
                        intent.putExtra(IntentExtras.SELECTED_FONT, font.name)
                        intent.putExtra(IntentExtras.SELECTED_BACKGROUND, background.name)
                        intent.putExtra(IntentExtras.SELECTED_ORIENTATION, orientation.name)
                        intent.putExtra(IntentExtras.SELECTED_GRADIENT, gradient.name)
                        startActivity(intent)
                    }
                )
            }
        }
    }
}

@Composable
fun FullscreenTextTheme(content: @Composable () -> Unit) { // ask: what is that structure?
    MaterialTheme(
        colorScheme = monochromeLight,
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onShowFullscreen: (String, InputFont, BackgroundColor, GradientColor, Orientation) -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    val inputText by viewModel.inputText.collectAsState()
    val history by viewModel.history.collectAsState()
    val selectedFont by viewModel.selectedInputFont.collectAsState()
    val selectedBackground by viewModel.selectedBackground.collectAsState()
    val selectedGradient by viewModel.selectedGradient.collectAsState()
    val selectedOrientation by viewModel.selectedOrientation.collectAsState()
    val shouldShowGradientTooltip by viewModel.shouldShowGradientTooltip.collectAsState(initial = false)

    Scaffold(bottomBar = {
        Text(
            text = "v${getAppVersion()}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            textAlign = TextAlign.Center
        )
    }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { viewModel.updateInputText(it) },
                label = { Text(getLabelText(inputText)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 4,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

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

            Text(
                "Background",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))

            BackgroundSelector(
                selectedBackground = selectedBackground,
                onBackgroundSelected = { viewModel.updateBackground(it) },
                shouldShowTooltip = shouldShowGradientTooltip,
                onTooltipShown = viewModel::markTooltipShown
            )

            Spacer(modifier = Modifier.height(16.dp))

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

            PreviewButton(
                text = inputText,
                inputFont = selectedFont,
                background = selectedBackground,
                gradient = selectedGradient,
                orientation = selectedOrientation,
                onShowFullscreen = {
                    viewModel.saveText(inputText)
                    onShowFullscreen(
                        inputText,
                        selectedFont,
                        selectedBackground,
                        selectedGradient,
                        selectedOrientation
                    )
                },
                enabled = inputText.isNotBlank()
            )

            Spacer(modifier = Modifier.height(24.dp))

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
                                selectedGradient,
                                selectedOrientation
                            )
                        }
                    )
                }
            }
        }
    }
}

fun getLabelText(text: String): String {
    return when {
        text.isNotBlank() -> "Enter text to display ${text.length}/$inputTextMaxAmount"
        else -> "Enter text to display"
    }
}
