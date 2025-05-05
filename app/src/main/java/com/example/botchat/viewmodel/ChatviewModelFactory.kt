package com.example.botchat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.botchat.Repository.ChatRepository
import com.example.botchat.data.UserSettingsDataStore

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