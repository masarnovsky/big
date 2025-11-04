package com.masarnovsky.big.mvvm.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.masarnovsky.big.mvvm.BackgroundColor
import com.masarnovsky.big.mvvm.GradientColor
import com.masarnovsky.big.mvvm.InputFont
import com.masarnovsky.big.mvvm.Orientation
import com.masarnovsky.big.mvvm.getRandomGradient
import com.masarnovsky.big.mvvm.model.TextDatabase
import com.masarnovsky.big.mvvm.model.TextEntity
import com.masarnovsky.big.mvvm.model.UserPreferencesManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesManager = UserPreferencesManager(application)
    private val database =
        TextDatabase.Companion.getDatabase(application) // FIXME: move to correct layer (for test purposes!) - use chat https://claude.ai/chat/b6e0b0ec-ba9a-49d9-ac05-25b9d9f44af6
    private val dao = database.textDao()

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
            dao.getAllTexts().collect { texts ->
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
        if (text.isNotBlank()) {
            viewModelScope.launch {
                dao.insert(TextEntity(text = text))
            }
        }
    }

    fun deleteText(entity: TextEntity) {
        viewModelScope.launch {
            dao.delete(entity)
        }
    }

    fun markTooltipShown() {
        viewModelScope.launch {
            preferencesManager.markGradientTooltipShown()
        }
    }
}