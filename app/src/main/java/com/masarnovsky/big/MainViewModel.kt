package com.masarnovsky.big

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = TextDatabase.getDatabase(application) // LLM: is it OK to do such things?
    private val dao = database.textDao()

    private val _inputText = MutableStateFlow("") // LLM: // what is state flow?
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private val _history = MutableStateFlow<List<TextEntity>>(emptyList())
    val history: StateFlow<List<TextEntity>> = _history.asStateFlow()

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

    fun saveText(text: String) {
        if (text.isNotBlank()) {
            viewModelScope.launch {
                dao.insert(TextEntity(text = text))
            }
        }
    }

    fun deleteText(id: Int) {
        viewModelScope.launch {
            dao.delete(id)
        }
    }
}