package com.masarnovsky.big.mvvm.model

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

/**
 * Manages user preferences using DataStore.
 * Handles all user-specific settings and preferences with proper error handling.
 */
class UserPreferencesManager(private val context: Context) {

    private companion object {
        private const val TAG = "UserPreferencesManager"
        val GRADIENT_TOOLTIP_SHOWN = booleanPreferencesKey("gradient_tooltip_shown")
    }

    /**
     * Flow indicating whether the user has seen the gradient tooltip.
     * Returns false by default or if an error occurs.
     */
    val hasSeenGradientTooltip: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading preferences", exception)
                emit(androidx.datastore.preferences.core.emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[GRADIENT_TOOLTIP_SHOWN] ?: false
        }

    /**
     * Marks the gradient tooltip as shown.
     * Throws exception if the operation fails.
     */
    suspend fun markGradientTooltipShown() {
        try {
            context.dataStore.edit { preferences ->
                preferences[GRADIENT_TOOLTIP_SHOWN] = true
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error saving preferences", e)
            throw e
        }
    }
}