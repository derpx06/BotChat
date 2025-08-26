package com.example.ChatBlaze.ui.viewmodel.Chat

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ChatBlaze.data.downlaod.DefaultDownloadRepository
import com.example.ChatBlaze.data.download.ModelDownloaderViewModel
import com.example.ChatBlaze.data.model.UserSettingsDataStore
import com.example.ChatBlaze.data.repository.ChatRepository

class ChatViewModelFactory(
    private val dataStore: UserSettingsDataStore,
    private val chatRepository: ChatRepository,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatViewModel(
                settingsDataStore = dataStore,
                chatRepository = chatRepository,
                modelDownloaderViewModel = ModelDownloaderViewModel(
                    context,
                    DefaultDownloadRepository(context)
                ),
                context = context
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}