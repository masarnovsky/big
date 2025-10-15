package com.masarnovsky.big.mvvm.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.masarnovsky.big.mvvm.model.TextDatabase
import com.masarnovsky.big.mvvm.model.TextEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = TextDatabase.Companion.getDatabase(application) // FIXME: move to correct layer (for test purposes!) - use chat https://claude.ai/chat/b6e0b0ec-ba9a-49d9-ac05-25b9d9f44af6
    private val dao = database.textDao()

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private val _history = MutableStateFlow<List<TextEntity>>(emptyList())
    val history: StateFlow<List<TextEntity>> = _history.asStateFlow()

    private val _selectedFont = MutableStateFlow("Poppins")
    val selectedFont: StateFlow<String> = _selectedFont.asStateFlow()

    private val _selectedBackground = MutableStateFlow("black")
    val selectedBackground: StateFlow<String> = _selectedBackground.asStateFlow()

    private val _selectedOrientation = MutableStateFlow("landscape")
    val selectedOrientation: StateFlow<String> = _selectedOrientation.asStateFlow()

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
        _inputText.value = text
    }

    fun updateFont(font: String) {
        _selectedFont.value = font
    }

    fun updateBackground(background: String) {
        _selectedBackground.value = background
    }

    fun updateOrientation(orientation: String) {
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
}