package com.example.ChatBlaze.data

import com.google.gson.annotations.SerializedName

data class OpenRouterModel(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("created") val created: Long?,
    @SerializedName("description") val description: String?,
    @SerializedName("context_length") val contextLength: Int?,
    @SerializedName("architecture") val architecture: Architecture?,
    @SerializedName("pricing") val pricing: Pricing?,
    @SerializedName("top_provider") val topProvider: TopProvider?,
    @SerializedName("per_request_limits") val perRequestLimits: Any?,
    @SerializedName("supported_parameters") val supportedParameters: List<String>?
){
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
}

data class Architecture(
    @SerializedName("modality") val modality: String?,
    @SerializedName("input_modalities") val inputModalities: List<String>?,
    @SerializedName("output_modalities") val outputModalities: List<String>?,
    @SerializedName("tokenizer") val tokenizer: String?,
    @SerializedName("instruct_type") val instructType: String?
)

data class Pricing(
    @SerializedName("prompt") val prompt: String?,
    @SerializedName("completion") val completion: String?,
    @SerializedName("request") val request: String?,
    @SerializedName("image") val image: String?,
    @SerializedName("web_search") val webSearch: String?,
    @SerializedName("internal_reasoning") val internalReasoning: String?
)

data class TopProvider(
    @SerializedName("context_length") val contextLength: Int?,
    @SerializedName("max_completion_tokens") val maxCompletionTokens: Int?,
    @SerializedName("is_moderated") val isModerated: Boolean?
)