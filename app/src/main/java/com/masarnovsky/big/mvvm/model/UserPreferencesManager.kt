package com.masarnovsky.big.mvvm.model

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesManager(private val context: Context) {

    private companion object {
        val GRADIENT_TOOLTIP_SHOWN = booleanPreferencesKey("gradient_tooltip_shown")
    }

    val hasSeenGradientTooltip: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[GRADIENT_TOOLTIP_SHOWN] ?: false
        }

    suspend fun markGradientTooltipShown() {
        context.dataStore.edit { preferences ->
            preferences[GRADIENT_TOOLTIP_SHOWN] = true
        }
    }
}