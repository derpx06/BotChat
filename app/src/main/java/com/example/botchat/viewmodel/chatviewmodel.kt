package com.example.botchat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.botchat.data.ChatMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

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
        when {
            message.isEmpty() -> {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Message cannot be empty"
                )
            }
            message.length > 500 -> {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Message is too long (max 500 characters)"
                )
            }
            else -> {
                val newMessage = ChatMessage(content = message, isUser = true)
                _uiState.value = _uiState.value.copy(
                    messages = _uiState.value.messages + newMessage,
                    inputText = "",
                    isLoading = true
                )
                respondToUserMessage(message)
            }
        }
    }

    private fun respondToUserMessage(userMessage: String) {
        viewModelScope.launch(Dispatchers.Default) {
            // Random delay between 1000ms and 3000ms to simulate AI processing
            val delayMs = Random.nextLong(1000, 3001)
            delay(delayMs)

            val responseText = generateCustomResponse(userMessage.lowercase())
            val response = ChatMessage(content = responseText, isUser = false)
            _uiState.value = _uiState.value.copy(
                messages = _uiState.value.messages + response,
                isLoading = false
            )
        }
    }

    private fun generateCustomResponse(userMessage: String): String {
        return when {
            userMessage.contains("hello") || userMessage.contains("hi") || userMessage.contains("hey") -> {
                "Greetings! How can I assist you today?"
            }
            userMessage.contains("weather") -> {
                "I don't have real-time weather data, but tell me your location, and I can suggest what to wear based on typical conditions!"
            }
            userMessage.contains("calculate") || userMessage.contains("math") || userMessage.contains("solve") -> {
                when {
                    userMessage.contains("+") -> {
                        try {
                            val numbers = userMessage.split("+").map { it.trim().toDoubleOrNull() }
                            if (numbers.size == 2 && numbers.all { it != null }) {
                                "The result is ${numbers[0]!! + numbers[1]!!}"
                            } else {
                                "Please provide a valid addition, like 'calculate 5 + 3'"
                            }
                        } catch (e: Exception) {
                            "Sorry, I couldn't parse that math expression. Try 'calculate 5 + 3'."
                        }
                    }
                    else -> "I can help with basic math! Try something like 'calculate 5 + 3'."
                }
            }
            userMessage.contains("who is") || userMessage.contains("what is") -> {
                when {
                    userMessage.contains("elon musk") -> {
                        "Elon Musk is a tech entrepreneur, CEO of Tesla, SpaceX, and xAI, known for pushing boundaries in AI, space travel, and sustainable energy."
                    }
                    userMessage.contains("grok") -> {
                        "I'm Grok, created by xAI! I'm here to answer your questions with a dash of humor and a lot of insight. What's on your mind?"
                    }
                    else -> "That's an interesting question! Can you be more specific? For example, 'Who is Elon Musk?'"
                }
            }
            userMessage.contains("joke") -> {
                "Why did the computer go to art school? Because it wanted to learn how to draw a better 'byte'!"
            }
            userMessage.contains("time") -> {
                "I don't have a clock, but I can tell you it's always a great time to ask questions! What's the current time where you are?"
            }
            else -> {
                "Hmm, that's a unique question! I'm still learning, so how about asking something like 'tell me a joke' or 'who is Grok'?"
            }
        }
    }

    fun cancelProcessing() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}