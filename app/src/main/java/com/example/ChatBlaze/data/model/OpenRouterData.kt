package com.example.ChatBlaze.data.model

import com.google.gson.annotations.SerializedName


data class OpenRouterRequest(
    @SerializedName("model") val model: String,
    @SerializedName("messages") val messages: List<OpenRouterMessage>,
    @SerializedName("stream") val stream: Boolean,
    @SerializedName("max_tokens") val maxTokens: Int?,
    @SerializedName("temperature") val temperature: Double
)
data class OpenRouterMessage(
    val role: String,
    val content: String
)

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

