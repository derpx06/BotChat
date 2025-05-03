package com.example.botchat.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.botchat.api.HuggingFaceApiService
import com.example.botchat.data.ChatMessage
import com.example.botchat.data.HuggingFaceParameters
import com.example.botchat.data.HuggingFaceRequest
import com.example.botchat.data.HuggingFaceResponse
import com.example.botchat.data.UserSettingsDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.SocketTimeoutException
import javax.net.ssl.SSLHandshakeException

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showErrorPage: Boolean = false
)

class ChatViewModel(
    private val settingsDataStore: UserSettingsDataStore
) : ViewModel() {
    private val apiService: HuggingFaceApiService = HuggingFaceApiService.create()
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    // Updated model list with <6B parameter models, all supporting autostart
    private val supportedModels = setOf(
        "deepseek-ai/DeepSeek-R1-Distill-Qwen-1.5B",  // New model
        "microsoft/Phi-3-mini-4k-instruct",
        "google/gemma-2b-it",
        "Qwen/Qwen1.5-1.8B-Chat",
        "NousResearch/Hermes-2-Theta-Llama-3.1-4B",
        "Qwen/Qwen3-0.6B",
        "facebook/blenderbot-400M-distill"

    )

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
                    isLoading = true,
                    showErrorPage = false
                )
                respondToUserMessage(message)
            }
        }
    }

    private fun validateCredentials(apiKey: String, model: String): String? {
        return when {
            apiKey.isBlank() -> "API key is missing. Please set it in Settings."
            !model.matches(Regex("^[a-zA-Z0-9-_]+/[a-zA-Z0-9-_.]+$")) ->
                "Invalid model format. Use 'owner/model' (e.g., 'DeepSeek/DeepSeek-LLM-1.3B-Chat')"
            model !in supportedModels ->
                "Unsupported model. Please select from:\n${supportedModels.joinToString("\n")}"
            else -> null
        }
    }

    private fun respondToUserMessage(userMessage: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val apiKey = settingsDataStore.getHUGGINGFACE_API.first()
                val model = settingsDataStore.getSelectedModel.first()
                val apiEndpoint = settingsDataStore.getApiEndpoint.first()

                // Validate credentials
                val validationError = validateCredentials(apiKey, model)
                if (validationError != null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = validationError
                    )
                    return@launch
                }

                val modelUrl = "$apiEndpoint/models/$model"

                val request = HuggingFaceRequest(
                    inputs = userMessage,
                    parameters = HuggingFaceParameters(
                        //max_new_tokens = 200,
                        temperature = 0.7,
                        top_p = 0.9,
                        do_sample = true
                    )
                )

                val response = apiService.query(
                    authorization = "Bearer $apiKey",
                    payload = request,
                    url = modelUrl
                )

                if (response.isSuccessful && isValidResponse(response.body())) {
                    val responseBody = response.body()!!
                    val responseText = responseBody.firstOrNull { it.generated_text != null }?.generated_text?.trim()
                        ?: "No valid response received"
                    responseBody.firstOrNull()?.warnings?.forEach { warning ->
                        Log.w("HuggingFaceAPI", "Warning: $warning")
                    }
                    val botMessage = ChatMessage(content = responseText, isUser = false)
                    _uiState.value = _uiState.value.copy(
                        messages = _uiState.value.messages + botMessage,
                        isLoading = false
                    )
                } else {
                    val errorMessage = when {
                        response.code() == 503 -> {
                            _uiState.value = _uiState.value.copy(showErrorPage = true)
                            "Service Unavailable. Please try again later."
                        }
                        response.isSuccessful -> {
                            val responseBody = response.body()
                            if (responseBody.isNullOrEmpty()) {
                                "Empty response from API"
                            } else {
                                "API error: ${responseBody.firstOrNull()?.error ?: "Unknown error"}"
                            }
                        }
                        else -> {
                            val errorBody = response.errorBody()?.string() ?: response.message()
                            when (response.code()) {
                                429 -> "Rate limit exceeded. Please wait and try again."
                                401 -> "Invalid API key. Please check your settings."
                                else -> "API error ${response.code()}: $errorBody"
                            }
                        }
                    }
                    val fallbackResponse = generateCustomResponse(userMessage)
                    _uiState.value = _uiState.value.copy(
                        messages = _uiState.value.messages + ChatMessage(content = fallbackResponse, isUser = false),
                        isLoading = false,
                        errorMessage = "$errorMessage. Using fallback response."
                    )
                }
            } catch (e: Exception) {
                val fallbackResponse = generateCustomResponse(userMessage)
                val errorMessage = when (e) {
                    is SocketTimeoutException -> "Connection timed out. Please try again."
                    is ConnectException -> "Network unavailable. Please check your connection."
                    is SSLHandshakeException -> "Security error. Please check your API configuration."
                    else -> "Network error: ${e.message ?: "Unknown error"}"
                }
                _uiState.value = _uiState.value.copy(
                    messages = _uiState.value.messages + ChatMessage(content = fallbackResponse, isUser = false),
                    isLoading = false,
                    errorMessage = "$errorMessage. Using fallback response."
                )
            }
        }
    }

    private fun isValidResponse(response: List<HuggingFaceResponse>?): Boolean {
        return response != null &&
                response.isNotEmpty() &&
                response.any { it.generated_text != null && it.error == null }
    }

    private fun generateCustomResponse(userMessage: String): String {
        val lowerCaseMessage = userMessage.lowercase()
        return when {
            lowerCaseMessage.contains(Regex("hello|hi|hey", RegexOption.IGNORE_CASE)) ->
                "Greetings! I'm powered by a smart AI model like DeepSeek or Phi, specializing in technical discussions and creative problem-solving. How can I assist?"

            lowerCaseMessage.contains(Regex("joke|funny|laugh", RegexOption.IGNORE_CASE)) ->
                listOf(
                    "Why don't mathematicians argue about infinity? Because it's a never-ending story!",
                    "What do you call an AI that makes bad puns? A neural-🦜network!",
                    "Why did the computer go to art school? To improve its vector graphics!"
                ).random()

            lowerCaseMessage.contains(Regex("math|calculate|equation", RegexOption.IGNORE_CASE)) -> {
                when {
                    lowerCaseMessage.contains("+") -> {
                        try {
                            val numbers = lowerCaseMessage.split("+").map { it.trim().toDoubleOrNull() }
                            if (numbers.size == 2 && numbers.all { it != null }) {
                                "The result is ${numbers[0]!! + numbers[1]!!}"
                            } else {
                                "Please provide a valid addition, like 'calculate 5 + 3'"
                            }
                        } catch (e: Exception) {
                            "Sorry, I couldn't parse that math expression. Try 'calculate 5 + 3'."
                        }
                    }
                    lowerCaseMessage.contains("fibonacci") -> {
                        try {
                            val n = lowerCaseMessage.replace(Regex("[^0-9]"), "").toIntOrNull()
                            if (n != null && n >= 0) {
                                val fib = generateFibonacci(n)
                                "The ${n}th Fibonacci number is $fib"
                            } else {
                                "Please specify a non-negative number, like 'fibonacci 5'"
                            }
                        } catch (e: Exception) {
                            "Error calculating Fibonacci. Try 'fibonacci 5'."
                        }
                    }
                    lowerCaseMessage.contains("solve") && lowerCaseMessage.contains("x") -> {
                        "I can solve simple linear equations! For example, for 'solve 3x + 5 = 17', I'd say x = 4. Try a specific equation!"
                    }
                    else -> "I can help with mathematical reasoning! Try asking: 'Solve 3x + 5 = 17' or 'Explain quantum entanglement'"
                }
            }

            lowerCaseMessage.contains(Regex("who is|what is", RegexOption.IGNORE_CASE)) -> {
                when {
                    lowerCaseMessage.contains("elon musk") ->
                        "Elon Musk is the CEO of Tesla, SpaceX, and xAI, known for his ambitious goals in AI, space exploration, and sustainable energy. Want to know more about his projects?"
                    lowerCaseMessage.contains("grok") ->
                        "I'm Grok, created by xAI, running on a smart model like DeepSeek or Phi. I'm here to answer with wit, reason, and a touch of humor. What's your next question?"
                    lowerCaseMessage.contains("deepseek") ->
                        "DeepSeek is a Chinese AI company building powerful open-source models like DeepSeek-LLM-1.3B-Chat, which I might be running on! They excel in reasoning and coding. Curious about their tech?"
                    lowerCaseMessage.contains("phi") ->
                        "Phi models, like Phi-3-mini, are Microsoft’s compact yet powerful AI models. They’re great for chat and reasoning, and I might be using one now! Want to explore its capabilities?"
                    else -> "That's a great question! Be more specific, like 'Who is Elon Musk?' or 'What is DeepSeek?'"
                }
            }

            lowerCaseMessage.contains(Regex("time", RegexOption.IGNORE_CASE)) ->
                "I don’t have a clock, but I can reason about time zones or schedules. Tell me your location or a specific time-related question, and I’ll help!"

            lowerCaseMessage.contains(Regex("code|program", RegexOption.IGNORE_CASE)) -> {
                when {
                    lowerCaseMessage.contains("python") ->
                        "Want a Python snippet? Ask for something specific, like 'write a Python function to reverse a string,' and I’ll whip up some clean code!"
                    lowerCaseMessage.contains("sort") ->
                        "Sorting, huh? I can write a quicksort in Python or another language. Specify the language or say 'write a sorting algorithm in Python' for a sample!"
                    else -> "I’m a coding whiz! Ask for a specific task, like 'write a Python function to sort a list,' and I’ll deliver."
                }
            }

            else ->
                "As a compact AI model, I balance efficiency with depth. While thinking about your query, consider that I can:\n" +
                        "• Analyze technical documents\n• Solve logic puzzles\n• Explain complex concepts simply\n" +
                        "What would you like to explore?"
        }
    }

    private fun generateFibonacci(n: Int): Long {
        if (n <= 1) return n.toLong()
        var a = 0L
        var b = 1L
        for (i in 2..n) {
            val temp = a + b
            a = b
            b = temp
        }
        return b
    }

    fun cancelProcessing() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null, showErrorPage = false)
    }
}