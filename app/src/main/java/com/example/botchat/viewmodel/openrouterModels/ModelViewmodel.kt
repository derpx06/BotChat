package com.example.botchat.viewmodel.openrouterModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.botchat.api.OpenRouterApiService
import com.example.botchat.data.OpenRouterModel
import com.example.botchat.data.UserSettingsDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

sealed class ModelUIState{
    object Loading : ModelUIState()
    data class Success(val models: List<OpenRouterModel>) : ModelUIState()
    data class Error(val message: String) : ModelUIState()

}
class ModelViewModel(
    private val dataStore: UserSettingsDataStore
): ViewModel(){
    private val _uiState = MutableStateFlow<ModelUIState>(ModelUIState.Loading)
    val uiState: MutableStateFlow<ModelUIState> = _uiState
    init{
        fetchModels()
    }

    private fun fetchModels() {
        viewModelScope.launch {
            uiState.value = ModelUIState.Loading
            try {
                val models = OpenRouterApiService.create().getModels(
                    authHeader = "Bearer ${dataStore.getOpenRouterAPi}"
                )
                _uiState.value = ModelUIState.Success(models.data)

            }
            catch (e: Exception){
                _uiState.value = ModelUIState.Error("Failed to fetch models")
            }
        }

    }
    fun retry(){
        fetchModels()
    }
}