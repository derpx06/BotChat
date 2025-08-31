package com.example.ChatBlaze.ui.viewmodel.Chat

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ChatBlaze.data.api.HuggingFaceApiService
import com.example.ChatBlaze.data.api.LlmInferenceHelper
import com.example.ChatBlaze.data.api.OpenRouterApiService
import com.example.ChatBlaze.data.database.ChatMessage
import com.example.ChatBlaze.data.database.ChatSession
import com.example.ChatBlaze.data.model.HuggingFaceRequest
import com.example.ChatBlaze.data.model.OpenRouterMessage
import com.example.ChatBlaze.data.model.OpenRouterRequest
import com.example.ChatBlaze.data.model.UserSettingsDataStore
import com.example.ChatBlaze.data.repository.ChatRepository
import com.example.ChatBlaze.data.downlaod.ModelDownloaderViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.UnknownHostException

private const val MAX_CONTEXT_TOKENS = 4096

enum class FileType { IMAGE, PDF, OTHER }

data class SelectedFile(
    val uri: Uri,
    val type: FileType,
    val name: String
)

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

    private val _selectedFiles = MutableStateFlow<List<SelectedFile>>(emptyList())
    val selectedFiles: StateFlow<List<SelectedFile>> = _selectedFiles.asStateFlow()

    val allSessions: Flow<List<ChatSession>> = chatRepository.getAllChatSessions()
    private var streamingJob: Job? = null
    private val _messagesFlow = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messagesFlow: StateFlow<List<ChatMessage>> = _messagesFlow.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.collect { state ->
                chatRepository.getMessagesBySession(state.currentSessionId).collect { messages ->
                    _messagesFlow.value = messages
                    _uiState.update { it.copy(messages = messages) }
                }
            }
        }
    }

    private fun startNewSession() {
        _uiState.update { it.copy(currentSessionId = 0L, messages = emptyList(), streamingMessage = "") }
        _messagesFlow.value = emptyList()
    }

    fun loadSession(sessionId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(currentSessionId = sessionId, streamingMessage = "") }
            val messages = chatRepository.getMessagesBySession(sessionId).first()
            _messagesFlow.value = messages
            _uiState.update { it.copy(messages = messages) }
        }
    }

    fun deleteSession(sessionId: Long) {
        viewModelScope.launch {
            chatRepository.deleteSession(sessionId)
            if (_uiState.value.currentSessionId == sessionId) {
                startNewSession()
            }
        }
    }

    fun updateInputText(text: String) {
        _uiState.update { it.copy(inputText = text, errorMessage = null, showErrorDialog = false) }
    }

    fun addFiles(newFiles: List<SelectedFile>) {
        _selectedFiles.update { currentFiles ->
            currentFiles + newFiles.filterNot { newFile -> currentFiles.any { it.uri == newFile.uri } }
        }
    }

    fun removeFile(fileToRemove: SelectedFile) {
        _selectedFiles.update { currentFiles ->
            currentFiles.filterNot { it.uri == fileToRemove.uri }
        }
    }

    fun sendMessage(useStreaming: Boolean = true) {
        val inputText = _uiState.value.inputText.trim()
        val inputFiles = _selectedFiles.value

        if (inputText.isBlank() && inputFiles.isEmpty()) {
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
                    val sessionTitle = inputText.take(50).ifBlank { "New Chat" }
                    val session = ChatSession(title = sessionTitle)
                    sessionId = chatRepository.insertChatSession(session)
                    _uiState.update { it.copy(currentSessionId = sessionId) }
                }

                val attachmentUris = inputFiles.map { it.uri.toString() }
                val userMessage = ChatMessage(
                    content = inputText,
                    isUser = true,
                    sessionId = sessionId,
                    attachmentUris = attachmentUris
                )
                chatRepository.insertChatMessage(userMessage)

                val history = chatRepository.getMessagesBySession(sessionId).first()

                val messagesAfterUser = chatRepository.getMessagesBySession(sessionId).first()
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
                _selectedFiles.value = emptyList()

                val provider = settingsDataStore.getSelectedProvider.first()

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
                    val messagesAfterAssistant = chatRepository.getMessagesBySession(sessionId).first()
                    _messagesFlow.value = messagesAfterAssistant
                    _uiState.update { it.copy(isLoading = false, streamingMessage = "", messages = messagesAfterAssistant) }
                    loadSession(sessionId)
                }
            } catch (e: UnknownHostException) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "No internet connection. Please check your network and try again.",
                        showErrorDialog = true,
                        retryAction = { sendMessage(useStreaming) }
                    )
                }
            } catch (e: IOException) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Network error: ${e.message ?: "Unable to connect to the server"}. Please try again.",
                        showErrorDialog = true,
                        retryAction = { sendMessage(useStreaming) }
                    )
                }
            } catch (e: Exception) {
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