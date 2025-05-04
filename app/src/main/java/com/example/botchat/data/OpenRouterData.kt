package com.example.botchat.data

import com.google.gson.annotations.SerializedName


data class OpenRouterRequest(
    @SerializedName("model") val model: String,
    @SerializedName("messages") val messages: List<OpenRouterMessage>,
    @SerializedName("stream") val stream: Boolean,
    @SerializedName("max_tokens") val maxTokens: Int?,
    @SerializedName("temperature") val temperature: Double
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