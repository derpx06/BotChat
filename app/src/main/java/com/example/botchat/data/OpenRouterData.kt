package com.example.botchat.data

import com.google.gson.annotations.SerializedName

// Request
data class OpenRouterRequest(
    val model: String,
    val messages: List<OpenRouterMessage>,
    val temperature: Double? = 0.7,
    @SerializedName("max_tokens") val maxTokens: Int? = 500,
    @SerializedName("top_p") val topP: Double? = 1.0,
    @SerializedName("frequency_penalty") val frequencyPenalty: Double? = 0.0,
    @SerializedName("presence_penalty") val presencePenalty: Double? = 0.0
)

data class OpenRouterMessage(
    val role: String, // "user", "assistant", or "system"
    val content: String
)

// Response
data class OpenRouterResponse(
    val choices: List<Choice>?,
    val usage: UsageData?,
    val error: ErrorData?
)

data class Choice(
    val message: OpenRouterMessage,
    @SerializedName("finish_reason") val finishReason: String
)

data class UsageData(
    @SerializedName("prompt_tokens") val promptTokens: Int,
    @SerializedName("completion_tokens") val completionTokens: Int,
    @SerializedName("total_tokens") val totalTokens: Int
)

data class ErrorData(
    val message: String,
    val type: String?
)

// Model Info (for fetching available models)
data class OpenRouterModel(
    val id: String,
    val name: String,
    val description: String?,
    val pricing: PricingInfo,
    val context_length: Int
)

data class PricingInfo(
    val prompt: String, // Cost per 1k tokens
    val completion: String
)