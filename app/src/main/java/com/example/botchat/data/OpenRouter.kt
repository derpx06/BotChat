package com.example.botchat.data

// data/OpenRouterRequest.kt
data class OpenRouterRequest(
    val model: String,
    val messages: List<ChatMessage>,
    val temperature: Double = 0.7,
    val max_tokens: Int? = null,
    val top_p: Double = 1.0,
    val frequency_penalty: Double = 0.0,
    val presence_penalty: Double = 0.0
)

//data class ChatMessage(
//    val role: String, // "user", "assistant", or "system"
//    val content: String,
//    val name: String? = null
//)

// data/OpenRouterResponse.kt
data class OpenRouterResponse(
    val choices: List<Choice>,
    val usage: UsageData?
)

data class Choice(
    val message: ChatMessage,
    val finish_reason: String
)

data class UsageData(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int
)