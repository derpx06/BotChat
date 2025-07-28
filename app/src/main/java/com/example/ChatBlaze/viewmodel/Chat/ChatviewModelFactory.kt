package com.example.ChatBlaze.viewmodel.Chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ChatBlaze.Repository.ChatRepository
import com.example.ChatBlaze.data.UserSettingsDataStore

class ChatViewModelFactory(
    private val dataStore: UserSettingsDataStore,
    private val chatRepository: ChatRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatViewModel(dataStore, chatRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}