package com.masarnovsky.big.mvvm.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masarnovsky.big.mvvm.BackgroundColor
import com.masarnovsky.big.mvvm.GradientColor
import com.masarnovsky.big.mvvm.InputFont
import com.masarnovsky.big.mvvm.Orientation
import com.masarnovsky.big.mvvm.getRandomGradient
import com.masarnovsky.big.mvvm.model.TextEntity
import com.masarnovsky.big.mvvm.model.TextRepository
import com.masarnovsky.big.mvvm.model.UserPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the main screen.
 * Uses Hilt for dependency injection to provide repository and preferences manager.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: TextRepository,
    private val preferencesManager: UserPreferencesManager
) : ViewModel() {

    companion object {
        private const val TAG = "MainViewModel"
    }

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private val _history = MutableStateFlow<List<TextEntity>>(emptyList())
    val history: StateFlow<List<TextEntity>> = _history.asStateFlow()

    private val _selectedFont = MutableStateFlow(defaultInputFont)
    val selectedInputFont: StateFlow<InputFont> = _selectedFont.asStateFlow()

    private val _selectedBackground = MutableStateFlow(defaultBackgroundColor)
    val selectedBackground: StateFlow<BackgroundColor> = _selectedBackground.asStateFlow()

    private val _selectedGradient =
        MutableStateFlow(getRandomGradient()) // ask: what is this construction?
    val selectedGradient: StateFlow<GradientColor> = _selectedGradient.asStateFlow()

    private val _selectedOrientation = MutableStateFlow(defaultOrientation)
    val selectedOrientation: StateFlow<Orientation> = _selectedOrientation.asStateFlow()

    val shouldShowGradientTooltip: Flow<Boolean> = preferencesManager.hasSeenGradientTooltip
        .map { hasSeenIt -> !hasSeenIt }

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            repository.getAllTexts()
                .catch { e ->
                    Log.e(TAG, "Error loading history", e)
                    emit(emptyList())
                }
                .collect { texts ->
                    _history.value = texts
                }
        }
    }

    fun updateInputText(text: String) {
        if (text.length <= inputTextMaxAmount) {
            _inputText.value = text
        }
    }

    fun updateFont(inputFont: InputFont) {
        _selectedFont.value = inputFont
    }

    fun updateBackground(background: BackgroundColor) {
        _selectedBackground.value = background

        if (BackgroundColor.GRADIENT == background) {
            _selectedGradient.value = getRandomGradient()
        }
    }

    fun updateOrientation(orientation: Orientation) {
        _selectedOrientation.value = orientation
    }

    fun saveText(text: String) {
        viewModelScope.launch {
            val result = repository.insertText(text)
            result.onFailure { error ->
                Log.e(TAG, "Failed to save text: ${error.message}", error)
            }
        }
    }

    fun deleteText(entity: TextEntity) {
        viewModelScope.launch {
            val result = repository.deleteText(entity)
            result.onFailure { error ->
                Log.e(TAG, "Failed to delete text: ${error.message}", error)
            }
        }
    }

    fun markTooltipShown() {
        viewModelScope.launch {
            try {
                preferencesManager.markGradientTooltipShown()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to mark tooltip as shown", e)
            }
        }
    }
}