package com.masarnovsky.big.mvvm.model

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emptyFlow

/**
 * Repository class that handles data operations for text history.
 * Provides a clean API for the ViewModel and handles error cases.
 */
class TextRepository(private val textDao: TextDao) {

    companion object {
        private const val TAG = "TextRepository"
        private const val MAX_TEXT_LENGTH = 200
    }

    /**
     * Get all texts from the database, ordered by timestamp descending.
     * Returns an empty flow if an error occurs.
     */
    fun getAllTexts(): Flow<List<TextEntity>> {
        return textDao.getAllTexts()
            .catch { e ->
                Log.e(TAG, "Error loading texts from database", e)
                emit(emptyList())
            }
    }

    /**
     * Insert a new text entry into the database.
     * Validates the text before insertion.
     *
     * @param text The text to save
     * @return Result indicating success or failure with error message
     */
    suspend fun insertText(text: String): Result<Unit> {
        return try {
            // Validate input
            val validationError = validateText(text)
            if (validationError != null) {
                return Result.failure(IllegalArgumentException(validationError))
            }

            // Sanitize and save
            val sanitizedText = text.trim()
            textDao.insert(TextEntity(text = sanitizedText))
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error inserting text into database", e)
            Result.failure(e)
        }
    }

    /**
     * Delete a text entry from the database.
     *
     * @param textEntity The text entity to delete
     * @return Result indicating success or failure
     */
    suspend fun deleteText(textEntity: TextEntity): Result<Unit> {
        return try {
            textDao.delete(textEntity)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting text from database", e)
            Result.failure(e)
        }
    }

    /**
     * Validates the text input.
     *
     * @param text The text to validate
     * @return Error message if invalid, null if valid
     */
    private fun validateText(text: String): String? {
        return when {
            text.isBlank() -> "Text cannot be empty"
            text.length > MAX_TEXT_LENGTH -> "Text exceeds maximum length of $MAX_TEXT_LENGTH characters"
            else -> null
        }
    }
}
