package com.example.ChatBlaze.ui.viewmodel.Chat

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.util.Log
import com.example.ChatBlaze.data.api.HuggingFaceApiService
import com.example.ChatBlaze.data.api.OpenRouterApiService
import com.example.ChatBlaze.data.database.ChatMessage
import com.example.ChatBlaze.data.database.ChatSession
import com.example.ChatBlaze.data.model.HuggingFaceRequest
import com.example.ChatBlaze.data.model.OpenRouterMessage
import com.example.ChatBlaze.data.model.OpenRouterRequest
import com.example.ChatBlaze.data.model.UserSettingsDataStore
import com.example.ChatBlaze.data.repository.ChatRepository
import com.example.ChatBlaze.data.api.LlmInferenceHelper
import com.example.ChatBlaze.data.downlaod.ModelDownloaderViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import java.io.IOException
import java.net.UnknownHostException

private const val MAX_CONTEXT_TOKENS = 4096

data class UiState(
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val isLoading: Boolean = false,
    val isModelLoading: Boolean = false,
    val errorMessage: String? = null,
    val showErrorDialog: Boolean = false,
    val retryAction: (() -> Unit)? = null,
    val currentSessionId: Long = 0L,
    val streamingMessage: String = ""
)

class ChatViewModel(
    private val settingsDataStore: UserSettingsDataStore,
    private val chatRepository: ChatRepository,
    private val modelDownloaderViewModel: ModelDownloaderViewModel,
    private val context: Context,
    private val openRouterApiService: OpenRouterApiService = OpenRouterApiService.create(),
    private val huggingFaceApiService: HuggingFaceApiService = HuggingFaceApiService.create()
) : ViewModel() {
    private val llmInferenceHelper: LlmInferenceHelper by lazy {
        LlmInferenceHelper(context, modelDownloaderViewModel)
    }
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    val allSessions: Flow<List<ChatSession>> = chatRepository.getAllChatSessions()
    private var streamingJob: Job? = null
    private val _messagesFlow = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messagesFlow: StateFlow<List<ChatMessage>> = _messagesFlow.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.collect { state ->
                chatRepository.getMessagesBySession(state.currentSessionId).collect { messages ->
                    Log.d("ChatViewModel", "Flow emitted for session ${state.currentSessionId}: ${messages.size} messages, Contents: ${messages.map { it.content }}")
                    _messagesFlow.value = messages
                    _uiState.update { it.copy(messages = messages) }
                }
            }
        }
    }

    private fun startNewSession() {
        Log.d("ChatViewModel", "Preparing new session, not saving yet")
        _uiState.update { it.copy(currentSessionId = 0L, messages = emptyList(), streamingMessage = "") }
        _messagesFlow.value = emptyList()
    }

    fun loadSession(sessionId: Long) {
        viewModelScope.launch {
            Log.d("ChatViewModel", "Loading session with ID: $sessionId")
            _uiState.update { it.copy(currentSessionId = sessionId, streamingMessage = "") }
            val messages = chatRepository.getMessagesBySession(sessionId).first()
            Log.d("ChatViewModel", "Messages for session $sessionId: ${messages.size}, Contents: ${messages.map { it.content }}")
            _messagesFlow.value = messages
            _uiState.update { it.copy(messages = messages) }
        }
    }

    fun deleteSession(sessionId: Long) {
        viewModelScope.launch {
            Log.d("ChatViewModel", "Deleting session with ID: $sessionId")
            chatRepository.deleteSession(sessionId)
            if (_uiState.value.currentSessionId == sessionId) {
                startNewSession()
            }
        }
    }

    fun updateInputText(text: String) {
        _uiState.update { it.copy(inputText = text, errorMessage = null, showErrorDialog = false) }
    }

    fun sendMessage(useStreaming: Boolean = true) {
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
                var sessionId = _uiState.value.currentSessionId
                if (sessionId == 0L) {
                    val sessionTitle = inputText.take(50)
                    val session = ChatSession(title = sessionTitle)
                    sessionId = chatRepository.insertChatSession(session)
                    Log.d("ChatViewModel", "New session created with ID: $sessionId, Title: $sessionTitle")
                    _uiState.update { it.copy(currentSessionId = sessionId) }
                }

                Log.d("ChatViewModel", "Sending message for session $sessionId: $inputText")
                val userMessage = ChatMessage(content = inputText, isUser = true, sessionId = sessionId)
                chatRepository.insertChatMessage(userMessage)
                Log.d("ChatViewModel", "User message inserted: $inputText")

                // Fetch the updated history to build context for the model
                val history = chatRepository.getMessagesBySession(sessionId).first()

                val messagesAfterUser = chatRepository.getMessagesBySession(sessionId).first()
                Log.d("ChatViewModel", "Messages after user message for session $sessionId: ${messagesAfterUser.size}, Contents: ${messagesAfterUser.map { it.content }}")
                _messagesFlow.value = messagesAfterUser
                _uiState.update {
                    it.copy(
                        inputText = "",
                        isLoading = false,
                        isModelLoading = false,
                        errorMessage = null,
                        showErrorDialog = false,
                        retryAction = null,
                        streamingMessage = "",
                        messages = messagesAfterUser
                    )
                }

                val provider = settingsDataStore.getSelectedProvider.first()
                Log.d("ChatViewModel", "Provider: $provider")

                if (provider == "local") {
                    val selectedModelId = settingsDataStore.getSelectedLocalModel.first()
                    if (selectedModelId.isEmpty()) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "No local model selected",
                                showErrorDialog = true,
                                retryAction = null
                            )
                        }
                        return@launch
                    }

                    _uiState.update { it.copy(isModelLoading = true) }
                    try {
                        llmInferenceHelper.loadModelAndPrepareInterpreter(selectedModelId)
                        _uiState.update { it.copy(isModelLoading = false, isLoading = true) }
                    } catch (e: Exception) {
                        Log.e("ChatViewModel", "Local model loading error: ${e.message}", e)
                        _uiState.update {
                            it.copy(
                                isModelLoading = false,
                                errorMessage = "Failed to load model: ${e.message}",
                                showErrorDialog = true,
                                retryAction = { sendMessage(useStreaming) }
                            )
                        }
                        return@launch
                    }

                    if (useStreaming) {
                        streamingJob?.cancel()
                        streamingJob = viewModelScope.launch {
                            try {
                                val prompt = buildLocalModelPrompt(history)
                                llmInferenceHelper.generateResponseStream(prompt).collect { chunk ->
                                    if (chunk.isNotEmpty()) {
                                        _uiState.update {
                                            it.copy(
                                                streamingMessage = it.streamingMessage + chunk,
                                                messages = _messagesFlow.value
                                            )
                                        }
                                    }
                                }
                                val fullResponse = _uiState.value.streamingMessage
                                val assistantMessage = ChatMessage(content = fullResponse, isUser = false, sessionId = sessionId)
                                chatRepository.insertChatMessage(assistantMessage)
                                Log.d("ChatViewModel", "Assistant message inserted: $fullResponse")
                                val messagesAfterAssistant = chatRepository.getMessagesBySession(sessionId).first()
                                _messagesFlow.value = messagesAfterAssistant
                                _uiState.update {
                                    it.copy(
                                        isLoading = false,
                                        streamingMessage = "",
                                        messages = messagesAfterAssistant
                                    )
                                }
                                loadSession(sessionId)
                            } catch (e: Exception) {
                                Log.e("ChatViewModel", "Local streaming error: ${e.message}", e)
                                _uiState.update {
                                    it.copy(
                                        isLoading = false,
                                        errorMessage = "Local inference error: ${e.message}",
                                        showErrorDialog = true,
                                        retryAction = { sendMessage(useStreaming) }
                                    )
                                }
                            }
                        }
                    } else {
                        // Non-streaming logic for local model would go here
                    }
                    return@launch
                }

                _uiState.update { it.copy(isLoading = true) }
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
                val apiEndpoint = if (isHuggingFace) settingsDataStore.getApiEndpoint.first() else "https://api.openrouter.ai/api/v1/"

                Log.d("ChatViewModel", "API Endpoint: $apiEndpoint, Model: $model")

                if (apiKey.isEmpty() || model.isEmpty() || (isHuggingFace && apiEndpoint.isEmpty())) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Missing API key, model, or endpoint",
                            showErrorDialog = true,
                            retryAction = null
                        )
                    }
                    return@launch
                }

                if (!isHuggingFace && useStreaming) {
                    streamingJob?.cancel()
                    streamingJob = viewModelScope.launch {
                        val request = OpenRouterRequest(
                            model = model,
                            messages = buildOpenRouterHistory(history),
                            maxTokens = 5000,
                            stream = true,
                            temperature = 0.7
                        )
                        var fullResponse = ""
                        try {
                            OpenRouterApiService.createStreamingClient(apiKey, request).collect { chunk ->
                                if (chunk.isNotEmpty()) {
                                    fullResponse += chunk
                                    Log.d("ChatViewModel", "Streaming chunk received: $chunk, Full: $fullResponse")
                                    _uiState.update {
                                        it.copy(
                                            streamingMessage = fullResponse,
                                            messages = _messagesFlow.value
                                        )
                                    }
                                }
                            }
                            val assistantMessage = ChatMessage(content = fullResponse, isUser = false, sessionId = sessionId)
                            chatRepository.insertChatMessage(assistantMessage)
                            Log.d("ChatViewModel", "Assistant message inserted: $fullResponse")
                            val messagesAfterAssistant = chatRepository.getMessagesBySession(sessionId).first()
                            Log.d("ChatViewModel", "Messages after assistant message for session $sessionId: ${messagesAfterAssistant.size}, Contents: ${messagesAfterAssistant.map { it.content }}")
                            _messagesFlow.value = messagesAfterAssistant
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    streamingMessage = "",
                                    messages = messagesAfterAssistant
                                )
                            }
                            loadSession(sessionId)
                        } catch (e: Exception) {
                            Log.e("ChatViewModel", "Streaming error: ${e.message}", e)
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = "Streaming error: ${e.message}",
                                    showErrorDialog = true,
                                    retryAction = { sendMessage(useStreaming) }
                                )
                            }
                        }
                    }
                } else {
                    val responseText = if (!isHuggingFace) {
                        val request = OpenRouterRequest(
                            model = model,
                            messages = buildOpenRouterHistory(history),
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
                            inputs = buildLocalModelPrompt(history),
                            parameters = mapOf(
                                "max_new_tokens" to 100000,
                                "temperature" to 0.7
                            )
                        )
                        val url = "$apiEndpoint/models/$model"
                        val response = huggingFaceApiService.query("Bearer $apiKey", request, url)
                        val fullText = response.body()?.firstOrNull()?.generated_text
                            ?: throw IOException("No response received from HuggingFace")
                        if (useStreaming) {
                            fullText.split(" ").forEach { word ->
                                _uiState.update { it.copy(streamingMessage = it.streamingMessage + word + " ") }
                                delay(50)
                            }
                        }
                        fullText
                    }
                    val assistantMessage = ChatMessage(content = responseText, isUser = false, sessionId = sessionId)
                    chatRepository.insertChatMessage(assistantMessage)
                    Log.d("ChatViewModel", "Assistant message inserted: $responseText")
                    val messagesAfterAssistant = chatRepository.getMessagesBySession(sessionId).first()
                    Log.d("ChatViewModel", "Messages after assistant message for session $sessionId: ${messagesAfterAssistant.size}, Contents: ${messagesAfterAssistant.map { it.content }}")
                    _messagesFlow.value = messagesAfterAssistant
                    _uiState.update { it.copy(isLoading = false, streamingMessage = "", messages = messagesAfterAssistant) }
                    loadSession(sessionId)
                }
            } catch (e: UnknownHostException) {
                Log.e("ChatViewModel", "No internet connection: ${e.message}", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "No internet connection. Please check your network and try again.",
                        showErrorDialog = true,
                        retryAction = { sendMessage(useStreaming) }
                    )
                }
            } catch (e: IOException) {
                Log.e("ChatViewModel", "Network error: ${e.message}", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Network error: ${e.message ?: "Unable to connect to the server"}. Please try again.",
                        showErrorDialog = true,
                        retryAction = { sendMessage(useStreaming) }
                    )
                }
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Unexpected error: ${e.message}", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Unexpected error: ${e.message ?: "Something went wrong"}. Please try again or check settings.",
                        showErrorDialog = true,
                        retryAction = { sendMessage(useStreaming) }
                    )
                }
            }
        }
    }

    fun cancelProcessing() {
        streamingJob?.cancel()
        _uiState.update { it.copy(isLoading = false, streamingMessage = "", isModelLoading = false) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null, showErrorDialog = false, retryAction = null) }
    }

    fun clearMessages() {
        viewModelScope.launch {
            Log.d("ChatViewModel", "Clearing all sessions and messages")
            chatRepository.deleteAllSessions()
            startNewSession()
        }
    }

    fun startNewChat() {
        startNewSession()
    }

    private fun buildLocalModelPrompt(history: List<ChatMessage>): String {
        return buildPrompt(
            history = history,
            toString = { messages ->
                buildString {
                    append("You are a helpful AI assistant.\n\n")
                    messages.forEach { message ->
                        append(if (message.isUser) "User: ${message.content}\n" else "Assistant: ${message.content}\n")
                    }
                    append("Assistant:")
                }
            }
        )
    }

    private fun buildOpenRouterHistory(history: List<ChatMessage>): List<OpenRouterMessage> {
        return buildPrompt(
            history = history,
            toString = { messages ->
                val openRouterMessages = mutableListOf<OpenRouterMessage>()
                openRouterMessages.add(OpenRouterMessage(role = "system", content = "You are a helpful AI assistant."))
                messages.forEach {
                    openRouterMessages.add(OpenRouterMessage(role = if (it.isUser) "user" else "assistant", content = it.content))
                }
                openRouterMessages
            }
        )
    }

    private fun <T> buildPrompt(history: List<ChatMessage>, toString: (List<ChatMessage>) -> T): T {
        val mutableHistory = history.toMutableList()
        var tokenCount = mutableHistory.sumOf { it.content.length / 4 }

        while (tokenCount > MAX_CONTEXT_TOKENS && mutableHistory.size > 2) {
            mutableHistory.removeAt(1)
            tokenCount = mutableHistory.sumOf { it.content.length / 4 }
        }

        return toString(mutableHistory)
    }
}