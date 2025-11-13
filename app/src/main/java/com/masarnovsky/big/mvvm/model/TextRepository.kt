package com.masarnovsky.big.mvvm.model

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

class TextRepository(private val textDao: TextDao) {

    companion object {
        private const val TAG = "TextRepository"
        private const val MAX_TEXT_LENGTH = 200
    }

    fun getAllTexts(): Flow<List<TextEntity>> {
        return textDao.getAllTexts()
            .catch { e ->
                Log.e(TAG, "Error loading texts from database", e)
                emit(emptyList())
            }
    }

    suspend fun insertText(text: String): Result<Unit> {
        return try {
            val validationError = validateText(text)
            if (validationError != null) {
                return Result.failure(IllegalArgumentException(validationError))
            }

            val sanitizedText = text.trim()
            textDao.insert(TextEntity(text = sanitizedText))
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error inserting text into database", e)
            Result.failure(e)
        }
    }

    suspend fun deleteText(textEntity: TextEntity): Result<Unit> {
        return try {
            textDao.delete(textEntity)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting text from database", e)
            Result.failure(e)
        }
    }

    private fun validateText(text: String): String? {
        return when {
            text.isBlank() -> "Text cannot be empty"
            text.length > MAX_TEXT_LENGTH -> "Text exceeds maximum length of $MAX_TEXT_LENGTH characters"
            else -> null
        }
    }
}
