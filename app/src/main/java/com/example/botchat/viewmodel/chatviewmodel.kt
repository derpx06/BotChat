package com.example.botchat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.botchat.api.HuggingFaceApiService
import com.example.botchat.api.OpenRouterApiService
import com.example.botchat.data.HuggingFaceRequest
import com.example.botchat.data.OpenRouterMessage
import com.example.botchat.data.OpenRouterRequest
import com.example.botchat.data.UserSettingsDataStore
import com.example.botchat.database.ChatMessage
import com.example.botchat.Repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.util.Log
import java.io.IOException
import java.net.UnknownHostException

data class UiState(
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showErrorDialog: Boolean = false,
    val retryAction: (() -> Unit)? = null
)

class ChatViewModel(
    private val settingsDataStore: UserSettingsDataStore,
    private val chatRepository: ChatRepository,
    private val openRouterApiService: OpenRouterApiService = OpenRouterApiService.create(),
    private val huggingFaceApiService: HuggingFaceApiService = HuggingFaceApiService.create()
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            chatRepository.getAllChatMessages().collect { messages ->
                _uiState.update { it.copy(messages = messages) }
            }
        }
    }

    fun updateInputText(text: String) {
        _uiState.update { it.copy(inputText = text, errorMessage = null, showErrorDialog = false) }
    }

    fun sendMessage() {
        val inputText = _uiState.value.inputText.trim()
        if (inputText.isBlank()) {
            _uiState.update {
                it.copy(
                    errorMessage = "Message cannot be empty",
                    showErrorDialog = true,
                    retryAction = null
                )
            }
            return
        }

        viewModelScope.launch {
            try {
                Log.d("ChatViewModel", "Sending message: $inputText")
                val provider = settingsDataStore.getSelectedProvider.first()
                val isHuggingFace = provider == "huggingface"
                val apiKey = if (isHuggingFace) {
                    settingsDataStore.getHUGGINGFACE_API.first()
                } else {
                    settingsDataStore.getOpenRouterAPi.first()
                }
                val model = if (isHuggingFace) {
                    settingsDataStore.getSelectedModel.first()
                } else {
                    settingsDataStore.getOpenRouterModel.first()
                }
                val apiEndpoint = if (isHuggingFace) settingsDataStore.getApiEndpoint.first() else "https://openrouter.ai/api/v1"

                Log.d("ChatViewModel", "Provider: $provider, Model: $model, API key: ${apiKey.take(10)}..., Endpoint: $apiEndpoint")

                if (apiKey.isBlank()) {
                    Log.e("ChatViewModel", "$provider API key is missing")
                    _uiState.update {
                        it.copy(
                            errorMessage = "Please provide a valid $provider API key in settings",
                            showErrorDialog = true,
                            retryAction = null
                        )
                    }
                    return@launch
                }
                if (model.isBlank()) {
                    Log.e("ChatViewModel", "$provider model is missing")
                    _uiState.update {
                        it.copy(
                            errorMessage = "Please select a $provider model in settings",
                            showErrorDialog = true,
                            retryAction = null
                        )
                    }
                    return@launch
                }
                if (isHuggingFace && apiEndpoint.isBlank()) {
                    Log.e("ChatViewModel", "HuggingFace endpoint is missing")
                    _uiState.update {
                        it.copy(
                            errorMessage = "Please provide a HuggingFace endpoint in settings",
                            showErrorDialog = true,
                            retryAction = null
                        )
                    }
                    return@launch
                }

                val userMessage = ChatMessage(content = inputText, isUser = true)
                chatRepository.insertChatMessage(userMessage)
                _uiState.update {
                    it.copy(
                        inputText = "",
                        isLoading = true,
                        errorMessage = null,
                        showErrorDialog = false,
                        retryAction = null
                    )
                }

                val responseText = if (!isHuggingFace) {
                    val request = OpenRouterRequest(
                        model = model,
                        messages = listOf(OpenRouterMessage(role = "user", content = inputText)),
                        maxTokens = 1000,
                        stream = false,
                        temperature = 0.7
                    )
                    val response = openRouterApiService.getChatCompletion("Bearer $apiKey", request)
                    (response["choices"] as? List<*>)
                        ?.firstOrNull()
                        ?.let { choice -> (choice as? Map<*, *>)?.get("message") }
                        ?.let { message -> (message as? Map<*, *>)?.get("content") as? String }
                        ?: throw IOException("No response received from OpenRouter")
                } else {
                    val request = HuggingFaceRequest(
                        inputs = inputText,
                        parameters = mapOf(
                            "max_new_tokens" to 100000,
                            "temperature" to 0.7
                        )
                    )
                    val url = "$apiEndpoint/models/$model"
                    val response = huggingFaceApiService.query("Bearer $apiKey", request, url)
                    response.body()?.firstOrNull()?.generated_text
                        ?: throw IOException("No response received from HuggingFace")
                }

                Log.d("ChatViewModel", "Response received: $responseText")
                val assistantMessage = ChatMessage(content = responseText, isUser = false)
                chatRepository.insertChatMessage(assistantMessage)
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: UnknownHostException) {
                Log.e("ChatViewModel", "No internet connection: ${e.message}", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "No internet connection. Please check your network and try again.",
                        showErrorDialog = true,
                        retryAction = { sendMessage() }
                    )
                }
            } catch (e: IOException) {
                Log.e("ChatViewModel", "Network error: ${e.message}", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Network error: ${e.message ?: "Unable to connect to the server"}. Please try again.",
                        showErrorDialog = true,
                        retryAction = { sendMessage() }
                    )
                }
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Unexpected error: ${e.message}", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Unexpected error: ${e.message ?: "Something went wrong"}. Please try again or check settings.",
                        showErrorDialog = true,
                        retryAction = null
                    )
                }
            }
        }
    }

    fun cancelProcessing() {
        Log.d("ChatViewModel", "Cancelling processing")
        _uiState.update {
            it.copy(
                isLoading = false,
                errorMessage = "Processing cancelled",
                showErrorDialog = true,
                retryAction = { sendMessage() }
            )
        }
    }

    fun clearError() {
        Log.d("ChatViewModel", "Clearing error")
        _uiState.update {
            it.copy(
                errorMessage = null,
                showErrorDialog = false,
                retryAction = null
            )
        }
    }

    fun clearMessages() {
        viewModelScope.launch {
            chatRepository.deleteAllChatMessages()
            _uiState.update { it.copy(errorMessage = null, showErrorDialog = false, retryAction = null) }
        }
    }
}