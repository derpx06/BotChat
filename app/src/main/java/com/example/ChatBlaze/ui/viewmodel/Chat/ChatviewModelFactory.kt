package com.example.ChatBlaze.ui.viewmodel.Chat

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ChatBlaze.data.downlaod.DefaultDownloadRepository
import com.example.ChatBlaze.data.downlaod.ModelDownloaderViewModel
import com.example.ChatBlaze.data.model.UserSettingsDataStore
import com.example.ChatBlaze.data.repository.ChatRepository
import com.example.ChatBlaze.ui.speech.SpeechToTextManager

class ChatViewModelFactory(
    private val dataStore: UserSettingsDataStore,
    private val chatRepository: ChatRepository,
    private val application: Application // Changed to Application context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatViewModel(
                settingsDataStore = dataStore,
                chatRepository = chatRepository,
                modelDownloaderViewModel = ModelDownloaderViewModel(
                    application,
                    DefaultDownloadRepository(application)
                ),
                context = application,
                speechToTextManager = SpeechToTextManager(application)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
