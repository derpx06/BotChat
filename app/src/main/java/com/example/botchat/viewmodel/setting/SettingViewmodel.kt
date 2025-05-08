package com.example.botchat.viewmodel.setting

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.botchat.data.UserSettingsDataStore
import kotlinx.coroutines.launch

class SettingViewModel(
    private val settingsDataStore: UserSettingsDataStore
) : ViewModel() {
    val darkModeSetting = settingsDataStore.getDarkModeSetting
    val notificationsEnabled = settingsDataStore.getNotificationsEnabled
    val cachingEnabled = settingsDataStore.getCachingEnabled
    val analyticsEnabled = settingsDataStore.getAnalyticsEnabled
    val openRouterApiKey = settingsDataStore.getOpenRouterAPi
    val openRouterModel = settingsDataStore.getOpenRouterModel
    val huggingFaceApiKey = settingsDataStore.getHUGGINGFACE_API
    val selectedModel = settingsDataStore.getSelectedModel
    val apiEndpoint = settingsDataStore.getApiEndpoint
    val historyRetentionDays = settingsDataStore.getHistoryRetentionDays
    val soundEffectsEnabled = settingsDataStore.getSoundEffectsEnabled
    val selectedProvider = settingsDataStore.getSelectedProvider
    val systemPrompt = settingsDataStore.getSystemPrompt
    val theme = settingsDataStore.getTheme
    var showSettings by mutableStateOf(false)

    @Composable
    fun getDarkModeEnabled(): Boolean {
        val darkModeSetting by darkModeSetting.collectAsStateWithLifecycle(initialValue = "system")
        return when (darkModeSetting) {
            "dark" -> true
            "light" -> false
            else -> isSystemInDarkTheme()
        }
    }

    fun updateDarkModeSetting(setting: String) {
        viewModelScope.launch {
            settingsDataStore.updateDarkModeSetting(setting)
        }
    }

    fun updateNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.updateNotificationsEnabled(enabled)
        }
    }

    fun updateCachingEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.updateCachingEnabled(enabled)
        }
    }

    fun updateAnalyticsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.updateAnalyticsEnabled(enabled)
        }
    }

    fun updateOpenRouterApiKey(apiKey: String) {
        viewModelScope.launch {
            settingsDataStore.updateOpenRouterApiKey(apiKey)
        }
    }

    fun updateOpenRouterModel(model: String) {
        viewModelScope.launch {
            settingsDataStore.updateOpenRouterModel(model)
        }
    }

    fun updateHuggingFaceApiKey(apiKey: String) {
        viewModelScope.launch {
            settingsDataStore.updateHUGGINGFACE_API_KEY(apiKey)
        }
    }

    fun updateSelectedModel(model: String) {
        viewModelScope.launch {
            settingsDataStore.updateSelectedModel(model)
        }
    }

    fun updateApiEndpoint(endpoint: String) {
        viewModelScope.launch {
            settingsDataStore.updateApiEndpoint(endpoint)
        }
    }

    fun updateHistoryRetentionDays(days: Int) {
        viewModelScope.launch {
            settingsDataStore.updateHistoryRetentionDays(days)
        }
    }

    fun updateSoundEffectsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.updateSoundEffectsEnabled(enabled)
        }
    }

    fun updateSelectedProvider(provider: String) {
        viewModelScope.launch {
            settingsDataStore.updateSelectedProvider(provider)
        }
    }

    fun updateSystemPrompt(prompt: String) {
        viewModelScope.launch {
            settingsDataStore.updateSystemPrompt(prompt)
        }
    }

    fun updateTheme(theme: String) {
        viewModelScope.launch {
            settingsDataStore.updateTheme(theme)
        }
    }


    fun toggleSettings() {
        showSettings = !showSettings
    }
}