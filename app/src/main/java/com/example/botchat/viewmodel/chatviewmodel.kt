package com.example.botchat.viewmodel

import androidx.lifecycle.ViewModel
import com.example.botchat.Data.ChatMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class ChatViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun updateInputText(text: String) {
        _uiState.value = _uiState.value.copy(inputText = text)
    }

    fun sendMessage() {
        val message = _uiState.value.inputText.trim()
        if (message.isNotEmpty()) {
            val newMessage = ChatMessage(content = message, isUser = true)
            _uiState.value = _uiState.value.copy(
                messages = _uiState.value.messages + newMessage,
                inputText = "",
                isLoading = true
            )
            simulateAiResponse()
        } else {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Message cannot be empty"
            )
        }
    }

    private fun simulateAiResponse() {
        // Simulate AI response (replace with actual API call in production)
        val response = ChatMessage(
            content = "Hello! I'm your AI Assistant. How can I help you today?",
            isUser = false
        )
        _uiState.value = _uiState.value.copy(
            messages = _uiState.value.messages + response,
            isLoading = false
        )
    }

    fun cancelProcessing() {
        _uiState.value = _uiState.value.copy(isLoading = false)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}