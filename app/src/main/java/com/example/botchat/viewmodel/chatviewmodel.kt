package com.example.chatapp.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.botchat.Data.ChatMessage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

class ChatViewModel : ViewModel() {
    private val _uiState = mutableStateOf(ChatUiState())
    val uiState: State<ChatUiState> = _uiState

    fun sendMessage() {
        val message = _uiState.value.inputText
        if (message.isBlank()) return

        viewModelScope.launch {
            try {
                addMessage(
                    ChatMessage(
                        content = message,
                        isUser = true,
                        timestamp = System.currentTimeMillis(),
                        id = UUID.randomUUID().toString()
                    )
                )
                clearInput()
                setLoading(true)

                delay(1000)

                val dummyResponse = when {
                    message.lowercase().contains("hello") || message.lowercase().contains("hi") ->
                        "Greetings! How may I assist you today?"
                    message.lowercase().contains("how are you") ->
                        "I'm splendid, thank you! How are you faring?"
                    message.lowercase().contains("weather") ->
                        "The forecast is delightful—sunny with a hint of clouds."
                    message.lowercase().contains("joke") ->
                        "Why did the tomato turn red? It saw the salad dressing!"
                    message.lowercase().contains("bye") ->
                        "Farewell! Wishing you a splendid day ahead!"
                    message.lowercase().contains("time") ->
                        "The current time is ${java.text.SimpleDateFormat("HH:mm:ss").format(System.currentTimeMillis())}."
                    message.lowercase().contains("thanks") || message.lowercase().contains("thank you") ->
                        "My pleasure! Anything else I can do for you?"
                    else ->
                        "Fascinating! You mentioned '$message'. What else intrigues you?"
                }
                addMessage(
                    ChatMessage(
                        content = dummyResponse,
                        isUser = false,
                        timestamp = System.currentTimeMillis(),
                        id = UUID.randomUUID().toString()
                    )
                )
            } catch (e: Exception) {
                handleError("Something went awry: ${e.message}")
            } finally {
                setLoading(false)
            }
        }
    }

    private fun addMessage(message: ChatMessage) {
        _uiState.value = _uiState.value.copy(
            messages = _uiState.value.messages + message
        )
    }

    private fun handleError(message: String) {
        _uiState.value = _uiState.value.copy(
            errorMessage = message
        )
    }

    fun updateInputText(text: String) {
        _uiState.value = _uiState.value.copy(inputText = text)
    }

    private fun clearInput() {
        _uiState.value = _uiState.value.copy(inputText = "")
    }

    private fun setLoading(loading: Boolean) {
        _uiState.value = _uiState.value.copy(isLoading = loading)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)