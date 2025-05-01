package com.example.botchat.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.botchat.data.UserSettingsDataStore
import kotlinx.coroutines.flow.Flow

class SettingViewModel(
    private val settingsDataStore: UserSettingsDataStore
) : ViewModel() {

    // UI toggle for showing/hiding the settings sheet
    var showSettings by mutableStateOf(false)
        private set

    fun toggleSettings() {
        showSettings = !showSettings
    }

    // Expose each DataStore flow directly
    val darkModeEnabled: Flow<Boolean>      = settingsDataStore.getDarkMode
    val apiKey: Flow<String>                = settingsDataStore.getHUGGINGFACE_API
    val selectedModelFlow: Flow<String>     = settingsDataStore.getSelectedModel
    val apiEndpoint: Flow<String>           = settingsDataStore.getApiEndpoint
    val systemPrompt: Flow<String>          = settingsDataStore.getSystemPrompt
    val notificationsEnabled: Flow<Boolean> = settingsDataStore.getNotificationsEnabled
    val historyRetentionDays: Flow<Int>     = settingsDataStore.getHistoryRetentionDays
    val cachingEnabled: Flow<Boolean>       = settingsDataStore.getCachingEnabled
    val analyticsEnabled: Flow<Boolean>     = settingsDataStore.getAnalyticsEnabled

    // Suspend functions to update each setting
    suspend fun updateDarkMode(enable: Boolean) {
        settingsDataStore.updateDarkMode(enable)
    }

    suspend fun updateApiKey(key: String) {
        settingsDataStore.updateHUGGINGFACE_API_KEY(key)
    }

    suspend fun updateSelectedModel(model: String) {
        settingsDataStore.updateSelectedModel(model)
    }

    suspend fun updateApiEndpoint(endpoint: String) {
        settingsDataStore.updateApiEndpoint(endpoint)
    }

    suspend fun updateSystemPrompt(prompt: String) {
        settingsDataStore.updateSystemPrompt(prompt)
    }

    suspend fun updateNotificationsEnabled(enabled: Boolean) {
        settingsDataStore.updateNotificationsEnabled(enabled)
    }

    suspend fun updateHistoryRetentionDays(days: Int) {
        settingsDataStore.updateHistoryRetentionDays(days)
    }

    suspend fun updateCachingEnabled(enabled: Boolean) {
        settingsDataStore.updateCachingEnabled(enabled)
    }

    suspend fun updateAnalytics(enabled: Boolean) {
        settingsDataStore.updateAnalyticsEnabled(enabled)
    }

    // Extra UI-only toggles
    var showAdvancedSettings by mutableStateOf(false)
        private set
    fun toggleAdvancedSettings() {
        showAdvancedSettings = !showAdvancedSettings
    }

    var showApiKey by mutableStateOf(false)
        private set
    fun toggleApiKeyVisibility() {
        showApiKey = !showApiKey
    }

    var soundEffectsEnabled by mutableStateOf(true)
        private set
    fun toggleSoundEffects() {
        soundEffectsEnabled = !soundEffectsEnabled
    }
}
