package com.example.botchat.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

data class UserSettingsDataStore(private val context: Context) {

    companion object {
        private val DARK_MODE = booleanPreferencesKey("dark_mode")
        private val HUGGINGFACE_API_KEY = stringPreferencesKey("api_key")
        private val SELECTED_MODEL = stringPreferencesKey("selected_model")
        private val API_ENDPOINT = stringPreferencesKey("api_endpoint")
        private val SYSTEM_PROMPT = stringPreferencesKey("system_prompt")
        private val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        private val HISTORY_RETENTION_DAYS = intPreferencesKey("history_retention_days")
        private val CACHING_ENABLED = booleanPreferencesKey("caching_enabled")
        private val ANALYTICS_ENABLED = booleanPreferencesKey("analytics_enabled")
    }

    val getDarkMode: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[DARK_MODE] ?: false
        }

    val getHUGGINGFACE_API: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[HUGGINGFACE_API_KEY] ?: ""
        }

    val getSelectedModel: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[SELECTED_MODEL] ?: "facebook/blenderbot-400M-distill"
        }

    val getApiEndpoint: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[API_ENDPOINT] ?: "https://api-inference.huggingface.co"
        }

    val getSystemPrompt: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[SYSTEM_PROMPT] ?: "You are a helpful assistant."
        }

    val getNotificationsEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[NOTIFICATIONS_ENABLED] ?: true
        }

    val getHistoryRetentionDays: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[HISTORY_RETENTION_DAYS] ?: 7
        }

    val getCachingEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[CACHING_ENABLED] ?: true
        }

    val getAnalyticsEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[ANALYTICS_ENABLED] ?: false
        }

    suspend fun updateDarkMode(enableDarkMode: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_MODE] = enableDarkMode
        }
    }

    suspend fun updateHUGGINGFACE_API_KEY(apiKey: String) {
        context.dataStore.edit { preferences ->
            preferences[HUGGINGFACE_API_KEY] = apiKey
        }
    }

    suspend fun updateSelectedModel(model: String) {
        context.dataStore.edit { preferences ->
            preferences[SELECTED_MODEL] = model
        }
    }

    suspend fun updateApiEndpoint(endpoint: String) {
        context.dataStore.edit { preferences ->
            preferences[API_ENDPOINT] = endpoint
        }
    }

    suspend fun updateSystemPrompt(prompt: String) {
        context.dataStore.edit { preferences ->
            preferences[SYSTEM_PROMPT] = prompt
        }
    }

    suspend fun updateNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun updateHistoryRetentionDays(days: Int) {
        context.dataStore.edit { preferences ->
            preferences[HISTORY_RETENTION_DAYS] = days
        }
    }

    suspend fun updateCachingEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[CACHING_ENABLED] = enabled
        }
    }

    suspend fun updateAnalyticsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[ANALYTICS_ENABLED] = enabled
        }
    }
}