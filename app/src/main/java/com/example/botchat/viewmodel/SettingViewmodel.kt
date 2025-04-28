package com.example.botchat.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class SettingViewModel : ViewModel() {
    var showSettings by mutableStateOf(false)
        private set
    fun toggleSettings() {
        showSettings = !showSettings
    }

    var darkModeEnabled by mutableStateOf(false)
        private set
    fun toggleDarkMode() {
        darkModeEnabled = !darkModeEnabled
    }

    var notificationsEnabled by mutableStateOf(true)
        private set
    fun toggleNotifications() {
        notificationsEnabled = !notificationsEnabled
    }

    var historyRetentionDays by mutableStateOf(7)
        private set
    fun updateHistoryRetentionDays(days: Int) {
        if (days in 1..30) {
            historyRetentionDays = days
        }
    }

    var apiKey by mutableStateOf("")
        private set
    fun updateApiKey(key: String) { // Renamed from setApiKey to avoid clash
        apiKey = key
    }

    var serverUrl by mutableStateOf("https://api.example.com")
        private set
    fun updateServerUrl(url: String) { // Renamed from setServerUrl to avoid clash
        serverUrl = url.trim()
    }

    var showAdvancedSettings by mutableStateOf(false)
        private set
    fun toggleAdvancedSettings() {
        showAdvancedSettings = !showAdvancedSettings
    }

    var cachingEnabled by mutableStateOf(true)
        private set
    fun toggleCaching() {
        cachingEnabled = !cachingEnabled
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

    var analyticsEnabled by mutableStateOf(false)
        private set
    fun toggleAnalytics() {
        analyticsEnabled = !analyticsEnabled
    }
}