package com.example.botchat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.botchat.api.HuggingFaceApiService
import com.example.botchat.api.OpenRouterApiService
import com.example.botchat.data.ChatMessage
import com.example.botchat.data.HuggingFaceRequest
import com.example.botchat.data.OpenRouterMessage
import com.example.botchat.data.OpenRouterRequest
import com.example.botchat.data.UserSettingsDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import android.util.Log
import com.example.botchat.Repository.ChatRepository
import com.example.botchat.database.ChatDatabase
import java.io.IOException
import kotlin.reflect.jvm.internal.impl.descriptors.Visibilities.Local

data class UiState(
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showErrorPage: Boolean = false
)

class ChatViewModel(
    private val settingsDataStore: UserSettingsDataStore,
    private val openRouterApiService: OpenRouterApiService = OpenRouterApiService.create(),
    private val huggingFaceApiService: HuggingFaceApiService = HuggingFaceApiService.create(),
   // private val chatRepository: ChatRepository = ChatRepository(ChatDatabase.getDatabase().chatMessageDao())
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun updateInputText(text: String) {
        _uiState.value = _uiState.value.copy(inputText = text, errorMessage = null)
    }

    fun sendMessage() {
        val inputText = _uiState.value.inputText.trim()
        if (inputText.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Message cannot be empty")
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
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "$provider API key is missing",
                        showErrorPage = true
                    )
                    return@launch
                }
                if (model.isBlank()) {
                    Log.e("ChatViewModel", "$provider model is missing")
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Please select a $provider model",
                        showErrorPage = true
                    )
                    return@launch
                }
                if (isHuggingFace && apiEndpoint.isBlank()) {
                    Log.e("ChatViewModel", "HuggingFace endpoint is missing")
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "HuggingFace endpoint is missing",
                        showErrorPage = true
                    )
                    return@launch
                }

                val userMessage = ChatMessage(content = inputText, isUser = true)
                _uiState.value = _uiState.value.copy(
                    messages = _uiState.value.messages + userMessage,
                    inputText = "",
                    isLoading = true,
                    errorMessage = null
                )

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
                        ?: ""
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
                    response.body()?.firstOrNull()?.generated_text ?: ""
                }

                Log.d("ChatViewModel", "Response received: $responseText")
                if (responseText.isNotBlank()) {
                    val assistantMessage = ChatMessage(content = responseText, isUser = false)
                    _uiState.value = _uiState.value.copy(
                        messages = _uiState.value.messages + assistantMessage,
                        isLoading = false
                    )
                } else {
                    Log.w("ChatViewModel", "Empty response from $provider")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "No response received from $provider"
                    )
                }
            } catch (e: IOException) {
                Log.e("ChatViewModel", "Network error: ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Network error: ${e.message}",
                    showErrorPage = true
                )
            } catch (e: Exception) {
                Log.e("ChatViewModel", "General error: ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error: ${e.message}",
                    showErrorPage = true
                )
            }
        }
    }

    fun cancelProcessing() {
        Log.d("ChatViewModel", "Cancelling processing")
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            errorMessage = "Processing cancelled"
        )
    }

    fun clearError() {
        Log.d("ChatViewModel", "Clearing error")
        _uiState.value = _uiState.value.copy(
            errorMessage = null,
            showErrorPage = false
        )
    }
}