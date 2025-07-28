package com.example.ChatBlaze.data.model

// Grok
data class GrokRequest(
    val model: String,
    val messages: List<GrokMessage>,
    val max_tokens: Int = 1000,
    val temperature: Double = 0.7,
    val stream: Boolean = false
)

data class GrokMessage(
    val role: String,
    val content: String
)

data class GrokResponse(
    val choices: List<GrokChoice>
)

data class GrokChoice(
    val message: GrokMessage
)

// Gemini
data class GeminiRequest(
    val contents: List<GeminiContent>,
    val generationConfig: GeminiConfig
)

data class GeminiContent(
    val parts: List<GeminiPart>
)

data class GeminiPart(
    val text: String
)

data class GeminiConfig(
    val maxOutputTokens: Int = 1000,
    val temperature: Double = 0.7
)

data class GeminiResponse(
    val candidates: List<GeminiCandidate>
)

data class GeminiCandidate(
    val content: GeminiContent
)

// Anthropic
data class AnthropicRequest(
    val model: String,
    val messages: List<AnthropicMessage>,
    val max_tokens: Int = 1000,
    val temperature: Double = 0.7
)

data class AnthropicMessage(
    val role: String,
    val content: String
)

data class AnthropicResponse(
    val content: List<AnthropicContent>
)

data class AnthropicContent(
    val text: String
)

// Deepseek
data class DeepseekRequest(
    val model: String,
    val messages: List<DeepseekMessage>,
    val max_tokens: Int = 1000,
    val temperature: Double = 0.7,
    val stream: Boolean = false
)

data class DeepseekMessage(
    val role: String,
    val content: String
)

data class DeepseekResponse(
    val choices: List<DeepseekChoice>
)

data class DeepseekChoice(
    val message: DeepseekMessage
)