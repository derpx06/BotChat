package com.example.botchat.viewmodel.openrouterModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.botchat.api.OpenRouterApiService
import com.example.botchat.data.OpenRouterModel
import com.example.botchat.data.UserSettingsDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ModelUIState {
    object Loading : ModelUIState()
    data class Success(val models: List<OpenRouterModel>) : ModelUIState()
    data class Error(val message: String) : ModelUIState()
}

class ModelViewModel(
    private val dataStore: UserSettingsDataStore
) : ViewModel() {
    private val _uiState = MutableStateFlow<ModelUIState>(ModelUIState.Loading)
    val uiState: StateFlow<ModelUIState> = _uiState.asStateFlow()

    init {
        fetchModels()
    }

    private fun fetchModels() {
        viewModelScope.launch {
            _uiState.value = ModelUIState.Loading
            try {
                val models = OpenRouterApiService.create().getModels(
                    authHeader = "Bearer ${dataStore.getOpenRouterAPi}"
                )
                _uiState.value = ModelUIState.Success(models.data)
            } catch (e: Exception) {
                _uiState.value = ModelUIState.Error("Failed to fetch models")
            }
        }
    }
    fun estimateParameters(model: OpenRouterModel): Long {
        val contextLength = model.contextLength ?: 8000
        val name = model.name.lowercase()
        return when {
            name.contains("405b") || contextLength > 128000 -> 405_000_000_000
            name.contains("70b") || contextLength > 32000 -> 70_000_000_000
            name.contains("8x22b") || contextLength > 16000 -> 22_000_000_000
            name.contains("8b") || contextLength > 8000 -> 8_000_000_000
            else -> 1_000_000_000
        }
    }

    fun formatParameters(parameters: Long): String {
        return when {
            parameters >= 1_000_000_000 -> "${parameters / 1_000_000_000}B"
            parameters >= 1_000_000 -> "${parameters / 1_000_000}M"
            else -> "$parameters"
        }
    }


    fun retry() {
        fetchModels()
    }
}